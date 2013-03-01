package org.apache.avro.scala

import shapeless.{Field => _, _}

// TODO(alek): generate @Deprecated annotations for aliases
// TODO(alek): propose friendlier syntax for scala 3.0 like "trait X mixin Y extends Z" (looks weird as "trait X mixin Y with Z extends A with B" or "trait[Y] X extends Z"
// TODO(alek): avro's Encoder shouldn't be Flushable; flushable subclasses should
// TODO(alek): actually, these methods are unnecessary later, just do trait Encoder { def write[T <: RecordDescriptor](record: T)(implicit output: OutputStream) }
// TODO(alek): propose ditching BufferedBinaryEncoder, since it duplicates functionality in BufferedOutputStream
// TODO(alek): make into a plugin

// TODO(alek): report scaladoc bug that lists return types inferred from varargs as Foo*, rather than Seq[Foo] - for example, def foo(a: Int*) = a
object Compiler {
  import CodeStringProcessor._

  def fileWrap(contents: String): String = {
    code"""
      // This file is machine-generated.

      import _root_.org.apache.avro.scala.ctag.@@
      import _root_.shapeless.{|∨| => |, ::}

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
    (schema match {
      case record: RecordBaseSchema => Seq(record)
      case UnionSchema(types) => types.flatMap(getRecords(_))
      case ArraySchema(elements) => getRecords(elements)
      case MapSchema(values) => getRecords(values)
      case _ => Seq()
    }).toSet
  }

  def getRecordParams(schemas: Seq[Schema]): (String, String) = {
    val recordFields = schemas.flatMap(getRecords).toSet
    val recordParams = recordFields.map("T$"+_.name).mkString(", ")
    val recordDeclParams = recordFields.map { schema => s"T${"$"}${schema.name} : ({ type L[X] = org.apache.avro.scala.Presenter[X, ${schema.name}${"$"}Record.Descriptor] })#L" }.mkString(", ")
    ((if (recordParams.length > 0) s"[$recordParams]" else ""), (if (recordDeclParams.length > 0) s"[$recordDeclParams]" else ""))
  }

  def toHList(items: Seq[String]): String = (items :+ "shapeless.HNil").mkString(" ::\n")

  def toHMap(items: Map[String, String]): String = toHList(items.toSeq.map { case (k, v) => s"($k -> $v)" })

  def compileSchema(schema: Schema, wrap: Boolean = true): String = fileWrap(compileSchemaAux(schema, wrap))

  def compileSchemaAux(schema: Schema, wrap: Boolean = true): String = {
    schema match {
      case UnionSchema(types) => types.map(compileSchemaAux(_)).mkString("\n\n")
      case EnumSchema(name, symbols, namespace) => {
        packageWrap(namespace, code"""
          object $name extends Enumeration {
            type $name = Value
            val ${symbols.mkString(", ")} = Value
          }""")
      }
      case fieldsSchema: RecordBaseSchema => {
        // TODO(alek): configurable encoder implementation
        // TODO(alek): return either String or OutputStream implementation, depending on type requirements
        // TODO(alek): http://docs.scala-lang.org/overviews/macros/typemacros.html
        // TODO(alek): generate code with treehugger or something actually readable (use macros - scala"") - handle all escaping, checking, etc (type checking? - optional; things have to be on classpath - for only some things, then?) - could actually be used by other macros - also add ability to generate unique identifiers with %uniq(foo) (substitution is still ${bar}) - include pretty-printing, automatically moving imports to top-level with %import(baz), %extends(array) -> "extends A with B with ...", backticking IDs that conflict with %id(foo), expansion of javadoc objects with %javadoc(foo) - whole thing returns not a String, but a SyntaxTree (or whatever the scalac library calls it) - might have to use our own tree repr that can be converted to scalac's later, function inlining via %inline - so code sections used multiple times can be type-checked
        // TODO(alek): ask scala devs about replacing first-class xml support with xml"" before Scala 3.0 - would break *a lot* of existing code, but fix would be easily automatable, and the language would actually *shrink* (crazy!)
        // TODO(alek): scala 3.0 - replace tuples with HLists (functions with currying), class -> HMap abstraction (eliminates most reflection), throw out classes completely, compile traits to concrete or abstract classes depending on whether they have abstract members, unify 'package' and 'package object', allow pretty much anything at top-level
        // TODO(alek): schema library based on case classes
        // TODO(alek): propose @nullable annotation (or similar), since a type of "null" doesn't make any sense, ask them to explicitly prohibit zero- and single-type unions
        // TODO(alek): escape $ in string processors with \
        // TODO(alek): nested records as nested classes
        // TODO(alek): anonymous records?
        // TODO(alek): override polymorphic methods, e.g. trait A { def foo[T]: T }; class B extends A { override def foo[String]: String = "" }
        // TODO(alek): "polymorphic composition"
        // TODO(alek): override case class equals, hashCode, etc to handle different implementations of record types
        // TODO(alek): have codec accept/return record type (HMap), provide json, binary, etc implementations of serializers, since java API sucks - also allows records to be abstracted over while maintaining type safety (for example, comparing two arbitrary records for equality, or compressing/encrypting all fields of a record)
        // TODO(alek): annotation to format toString output a certain way (using s"${foo}" notation?), or to call a certain function
        // TODO(alek): propose top-level 'type' declarations
        // TODO(alek): propose making constructor syntactic sugar/magic for creating a companion object 'new' method (which can't be explicitly defined through normal syntax) - possibly also replace alternative constructors with explicitly defining new() overloads on companion object - would also (mostly) eliminate need for/replace (Class)Manifest, and solve problem with constructor default parameters referring to methods
        val traits = Seq(if (fieldsSchema.isInstanceOf[ErrorSchema]) Some("Throwable") else None/*, Option(schema.getProp("scalaTrait"))*/).flatten.mkString(" with ")
        val `extends` = if (traits.length > 0) s"\n    extends $traits" else "" // TODO(alek): support multiple traits
        def recordName(schema: Schema) = fieldsSchema.name + "$Record"
        val pair = getRecordParams(fieldsSchema.fields.map(_.value))
        val typeParams = pair._1
        val typeDeclParams = pair._2
        val className = fieldsSchema.name
        val record = recordName(schema)
        def compileField(field: Field): String = {
          // TODO(alek): figure out default values for polymorphic record types
          val default = "" //if (field.defaultValue != null) " = " + compileDefaultValue(field.schema, field.defaultValue) else ""
          code"${field.name}: ${TypeMap(field.value)}$default"
        }
        val source = code"""
          object $record {
            ${fieldsSchema.fields.map { field => code"""
              object ${field.name} extends org.apache.avro.scala.TField[${TypeMap(field.value, false)}]
              //implicit def singleton${"$"}${field.name} = new org.apache.avro.scala.Singleton[${field.name}.type] { override def instance = ${field.name} }
            """ }.mkString("\n")}

            type Descriptor = (${toHList(fieldsSchema.fields.map { field => s"(${field.name}.type, ${TypeMap(field.value, false)})" })}) @@ org.apache.avro.scala.RecordDescriptor

            val schema = org.apache.avro.scala.Schema.fromJava(new org.apache.avro.Schema.Parser().parse(${"\"\"\""}
              ${schema.jSchema}${"\"\"\""}))
          }

          object $className {
            implicit def presenter$typeDeclParams = new org.apache.avro.scala.Presenter[$className$typeParams, $record.Descriptor] {
              override def from(repr: $className$typeParams) = ${toHMap(fieldsSchema.fields.map { field => (s"$record.${field.name}", DatumEncoder(field.value, "repr.%s".format(field.name))) }.toMap)}

              override def to(record: $record.Descriptor) = ???

              override def fromJson(json: scala.util.parsing.json.JSONType): $className$typeParams = ??? // for default values
            }

            def encode$typeDeclParams(repr: $className$typeParams)(implicit codec: Codec): Iterator[Byte] = {
              codec.encode(presenter.from(repr))
            }

            def decode$typeDeclParams(record: Iterator[Byte]): $className$typeParams = {
              presenter.to(codec.decode(record))
            }
          }

          case class $className$typeDeclParams(${fieldsSchema.fields.map(compileField(_)).mkString(", ")})${`extends`}"""
        if (wrap) packageWrap(fieldsSchema.namespace, source) else source
      }
    }
  }

  def compileProtocol(protocol: Protocol): String = {
    // TODO(alek): covariance/contravariance can't be expressed this way (for function parameters/return types)
    // TODO(alek): companion object factory for protocol handler objects that allows us to use abstract type members instead of type parameters (implicits specified on the factory method)
    def compileMessage(message: Message): String = {
      val errors = message.errors.map { error => TypeMap(error) }.mkString(", ")
      val throws = if ( errors.length > 0 ) s"@throws[$errors]\n" else "" // TODO(alek): fix newline
      val params = message.parameters.map { param => s"${param.name}: ${TypeMap(param.value)}" }.mkString(", ")
      code"$throws def ${message.name}($params): ${TypeMap(message.response)}"
    }

    val pair = getRecordParams(protocol.messages.flatMap { message => message.parameters.map(_.value) ++ message.errors :+ message.response }.toSeq)
    val typeParams = pair._1
    val typeDeclParams = pair._2
    val protocolName = protocol.name + "$Protocol"
    val descriptor = s"$protocolName.Descriptor"
    // TODO(alek): default type parameters (type members, but talk to scala devs about using them in constructors)
    // TODO(alek): handle void return types
    // TODO(alek): ask scala devs why `object TypeMap { def apply(x: Int) }; Seq(5,6).map(TypeMap _)` doesn't work
    // TODO(alek): ask whether errors can be returned normally from messages (or accepted as parameters)
    // TODO(alek): propose creating @error annotation on records, rather than compeletely separate type
    // TODO(alek): HList of monad operations - abstract the logic below like it was for map (hnil === return?)
    // TODO(alek): teach Scala 3 to infer class type parameters/members from their usage in other members' type signatures (function parameter/return types, etc)
    // TODO(alek): report bug that type members can't be used in constructor parameters
    // TODO(alek): see if you can enforce Names being an HSet
    // TODO(alek): unapply for HLists (head, tail)
    // TODO(alek): Poly2.tupled (for pattern-matching on parameters, etc)
    fileWrap(packageWrap(protocol.namespace, code"""
      ${protocol.declarations.map(compileSchemaAux(_, wrap = false)).mkString("\n\n")}

      object ${protocolName} {
        ${protocol.messages.map { message => s"object ${message.name} extends shapeless.Field[(${message.parameters.map { parameter => TypeMap(parameter.value, false) }.mkString(", ")}) => ${TypeMap(message.response, false)}]" }.mkString("\n")}

        type Descriptor = ${toHList(protocol.messages.map { message => s"(${message.name}.type, (${message.parameters.map { parameter => TypeMap(parameter.value, false) }.mkString(", ")}) => ${TypeMap(message.response, false)})" })}
        implicit def rpc = new org.apache.avro.scala.Rpc[Descriptor] {
          override final def handlers = Map(${protocol.messages.map { message =>
            val params = s"impl.get(${message.name})(${message.parameters.zipWithIndex.map { case (param, idx) => s"codec.decode[${TypeMap(param.value)}](parameters($idx).getBytes.toIterator)" }.mkString(", ")})"
            val call = s"codec.encode[${TypeMap(message.response)}]($params)"
            s"${'"'}${message.name}${'"'} -> { (impl: Descriptor, parameters: Seq[String], codec: org.apache.avro.scala.Codec) => $call }"
          }.mkString(", ")})
        }

        val protocol = org.apache.avro.scala.Protocol.fromJava(org.apache.avro.Protocol.parse(${"\"\"\""}
          ${protocol.jProtocol}${"\"\"\""}))
      }

      abstract class ${protocol.name}Handler$typeDeclParams extends org.apache.avro.scala.ProtocolHandler[${descriptor}] {
        final def impl${"$"} = ${toHMap(protocol.messages.map { message =>
          val call = s"${message.name}(${message.parameters.map { param => param.value match { case record: RecordBaseSchema => s"implicitly[org.apache.avro.scala.Presenter[${TypeMap(record)}, $descriptor]].decode(${param.name})"; case _ => param.name } }.mkString(", ")})"
          val ret = message.response match {
            case record: RecordBaseSchema => s"implicitly[org.apache.avro.scala.Presenter[${TypeMap(record)}, ${record.name}${"$"}Record.Descriptor]].fromRepr($call)"
            case _ => call
          }
          (s"$protocolName.${message.name}", s"((${message.parameters.map { param => s"${param.name}: ${TypeMap(param.value)}" }.mkString(", ")}) => $ret)")
        }.toMap)}

        ${protocol.messages.map(compileMessage(_)).mkString("\n\n")}
      }"""))
  }

  /*def compileDefaultValue(schema: Schema, default: JsonNode): String = {
    schema.getType match {
      case Schema.Type.NULL => {
        assert(default.getTextValue == null, default.getTextValue)
        "null"
      }
      case Schema.Type.BOOLEAN => default.getBooleanValue.toString
      case Schema.Type.INT => default.getIntValue.toString
      case Schema.Type.LONG => default.getLongValue.toString
      case Schema.Type.FLOAT
         | Schema.Type.DOUBLE => default.getDoubleValue.toString
      case Schema.Type.ARRAY => {
        val values = (0 until default.size).map(default.get(_).toString)
        "%s(%s)".format(TypeMap(schema), values.mkString(", "))
      }
      case Schema.Type.MAP => {
        val values = default.getFields.asScala.map { entry =>
          val key = new String(JsonStringEncoder.getInstance.quoteAsString(entry.getKey))
          assert(entry.getValue.isValueNode, "only JSON value nodes are currently supported")
            "\"%s\" -> %s".format(key, entry.getValue.toString)
        }
        "%s(%s)".format(TypeMap(schema), values.mkString(", "))
      }
      case Schema.Type.STRING => default.toString
      case Schema.Type.ENUM =>
        "%s.%s".format(TypeMap(schema), default.getTextValue)
      case Schema.Type.UNION => {
        val types = schema.getTypes.asScala
        if ( types.size == 0 )
          throw new RuntimeException("Cannot set default value for union with no types")
        compileDefaultValue(types.head, default)
      }
      case Schema.Type.RECORD => "null" // FIXME(alek): handle
    }
  }*/
}
