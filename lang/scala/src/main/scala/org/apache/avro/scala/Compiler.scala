/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.scala

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, FilenameFilter, InputStream, OutputStream}
import java.nio.ByteBuffer

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.reflect.ClassTag

import org.apache.avro.{Protocol => AvroProtocol, Schema => AvroSchema}
import org.apache.avro.io.{Decoder => AvroDecoder, DecoderFactory, EncoderFactory}
import org.apache.commons.io.FileUtils
import shapeless.{Field => SField, _}

case class Field(name: String, value: Schema)

trait Schema { var avroSchema: AvroSchema = null } // hack
case class UnionSchema(types: Seq[SingleSchema]) extends Schema // TODO(alek): use shapeless for stronger guarantees about types

trait SingleSchema extends Schema
case class ArraySchema(elements: Schema) extends SingleSchema
case class MapSchema(values: Schema) extends SingleSchema
case object BooleanSchema extends SingleSchema
case object BytesSchema extends SingleSchema
case object DoubleSchema extends SingleSchema
case object FloatSchema extends SingleSchema
case object IntSchema extends SingleSchema
case object LongSchema extends SingleSchema
case object NullSchema extends SingleSchema
case object StringSchema extends SingleSchema

trait NamedSchema extends SingleSchema { val name: String; val namespace: Option[String] }
case class FixedSchema(name: String, size: Int, namespace: Option[String]) extends NamedSchema
case class EnumSchema(name: String, symbols: Seq[String], namespace: Option[String]) extends NamedSchema

trait FieldsSchema extends SingleSchema { val fields: Seq[Field] }
trait RecordBaseSchema extends NamedSchema with FieldsSchema
case class ErrorSchema(name: String, fields: Seq[Field], namespace: Option[String]) extends RecordBaseSchema
case class RecordSchema(name: String, fields: Seq[Field], namespace: Option[String]) extends RecordBaseSchema

object Schema {
  def toFields(schema: AvroSchema): Seq[Field] = schema.getFields.asScala.map { field => Field(field.name, fromAvro(field.schema)) }

  def fromAvro(schema: AvroSchema): Schema = {
    schema.getType match {
      case AvroSchema.Type.NULL => NullSchema
      case AvroSchema.Type.BOOLEAN => BooleanSchema
      case AvroSchema.Type.INT => IntSchema
      case AvroSchema.Type.LONG => LongSchema
      case AvroSchema.Type.FLOAT => FloatSchema
      case AvroSchema.Type.DOUBLE => DoubleSchema
      case AvroSchema.Type.ENUM => EnumSchema(schema.getName, schema.getEnumSymbols.asScala, Option(schema.getNamespace))
      case AvroSchema.Type.STRING => StringSchema
      case AvroSchema.Type.FIXED => FixedSchema(schema.getName, schema.getFixedSize, Option(schema.getNamespace))
      case AvroSchema.Type.BYTES => BytesSchema
      case AvroSchema.Type.ARRAY => ArraySchema(fromAvro(schema.getElementType))
      case AvroSchema.Type.MAP => MapSchema(fromAvro(schema.getValueType))
      case AvroSchema.Type.RECORD => if (schema.isError) ErrorSchema(schema.getName, toFields(schema), Option(schema.getNamespace)) else RecordSchema(schema.getName, toFields(schema), Option(schema.getNamespace))
      case AvroSchema.Type.UNION => UnionSchema(schema.getTypes.asScala.map(fromAvro).asInstanceOf[Seq[SingleSchema]])
    }
  }
}

case class Parameter(name: String, value: Schema) // TODO(alek): default value

case class Message(name: String, parameters: Seq[Parameter], response: Schema, errors: Seq[ErrorSchema])

object Message {
  def fromAvro(message: AvroProtocol#Message): Message = {
    Message(message.getName,
      message.getRequest.getFields.asScala.map { field => Parameter(field.name, Schema.fromAvro(field.schema)) },
      Schema.fromAvro(message.getResponse),
      message.getErrors.getTypes.asScala.filter(_.getType != AvroSchema.Type.STRING).map { error => ErrorSchema(error.getName, Schema.toFields(error), Option(error.getNamespace)) })
  }
}

case class Protocol(name: String, declarations: Seq[NamedSchema], messages: Seq[Message], namespace: Option[String]) { var avroProtocol: AvroProtocol = null } // hack

object Protocol {
  def fromAvro(protocol: AvroProtocol): Protocol = {
    val ret = Protocol(protocol.getName,
      protocol.getTypes.asScala.map(Schema.fromAvro).toSeq.asInstanceOf[Seq[NamedSchema]],
      protocol.getMessages.values.asScala.toSeq.map { message =>
        Message(message.getName,
            message.getRequest.getFields.asScala.map { field => Parameter(field.name, Schema.fromAvro(field.schema)) },
            Schema.fromAvro(message.getResponse),
            message.getErrors.getTypes.asScala.filter(_.getType != AvroSchema.Type.STRING).map { error => ErrorSchema(error.getName, Schema.toFields(error), Option(error.getNamespace)) })
      },
      Option(protocol.getNamespace)
    )
    ret.avroProtocol = protocol
    ret
  }
}

// TODO(alek): generate @Deprecated annotations for aliases
// TODO(alek): propose friendlier syntax for scala 3.0 like "trait X mixin Y extends Z" (looks weird as "trait X mixin Y with Z extends A with B" or "trait[Y] X extends Z"
// TODO(alek): avro's Encoder shouldn't be Flushable; flushable subclasses should
// TODO(alek): actually, these methods are unnecessary later, just do trait Encoder { def write[T <: RecordDescriptor](record: T)(implicit output: OutputStream) }
trait Encoder {
  /*def setItemCount(itemCount: Long)(implicit output: OutputStream)
  def startItem()(implicit output: OutputStream)
  def writeArrayEnd()(implicit output: OutputStream)
  def writeArrayStart()(implicit output: OutputStream)
  def writeBoolean()(implicit output: OutputStream)
  def writeBytes(bytes: Iterable[Byte], start: Int = 0, len: Int = 0)(implicit output: OutputStream)
  def writeDouble(d: Double)(implicit output: OutputStream)
  def writeEnum(e: Int)(implicit output: OutputStream)
  def writeFixed(bytes: Iterable[Byte], start: Int = 0, len: Int = 0)(implicit output: OutputStream) // TODO(alek) consider vector or array
  def writeFloat(f: Float)(implicit output: OutputStream)
  def writeIndex(unionIndex: Int)(implicit output: OutputStream)
  def writeInt(n: Int)(implicit output: OutputStream)
  def writeLong(n: Long)(implicit output: OutputStream)
  def writeMapEnd()(implicit output: OutputStream)
  def writeMapStart()(implicit output: OutputStream)
  def writeNull()(implicit output: OutputStream)
  def writeString(str: Iterable[Char])(implicit output: OutputStream)*/
}

object Encoder {
  implicit val defaultEncoder = BinaryEncoder
}

// TODO(alek): propose ditching BufferedBinaryEncoder, since it duplicates functionality in BufferedOutputStream
object BinaryEncoder extends Encoder {
  val underlying = EncoderFactory.get().directBinaryEncoder(new ByteArrayOutputStream(), null) // dummy OutputStream
  def encoder(implicit output: OutputStream) = EncoderFactory.get().directBinaryEncoder(output, underlying)
  def setItemCount(itemCount: Long)(implicit output: OutputStream) { encoder.setItemCount(itemCount) }
  def startItem()(implicit output: OutputStream) { encoder.startItem() }
  def writeArrayEnd()(implicit output: OutputStream) { encoder.writeArrayEnd() }
  def writeArrayStart()(implicit output: OutputStream) { encoder.writeArrayStart() }
  def writeBoolean(b: Boolean)(implicit output: OutputStream) { encoder.writeBoolean(b) }
  def writeBytes(bytes: Iterable[Byte])(implicit output: OutputStream) { encoder.writeBytes(bytes.toArray) }
  def writeDouble(d: Double)(implicit output: OutputStream) { encoder.writeDouble(d) }
  def writeEnum(e: Int)(implicit output: OutputStream) { encoder.writeEnum(e) }
  def writeFixed(bytes: Iterable[Byte])(implicit output: OutputStream) { encoder.writeFixed(bytes.toArray) } // TODO(alek) consider vector or array
  def writeFloat(f: Float)(implicit output: OutputStream) { encoder.writeFloat(f) }
  def writeIndex(i: Int)(implicit output: OutputStream) { encoder.writeIndex(i) }
  def writeInt(n: Int)(implicit output: OutputStream) { encoder.writeInt(n) }
  def writeLong(n: Long)(implicit output: OutputStream) { encoder.writeLong(n) }
  def writeMapEnd()(implicit output: OutputStream) { encoder.writeMapEnd() }
  def writeMapStart()(implicit output: OutputStream) { encoder.writeMapStart() }
  def writeNull()(implicit output: OutputStream) { encoder.writeNull() }
  def writeString(str: String)(implicit output: OutputStream) { encoder.writeString(str) }
}

//object JsonEncoder extends Encoder

trait Decoder {
  final def apply[L <: HList](input: InputStream)(implicit hdecoder: DecoderAux[L]): L = hdecoder(input)
}

/*object Decoder {
  implicit val defaultDecoder = BinaryDecoder
}*/

trait DecoderAux[L <: HList] {
  def apply(input: InputStream): L
}

trait BinaryDecoder[L <: HList] extends DecoderAux[L]

object BinaryDecoder {
  implicit def headDecode[V, K <: SField[V], T <: HList](implicit consDecoder: BinaryDecoder[T], singleton: Singleton[K], reader: ValueReader[V]) = new HBinaryDecoder[(K, V) :: T] {
    def apply(input: InputStream) = (singleton.instance -> reader(input)) :: consDecoder(input)
  }

  implicit def finishDecode = new BinaryDecoder[HNil] {
    def apply(input: InputStream) = HNil // TODO(alek): check string empty
  }
}

trait ValueReader[V] {
  def apply(input: InputStream): V
}

object ValueReader {
  val underlying = DecoderFactory.get().directBinaryDecoder(new ByteArrayInputStream(Array()), null) // dummy InputStream
  def decoder(input: InputStream) = DecoderFactory.get().directBinaryDecoder(input, underlying)

  implicit def readArray[E](implicit reader: ValueReader[E], m: ClassTag[E]) = new ValueReader[AvroArray[E]] {
    def apply(input: InputStream) = {
      var blockSize = decoder(input).readArrayStart().toInt // FIXME(alek): support longs
      val array = new Array[E](blockSize)
      while (blockSize > 0) {
        for (i <- 0 until blockSize)
          array(i) = reader(input)
        blockSize = decoder(input).arrayNext().toInt
      }
      new AvroArray(array)
    }
  }

  implicit def readMap[V](implicit reader: ValueReader[V], m: ClassTag[V]) = new ValueReader[AvroMap[V]] {
    def apply(input: InputStream) = {
      var blockSize = decoder(input).readMapStart().toInt
      val map = mutable.Map[String, V]()
      while (blockSize > 0) {
        for (_ <- 0 until blockSize)
          map += (decoder(input).readString() -> reader(input))
        blockSize = decoder(input).mapNext().toInt
      }
      new AvroMap(map)
    }
  }

  implicit def readRecord[L <: HList](implicit hdecoder: BinaryDecoder[L]) = new ValueReader[L] {
    def apply(input: InputStream) = hdecoder(input)
  }

  implicit def readUnion[L <: HList](implicit reader: UnionReader[L]) = new ValueReader[L] {
    def apply(input: InputStream) = reader(decoder(input), input, decoder(input).readIndex())
  }

  implicit def readBoolean = new ValueReader[Boolean] {
    def apply(input: InputStream) = decoder(input).readBoolean()
  }

  implicit def readBytes = new ValueReader[Array[Byte]] {
    def apply(input: InputStream) = {
      val buffer = ByteBuffer.allocate(0)
      decoder(input).readBytes(buffer)
      buffer.array
    }
  }

  implicit def readDouble = new ValueReader[Double] {
    def apply(input: InputStream) = decoder(input).readDouble()
  }

  implicit def readFloat = new ValueReader[Float] {
    def apply(input: InputStream) = decoder(input).readFloat()
  }

  implicit def readEnum[E <: Enumeration](implicit e: EnumCreator[E]) = new ValueReader[E#Value] {
    def apply(input: InputStream) = e(decoder(input).readEnum())
  }

  /*implicit def readFixed = new ValueReader[FixedArray[Byte]] {
    val buffer = new Array[Byte](1024) // TODO(alek): fix this
    decoder(input).readFixed(buffer)
    buffer
  }*/

  implicit def readInt = new ValueReader[Int] {
    def apply(input: InputStream) = decoder(input).readInt()
  }

  implicit def readLong = new ValueReader[Long] {
    def apply(input: InputStream) = decoder(input).readLong()
  }

  implicit def readNull = new ValueReader[Unit] {
    def apply(input: InputStream) = { decoder(input).readNull(); () }
  }

  implicit def readString = new ValueReader[String] {
    def apply(input: InputStream) = decoder(input).readString()
  }
}

trait UnionReader[L <: HList] {
  def apply(decoder: AvroDecoder, input: InputStream, index: Int): L
}

object UnionReader {
  implicit def hcons[H, T <: HList](implicit headDecoder: ValueReader[H], consDecoder: UnionReader[T]) = new UnionReader[Option[H] :: T] {
    def apply(decoder: AvroDecoder, input: InputStream, index: Int) = (if (index == 0) Some(headDecoder(input)) else None) :: consDecoder(decoder, input, index-1)
  }
  implicit def hnil = new UnionReader[HNil] {
    def apply(decoder: AvroDecoder, input: InputStream, index: Int) = HNil
  }
}

trait EnumCreator[E <: Enumeration] {
  def apply(index: Int): E#Value
}

// TODO(alek): propose unboxed wrapper classes for AnyRefs in addition to AnyVals (basically newtype) - mostly syntactic sugar for the following
class AvroArray[E](private val underlying: Array[E]) // TODO(alek): require E <: AvroValue

object AvroArray {
  implicit def toUnderlying[E](wrapped: AvroArray[E]): Array[E] = wrapped.underlying
}

class AvroMap[V](private val underlying: mutable.Map[String, V]) // TODO(alek): require E <: AvroValue

object AvroMap {
  implicit def toUnderlying[V](wrapped: AvroMap[V]): mutable.Map[String, V] = wrapped.underlying
}

// TODO(alek): propose Manifest-style typeclass to get singleton instance of companion object type at runtime - useful for this, at least
trait Singleton[S] {
  def instance: S
}

trait Codec[T, R <: HList] {
  def encode(repr: T): R
  /*def encodeToString(repr: T)(implicit encoder: Encoder): Seq[Byte] = {
    val record = encode(repr)
    val output = new ByteArrayOutputStream()
    encode(repr)(encoder, output)
    //encoder.flush()
    output.toByteArray
  }*/
  def decode(record: R): T
  def decodeFromString(bytes: Iterator[Byte])(implicit decoder: Decoder, h: DecoderAux[R]): T = decode(decoder[R](new ByteArrayInputStream(bytes.toArray)))
}
// TODO(alek): keep this around as a shortcut, derive from Canon and (other) Codec existing

// TODO(alek): it would be great if we could create a newtype for Map[String, ...], so we didn't have to wrap it
// TODO(alek): use shapeless.Newtype for this, bring back RecordDescriptor as well
trait Rpc[P <: HList] {
  def handlers: Map[String, (P, Seq[String], Encoder, Decoder) => Option[String]] // TODO(alek): something to regain type safety (mapping?)
}

trait ProtocolHandler[P <: HList] {
  def impl$: P
}

object Union {
  // lovingly stolen from http://www.chuusai.com/2011/06/09/scala-union-types-curry-howard/
  type ¬[A] = A => Nothing
  type |[T, U] = { type L[X] = ¬[¬[X]] <:< ¬[¬[T] with ¬[U]] }
}

object CodeStringProcessor {
  implicit def codeHelper(ctx: StringContext) = new {
    def code(args: Any*): String = {
      val parts = ctx.parts.iterator
      val expressions = args.iterator
      val buf = new StringBuffer()
      def nextPart(): Int = {
        val part = parts.next()
        buf.append(part)
        part.slice(part.lastIndexOf("\n"), part.length).length
      }
      var lastIndent = nextPart()
      while (parts.hasNext) {
        val expression = expressions.next()
        buf.append(if (expression.isInstanceOf[String]) {
          val lines = expression.asInstanceOf[String].split("\n")
          (lines.take(1) ++ lines.drop(1).map(" " * lastIndent + _)).mkString("\n")
        }
        else
          expression)
        lastIndent = nextPart()
      }
      val interpolated = """(^\s*\n)|(\s*$)""".r.replaceAllIn(buf.toString, "")
      val indents = interpolated.split("\n").filter(!_.trim.isEmpty).map("^\\s*".r.findFirstIn(_).get.length)
      val offset = if (indents.size > 0) indents.min else 0
      interpolated.split("\n").map { line => line.slice(offset, line.length) }.mkString("\n")
    }
  }
}

// TODO(alek): report scaladoc bug that lists return types inferred from varargs as Foo*, rather than Seq[Foo] - for example, def foo(a: Int*) = a

object Compiler {
  import CodeStringProcessor._

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
    val recordFields = schemas.flatMap(getRecords)
    val recordParams = recordFields.map("T$"+_.name).mkString(", ")
    val recordDeclParams = recordFields.map { schema => s"T${"$"}${schema.name} : ({ type L[X] = org.apache.avro.scala.Codec[X, ${schema.name}${"$"}Record.Descriptor] })#L" }.mkString(", ")
    ((if (recordParams.length > 0) s"[$recordParams]" else ""), (if (recordDeclParams.length > 0) s"[$recordDeclParams]" else ""))
  }

  def toHList(items: Seq[String]): String = (items :+ "shapeless.HNil").mkString(" ::\n")

  def toHMap(items: Map[String, String]): String = toHList(items.toSeq.map { case (k, v) => s"($k -> $v)" })

  def compileSchema(schema: Schema, wrap: Boolean = true): String = {
    schema match {
      case UnionSchema(types) => types.map(compileSchema(_)).mkString("\n\n")
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
        val encode = fieldsSchema.fields.map { field => s"$record.${field.name} -> ${DatumEncoder(field.value, "repr.%s".format(field.name), "encoder")}" }.mkString("\n")
        val source = code"""
          object $record {
            ${fieldsSchema.fields.map { field => code"""
              object ${field.name} extends shapeless.Field[${TypeMap(field.value, false)}]
              implicit def singleton${"$"}${field.name} = new org.apache.avro.scala.Singleton[${field.name}.type] { override def instance = ${field.name} }
            """ }.mkString("\n")}

            type Descriptor = ${toHList(fieldsSchema.fields.map { field => s"(${field.name}.type, ${TypeMap(field.value, false)})" })}

            val schema = new org.apache.avro.Schema.Parser().parse(${"\"\"\""}
              ${schema}${"\"\"\""})
          }

          object $className {
            implicit def codec$typeDeclParams = new org.apache.avro.scala.Codec[$className$typeParams, $record.Descriptor] {
              def encode(repr: $className$typeParams)(implicit encoder: Encoder, output: OutputStream) {
                $encode
              }

              def decode(record: Seq[Byte]): $className$typeParams = { ??? }

              def fromJson(json: scala.util.parsing.json.JSONType): $className$typeParams = { ??? } // for default values
            }

            def encode$typeDeclParams(repr: $className$typeParams): Seq[Byte] = {
              codec.encode(repr)
            }

            def decode$typeDeclParams(record: Seq[Byte]): $className$typeParams = {
              codec.decode(record)
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
    // TODO(alek): default type parameters (type members, but talk to scala devs about using them in constructors)
    // TODO(alek): handle void return types
    // TODO(alek): ask scala devs why `object TypeMap { def apply(x: Int) }; Seq(5,6).map(TypeMap _)` doesn't work
    // TODO(alek): ask whether errors can be returned normally from messages (or accepted as parameters)
    // TODO(alek): propose creating @error annotation on records, rather than compeletely separate type
    packageWrap(protocol.namespace, code"""
      ${protocol.declarations.map(compileSchema(_, wrap = false)).mkString("\n\n")}

      object ${protocol.name}${"$"}Protocol {
        ${protocol.messages.map { message => s"object ${message.name} extends shapeless.Field[(${message.parameters.map { parameter => TypeMap(parameter.value, false) }.mkString(", ")}) => ${TypeMap(message.response, false)}]" }.mkString("\n")}

        type Descriptor = ${toHList(protocol.messages.map { message => s"(${message.name}.type, (${message.parameters.map { parameter => TypeMap(parameter.value, false) }.mkString(", ")}) => ${TypeMap(message.response, false)})" })}
        implicit def foo = new org.apache.avro.scala.Rpc[Descriptor] {
          override final def handlers = Map(${protocol.messages.map { message =>
            val call = s"impl(${message.name})(${message.parameters.map { param => param.value match { case record: RecordBaseSchema => s"decoder[${record.name}${"$"}Record.Descriptor](${param.name})"; case _ => param.name } }.mkString(", ")})"
            val ret = message.response match {
              case record: RecordBaseSchema => s"encoder[${record.name}${"$"}Record.Descriptor](${call})"
              case _ => call
            }
            s"${'"'}${message.name}${'"'} -> { (impl: Descriptor, parameters: Seq[String], encoder: org.apache.avro.scala.Encoder, decoder: org.apache.avro.scala.Decoder) => ${ret} }"
          }.mkString(", ")})
        }

        val protocol = new org.apache.avro.Schema.Parser().parse(${"\"\"\""}
          ${protocol}${"\"\"\""})
      }

      abstract class ${protocol.name}Handler$typeDeclParams extends org.apache.avro.scala.ProtocolHandler[${protocol.name}${"$"}Protocol.Descriptor] {
        final def impl${"$"} = ${toHMap(protocol.messages.map { message =>
          val call = s"${message.name}(${message.parameters.map { param => param.value match { case record: RecordBaseSchema => s"implicitly[org.apache.avro.scala.Codec[${TypeMap(record)}, ${record.name}${"$"}Record.Descriptor]].decode(${param.name})"; case _ => param.name } }.mkString(", ")})"
          val ret = message.response match {
            case record: RecordBaseSchema => s"implicitly[org.apache.avro.scala.Codec[${TypeMap(record)}, ${record.name}${"$"}Record.Descriptor]].encode(${call})"
            case _ => call
          }
          (s"${protocol.name}${"$"}Protocol.${message.name}", s"((${message.parameters.map { param => s"${param.name}: ${TypeMap(param.value)}" }.mkString(", ")}) => ${ret})")
        }.toMap)}

        ${protocol.messages.map(compileMessage(_)).mkString("\n\n")}
      }""")
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

object CompilerApp extends App {
  import CodeStringProcessor._

  if (args.size < 3) {
    printHelp()
    sys.exit(1)
  }

  abstract class InputType(val extension: String)
  case object SchemaInput extends InputType("avsc")
  case object ProtocolInput extends InputType("avpr")
  val inputType = args(0) match {
    case "schema" => SchemaInput
    case "protocol" => ProtocolInput
    case _ => {
      println("Must specify either 'schema' or 'protocol'")
      printHelp()
      sys.exit(1)
    }
  }

  val fileArgs = args.drop(1).map { path =>
    val f = new File(path)
    if (!f.exists) {
      println("CompilerApp: %s: No such file or directory".format(f.getPath))
      sys.exit(2)
    }
  }

  val outDir = new File(args(1))
  require(outDir.isDirectory && outDir.exists, outDir)
  val inPaths = args.drop(2)
  val inObjs = inPaths.map(new File(_))
  val inFiles = inObjs.filter(_.isFile)
  val inDirs = inObjs.filter(_.isDirectory)

  compileAndWrite(outDir, inFiles, inputType)
  for (inDir <- inDirs) {
    println(inDir + ":")
    compileAndWrite(outDir, inDir, inputType)
  }

  def compileAndWrite(outDir: File, inDir: File, inputType: InputType) {
    require(inDir.exists, inDir)
    object filter extends FilenameFilter {
      override def accept(dir: File, name: String): Boolean =
        name.endsWith(".%s" format inputType.extension)
    }
    val inFiles = inDir.listFiles(filter)
    compileAndWrite(outDir, inFiles, inputType)
  }

  def compileAndWrite(outDir: File, inFiles: Seq[File], inputType: InputType) {
    for (inFile <- inFiles) {
      val name = inFile.getName.stripSuffix(".%s".format(inputType.extension))
      val scalaFile = new File(outDir, "%s.scala".format(name)) // TODO(alek): handle collisions from files with same input name (but different directories) being written to same output directory
      val source = FileUtils.readFileToString(inFile)
      val scalaSource = code"""
        // This file is machine-generated.

        import _root_.org.apache.avro.scala.Union.|
        import _root_.shapeless.::

        ${inputType match {
            case SchemaInput => Compiler.compileSchema(Schema.fromAvro(new AvroSchema.Parser().parse(source)))
            case ProtocolInput => Compiler.compileProtocol(Protocol.fromAvro(AvroProtocol.parse(source)))
          }}
      """ + "\n"
      require(scalaFile.getParentFile.exists || scalaFile.getParentFile.mkdirs())
      FileUtils.writeStringToFile(scalaFile, scalaSource)
    }
  }

  def printHelp() {
    println("Usage: CompilerApp <schema|protocol> OUTDIR PATH...")
  }
}
