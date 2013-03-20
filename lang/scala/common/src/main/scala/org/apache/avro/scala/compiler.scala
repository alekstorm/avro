package org.apache.avro.scala

import shapeless._

import org.apache.avro.{Schema => JSchema}
import org.apache.avro.generic.GenericData

import code._

// TODO(alek): generate @Deprecated annotations for aliases
// TODO(alek): propose friendlier syntax for scala 3.0 like "trait X mixin Y extends Z" (looks weird as "trait X mixin Y with Z extends A with B" or "trait[Y] X extends Z"
// TODO(alek): avro's Encoder shouldn't be Flushable; flushable subclasses should be
// TODO(alek): propose ditching BufferedBinaryEncoder, since it duplicates functionality in BufferedOutputStream
// TODO(alek): make into a plugin
// TODO(alek): report scaladoc bug that lists return types inferred from varargs as Foo*, rather than Seq[Foo] - for example, def foo(a: Int*) = a
package object compiler {
  def fileWrap(contents: String): String = {
    code"""
      // This file is machine-generated.

      import $ref.jschema._
      import $ref.union.|
      import _root_.shapeless.::
      import _root_.shapeless.Record._

      $contents
    """ + "\n"
  }

  // TODO(alek): generate warnings if scala-fied identifiers are being mangled to become legal scala, add annotation to choose custom name
  // TODO(alek): never mind, backquote everything (what about contained backquotes?) - add option to auto-mangle to ASCII/Unicode printable chars
  // TODO(alek): never mind again, backquoted identifiers interpret backslashes as escape sequences - need to handle better (talk to scala devs - proposed syntax: all characters between two backticks (unescaped), except double backticks become single backticks (don't use backslash, since it would need to be escaped too)) - prefix, e.g. r`id` - r() defined as macro (analogous to strings with normal methods)
  // TODO(alek): use scala.reflect.api.Trees
  // TODO(alek): use dynamic types for generic records
  // TODO(alek): "doc" elements
  // TODO(alek): support nested records
  // TODO(alek): json records, actor (pattern matching) protocols, builders, user-chosen record field classes, futures, promises
  // TODO(alek): lenses/zippers
  // TODO(alek): generic protocols, as well as records
  // TODO(alek): parameter-level annotation for out-of-band information (become implicits in Scala; can also be passed through threadlocals, normal parameters (for generators that don't know the annotation), stateful client objects, etc)
  def packageWrap(namespace: Option[String], contents: String): String = {
    // FIXME(alek): namespace doesn't handle dots in backticks
    // TODO(alek): different IntelliJ highlighting for various string processors (randomly chosen, but project-wide for consistency, and can be configured manually)
    namespace match {
      case Some(x) => code"""
        package $x {
          $contents
        }
        """
      case None => contents
    }
  }

  def getRecords(schema: Schema): Set[RecordBaseSchema] = { // TODO(alek): other types
    schema match {
      case record: RecordBaseSchema => record.fields.flatMap(field => getRecords(field.value)).toSet + record
      case UnionSchema(types) => types.flatMap(getRecords).toSet
      case ArraySchema(elements) => getRecords(elements)
      case MapSchema(values) => getRecords(values)
      case _ => Set()
    }
  }

  def getUnions(schema: Schema): Set[UnionSchema] = {
    schema match {
      case record: RecordBaseSchema => record.fields.flatMap(field => getUnions(field.value)).toSet
      case union: UnionSchema => Set(union)
      case ArraySchema(elements) => getUnions(elements)
      case MapSchema(values) => getUnions(values)
      case _ => Set()
    }
  }

  def getTypes(schema: Schema): Set[Schema] = {
    (schema match {
      case record: RecordBaseSchema => record.fields.flatMap(field => getTypes(field.value)).toSet + record
      case union: UnionSchema => union.types.flatMap(getTypes).toSet + union
      case array: ArraySchema => getTypes(array.elements) + array
      case map: MapSchema => getTypes(map.values) + map
      case other => Set(other)
    }).toSet
  }

  def getRecordParams(schemas: Seq[Schema]): (Seq[String], Seq[String]) = {
    val recordFields = schemas.flatMap(getRecords).toSet
    val recordParams = recordFields.map(schema => (s"T${"$"}${schema.name}", (if (schema.isInstanceOf[ErrorSchema]) " <: Throwable" else "") + s" : ({ type L[X] = $ref.generated.PresenterAux[X, $ref.RecordThing[${schema.name}${"$"}Record.Descriptor]] })#L : _root_.scala.reflect.ClassTag")).toMap
    val unionParams = schemas.flatMap(getUnions).toSet.map((union: UnionSchema) => ("U$" + union.types.map(_.unionType).sorted.mkString("$"), if (union.types.size > 1) " : (" + union.types.map(_.presenterType).mkString(" | ") + ")#L" else "")).filter(_._2.length > 0).toMap
    val params = recordParams ++ unionParams
    (params.keys.toSeq, params.map { case (key, value) => key + value }.toSeq)
  }

  def toTypeParams(params: Traversable[String]) = if (params.size > 0) s"[${params.mkString(", ")}]" else ""

  def toHList(items: Seq[String], newline: Boolean = true): String = (items :+ "_root_.shapeless.HNil").mkString(s" ::${if (newline) "\n" else " "}")

  def toHMap(items: Seq[(String, String)], newline: Boolean = true): String = toHList(items.toSeq.map { case (k, v) => s"($k, ($v))" }, newline)

  def compileSchema(schema: Schema, wrap: Boolean = true): String = fileWrap(compileSchemaAux(schema, wrap))

  def phantom(schema: Schema, name: String) = s"$ref.ValueCodec[$name, ${schema.descriptorType}]"

  def codecParam(schemas: Seq[Schema], typeclasses: Seq[String] = Seq()) = (s"C${"$"} <: $ref.Codec[C${"$"}]" +: (typeclasses ++ schemas.toSet.toSeq.map((schema: Schema) => s"({ type L[X <: $ref.Codec[C${"$"}]] = ${phantom(schema, "X")} })#L"))).mkString(" : ")

  def getCodec(schema: Schema) = s"implicitly[${phantom(schema, "C$")}]"

  val ref = "_root_.org.apache.avro.scala"

  def createObject(jSchema: JSchema, attrs: Map[String, AnyRef]): AnyRef = {
    val record = new GenericData.Record(jSchema)
    attrs.foreach { case (key, value) => record.put(key, value) }
    record
  }

  def compileSchemaAux(schema: Schema, wrap: Boolean = true): String = {
    schema match {
      case UnionSchema(types) => types.map(compileSchemaAux(_)).mkString("\n\n")
      case EnumSchema(name, symbols, namespace) => {
        val source = code"""
          object $name extends Enumeration {
            type $name = Value
            val ${symbols.mkString(", ")} = Value
        }"""
        if (wrap) packageWrap(namespace, source) else source
      }
      case fieldsSchema: RecordBaseSchema => {
        // TODO(alek): have intellij underline in pale green interpolated parts of strings
        // TODO(alek): configurable encoder implementation
        // TODO(alek): return either String or OutputStream implementation, depending on type requirements
        // TODO(alek): http://docs.scala-lang.org/overviews/macros/typemacros.html
        // TODO(alek): generate code with treehugger or something actually readable (use macros - scala"") - handle all escaping, checking, etc (type checking? - optional; things have to be on classpath - for only some things, then?) - could actually be used by other macros - also add ability to generate unique identifiers with %uniq(foo) (substitution is still ${bar}) - include pretty-printing, automatically moving imports to top-level with %import(baz), %extends(array) -> "extends A with B with ...", backticking IDs that conflict with %id(foo), expansion of javadoc objects with %javadoc(foo) - whole thing returns not a String, but a SyntaxTree (or whatever the scalac library calls it) - might have to use our own tree repr that can be converted to scalac's later, function inlining via %inline - so code sections used multiple times can be type-checked
        // TODO(alek): scala 3.0 - replace tuples with HLists (functions with currying), class -> HMap abstraction (eliminates most reflection), throw out classes completely, compile traits to concrete or abstract classes depending on whether they have abstract members, unify 'package' and 'package object', allow pretty much anything at top-level
        // TODO(alek): schema library based on case classes
        // TODO(alek): propose @nullable annotation (or similar), since a type of "null" doesn't make any sense, ask them to explicitly prohibit zero- and single-type unions
        // TODO(alek): escape $ and " in string processors with \
        // TODO(alek): nested records as nested classes
        // TODO(alek): anonymous records?
        // TODO(alek): override polymorphic methods, e.g. trait A { def foo[T]: T }; class B extends A { override def foo[String]: String = "" }
        // TODO(alek): "polymorphic composition"
        // TODO(alek): override case class equals, hashCode, etc to handle different implementations of record types
        // TODO(alek): have codec accept/return record type (HMap), provide json, binary, etc implementations of serializers, since java API sucks - also allows records to be abstracted over while maintaining type safety (for example, comparing two arbitrary records for equality, or compressing/encrypting all fields of a record)
        // TODO(alek): annotation to format toString output a certain way (using s"${foo}" notation?), or to call a certain function
        // TODO(alek): propose top-level 'type' declarations
        // TODO(alek): propose making constructor syntactic sugar/magic for creating a companion object 'new' method (which can't be explicitly defined through normal syntax) - possibly also replace alternative constructors with explicitly defining new() overloads on companion object - would also (mostly) eliminate need for/replace (Class)Manifest, and solve problem with constructor default parameters referring to methods
        // TODO(alek): wrapper for FileReader
        val traits = Seq(if (fieldsSchema.isInstanceOf[ErrorSchema]) Some("Throwable") else None/*, Option(schema.getProp("scalaTrait"))*/).flatten.mkString(" with ")
        val `extends` = if (traits.length > 0) s"\n    extends $traits" else "" // TODO(alek): support multiple traits
        val pair = getRecordParams(fieldsSchema.fields.map(_.value))
        val typeParams = toTypeParams(pair._1)
        val typeDeclParams = toTypeParams(pair._2)
        val codecTypeDeclParams = toTypeParams(codecParam(Seq(fieldsSchema)) +: pair._2)
        val className = fieldsSchema.name
        val record = fieldsSchema.name + "$Record"
        //val enums = fieldsSchema.fields.map(_.value).filter(_.isInstanceOf[EnumSchema]).map(compileSchemaAux(_, false))
        def compileField(field: Field): String = {
          // TODO(alek): figure out default values for polymorphic record types
          val default = "" //if (field.defaultValue != null) " = " + compileDefaultValue(field.schema, field.defaultValue) else ""
          code"${field.name}: ${field.value.presenterType}$default"
        }
        // FIXME(alek): move inner enums inside case class
        val source = code"""
          object $record {
            ${fieldsSchema.fields.map { field => code"""
              object ${field.name} extends _root_.shapeless.Field[${field.value.descriptorType}] {
                implicit def singleton = new $ref.Singleton[${field.name}.type] {
                  val instance = ${field.name}
                }
              }
            """ }.mkString("\n")}

            type Descriptor = ${toHMap(fieldsSchema.fields.map(field => (s"${field.name}.type", field.value.descriptorType)))}

            val schema${"$"} = $ref.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse(${"\"\"\""}${schema.jSchema}${"\"\"\""}))
          }

          case class $className$typeDeclParams(${fieldsSchema.fields.map(compileField(_)).mkString(", ")})${`extends`}

          object $className {
            implicit def presenter$typeDeclParams: $ref.generated.PresenterAux[$className$typeParams, $ref.RecordThing[$record.Descriptor]] = new $ref.generated.Presenter[$className$typeParams] {
              type R = $ref.RecordThing[$record.Descriptor]
              override def to(repr: $className$typeParams): R = new $ref.RecordThing[$record.Descriptor](${toHMap(fieldsSchema.fields.map(field => (s"$record.${field.name}", field.value.fromPresenter("repr." + field.name))))})

              override def from(record: R): $className$typeParams = $className$typeParams(${fieldsSchema.fields.map(field => field.value.toPresenter(s"record.instance.get($record.${field.name})")).mkString(", ")})
            }

            def encode$codecTypeDeclParams(repr: $className$typeParams): Iterator[Byte] = {
              ${getCodec(fieldsSchema)}.encode(presenter.to(repr))
            }

            def decode$codecTypeDeclParams(record: Iterator[Byte]): $className$typeParams = {
              presenter.from(${getCodec(fieldsSchema)}.decode(record))
            }
          }"""
        if (wrap) packageWrap(fieldsSchema.namespace, source) else source
      }
    }
  }

  def messageFunc(message: Message, body: String) = s"(${message.parameters.map(param => s"${param.name}: ${param.value.descriptorType}").mkString(", ")}) => $body"

  // TODO(alek): codecs for protobuf, thrift, etc (see java impl)
  def compileProtocol(protocol: Protocol): String = {
    // TODO(alek): covariance/contravariance can't be expressed this way (for function parameters/return types)
    // TODO(alek): companion object factory for protocol handler objects that allows us to use abstract type members instead of type parameters (implicits specified on the factory method)
    def compileMessage(message: Message): String = {
      val errors = message.errors.map(_.presenterType).mkString(", ")
      val throws = if (errors.length > 0) s"@throws[$errors]\n" else "" // TODO(alek): fix newline
      val params = message.parameters.map(param => s"${param.name}: ${param.value.presenterType}").mkString(", ")
      code"$throws def ${message.name}($params): ${message.response.presenterType}"
    }

    def messageType(message: Message) = s"(${message.parameters.map(parameter => parameter.value.descriptorType).mkString(", ")}) => ${message.response.descriptorType}"

    val protocolName = protocol.name + "$Protocol"
    val messageSchemas = protocol.messages.flatMap(message => message.parameters.map(_.value) ++ message.errors :+ message.response).toSeq
    val pair = getRecordParams(messageSchemas)
    val typeParams = toTypeParams(pair._1)
    val typeDeclParams = toTypeParams(pair._2)                                                        //.flatMap(getTypes)
    val serverTypeDeclParams = toTypeParams(Seq(codecParam(messageSchemas)))
    val clientTypeDeclParams = toTypeParams(codecParam(messageSchemas, Seq(s"({ type L[X] = $ref.Client[$protocolName, X] })#L")) +: pair._2)
    val descriptor = s"$protocolName.Descriptor"
    // TODO(alek): default type parameters (type members, but talk to scala devs about using them in constructors)
    // TODO(alek): handle void return types
    // TODO(alek): ask scala devs why `object TypeMap { def apply(x: Int) }; Seq(5,6).map(TypeMap _)` doesn't work
    // TODO(alek): ask whether errors can be returned normally from messages (or accepted as parameters)
    // TODO(alek): propose creating @error annotation on records, rather than completely separate type
    // TODO(alek): HList of monad operations - abstract the logic below like it was for map (hnil === return?)
    // TODO(alek): teach Scala 3 to infer class type parameters/members from their usage in other members' type signatures (function parameter/return types, etc)
    // TODO(alek): report bug that type members can't be used in constructor parameters
    // TODO(alek): see if you can enforce Names being an HSet
    // TODO(alek): Poly2.tupled (for pattern-matching on parameters, etc)
    // TODO(alek): generate runtime manifest that witnesses presence of annotations on classes - AnnotationManifest[T, A <: Annotation]
    // TODO(alek): pluggable code generators for presenters
    fileWrap(packageWrap(protocol.namespace, code"""
      ${protocol.declarations.map(compileSchemaAux(_, wrap = false)).mkString("\n\n")}

      trait $protocolName extends $ref.ProtocolThing {
        type Descriptor = ${toHMap(protocol.messages.map(message => (s"$protocolName.${message.name}.type", messageType(message))))}
      }

      object $protocolName {
        ${protocol.messages.map(message => s"object ${message.name} extends _root_.shapeless.Field[(${message.parameters.map(parameter => parameter.value.descriptorType).mkString(", ")}) => ${message.response.descriptorType}]").mkString("\n")}

        implicit def server$serverTypeDeclParams = new $ref.Server[$protocolName, C${"$"}] {
          override final val handlers = Map(${protocol.messages.map { message =>
            val params = s"impl.get(${message.name}).apply(${message.parameters.zipWithIndex.map { case (param, idx) => s"${getCodec(param.value)}.decode(parameters($idx))" }.mkString(", ")})"
            val call = s"${getCodec(message.response)}.encode($params)"
            s""""${message.name}" -> { (impl: $protocolName#Descriptor, parameters: Seq[Iterator[Byte]]) => $call }"""
          }.mkString(", ")})
          override final val protocol = protocol${"$"}
      }

        implicit def client$serverTypeDeclParams = new $ref.Client[$protocolName, C${"$"}] {
          ${protocol.messages.map(message => s"""val ${message.name}${"$"} = protocol${"$"}.messages.filter(_.name == "${message.name}").head""").mkString("\n")}

          override final def senders(transceiver: _root_.org.apache.avro.ipc.Transceiver) = ${toHMap(protocol.messages.map { message =>
            (message.name, messageFunc(message, s"""${getCodec(message.response)}.decode(new RichJSchema(${message.name}${"$"}.response.jSchema).encode(new _root_.org.apache.avro.ipc.generic.GenericRequestor(protocol${"$"}.jProtocol, transceiver).request("${message.name}", _root_.org.apache.avro.scala.compiler.createObject(${message.name}${"$"}.jMessage.getRequest, Map(${message.parameters.map(param => s""""${param.name}" -> new RichJSchema(${message.name}${"$"}.parameters.filter(_.name == "${param.name}").head.value.jSchema).decode(${getCodec(param.value)}.encode(${param.name}))""").mkString(", ")})))))"""))
          })}
        }

        val protocol${"$"} = $ref.Protocol.fromJava(_root_.org.apache.avro.Protocol.parse(${"\"\"\""}${protocol.jProtocol}${"\"\"\""}))
      }

      abstract class ${protocol.name}$typeDeclParams extends $ref.ProtocolHandler[$protocolName] {
        final def impl${"$"} = ${toHMap(protocol.messages.map { message =>
          val call = message.response.fromPresenter(s"this.${message.name}(${message.parameters.map(param => param.value.toPresenter(param.name)).mkString(", ")})")
          (s"$protocolName.${message.name}", s"(${messageFunc(message, call)})")
        })}

        ${protocol.messages.map(compileMessage(_)).mkString("\n\n")}
      }

      class ${protocol.name}Client$clientTypeDeclParams(transceiver: _root_.org.apache.avro.ipc.Transceiver) {
        ${protocol.messages.map { message =>
          s"${compileMessage(message)} = ${message.response.toPresenter(s"implicitly[$ref.Client[$protocolName, C${"$"}]].senders(transceiver).get($protocolName.${message.name}).apply(${message.parameters.map(param => param.value.fromPresenter(param.name)).mkString(", ")})")}"
        }.mkString("\n\n")}
      }
      """))
  }
}
