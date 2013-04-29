package org.apache.avro.scala

import scala.collection.JavaConverters._

import org.apache.avro.{Protocol => JProtocol, Schema => JSchema}
import org.apache.avro.generic.{GenericDatumReader, GenericDatumWriter}
import org.apache.avro.io.{DecoderFactory, EncoderFactory}
import org.apache.avro.util.Utf8

import generated._

sealed trait Schema {
  var jSchema: JSchema = null // temporary hack
}

trait SchemaValue[T] {
  object Writer {
    def unapply(m: Manifest[_], value: Any): Option[T] = if (m <:< manifest[T]) Some(value.asInstanceOf[T]) else None
  }

  object Reader {
    def unapply(m: Manifest[_]): Boolean = m <:< manifest[T]
  }
}

sealed trait SingleSchema extends Schema
case object IntSchema extends SingleSchema with SchemaValue[Int]
case object LongSchema extends SingleSchema with SchemaValue[Long]
case object FloatSchema extends SingleSchema with SchemaValue[Float]
case object DoubleSchema extends SingleSchema with SchemaValue[Double]
case object BooleanSchema extends SingleSchema with SchemaValue[Boolean]
case object BytesSchema extends SingleSchema with SchemaValue[Array[Byte]]
case object StringSchema extends SingleSchema with SchemaValue[String]
case object NullSchema extends SingleSchema with SchemaValue[Unit]

// TODO(alek): use shapeless for stronger guarantees about types
case class UnionSchema(types: Seq[SingleSchema]) extends Schema
object UnionSchema extends SchemaValue[UnionDescriptor[_]]
case class ArraySchema(elements: Schema) extends SingleSchema
object ArraySchema extends SchemaValue[Seq[_]]
case class MapSchema(values: Schema) extends SingleSchema
object MapSchema extends SchemaValue[AvroMap[_]]

sealed trait NamedSchema extends SingleSchema { val name: String; val namespace: Option[String] }
case class FixedSchema(name: String, size: Int, namespace: Option[String]) extends NamedSchema
object FixedSchema extends SchemaValue[AvroFixed]
case class EnumSchema(name: String, symbols: Seq[String], namespace: Option[String]) extends NamedSchema
object EnumSchema extends SchemaValue[AvroEnum]

case class Field(name: String, value: Schema)
sealed trait RecordBaseSchema extends NamedSchema { val fields: Seq[Field] }
object RecordBaseSchema extends SchemaValue[RecordDescriptor[_]] {
  def unapply(schema: RecordBaseSchema) = Some((schema.name, schema.fields, schema.namespace))
}
case class ErrorSchema(name: String, fields: Seq[Field], namespace: Option[String]) extends RecordBaseSchema
case class RecordSchema(name: String, fields: Seq[Field], namespace: Option[String]) extends RecordBaseSchema

object Schema {
  def toFields(schema: JSchema): Seq[Field] = schema.getFields.asScala.map(field => Field(field.name, fromJava(field.schema)))

  // TODO(alek): convert to asScala pimps on JSchema, JProtocol, JMessage, add asJava member to Schema, Protocol, Message
  def fromJava(schema: JSchema): Schema = {
    val ret = schema.getType match {
      case JSchema.Type.NULL => NullSchema
      case JSchema.Type.BOOLEAN => BooleanSchema
      case JSchema.Type.INT => IntSchema
      case JSchema.Type.LONG => LongSchema
      case JSchema.Type.FLOAT => FloatSchema
      case JSchema.Type.DOUBLE => DoubleSchema
      case JSchema.Type.ENUM => EnumSchema(schema.getName, schema.getEnumSymbols.asScala, Option(schema.getNamespace))
      case JSchema.Type.STRING => StringSchema
      case JSchema.Type.FIXED => FixedSchema(schema.getName, schema.getFixedSize, Option(schema.getNamespace))
      case JSchema.Type.BYTES => BytesSchema
      case JSchema.Type.ARRAY => ArraySchema(fromJava(schema.getElementType))
      case JSchema.Type.MAP => MapSchema(fromJava(schema.getValueType))
      case JSchema.Type.RECORD => if (schema.isError) ErrorSchema(schema.getName, toFields(schema), Option(schema.getNamespace)) else RecordSchema(schema.getName, toFields(schema), Option(schema.getNamespace))
      case JSchema.Type.UNION => UnionSchema(schema.getTypes.asScala.map(fromJava).asInstanceOf[Seq[SingleSchema]])
    }
    ret.jSchema = schema
    ret
  }
}

case class Parameter(name: String, value: Schema) // TODO(alek): default value

case class Message(name: String, parameters: Seq[Parameter], response: Schema, errors: Seq[ErrorSchema]) { var jMessage: JProtocol#Message = null }

object Message {
  def fromJava(message: JProtocol#Message): Message = {
    val ret = Message(message.getName,
      message.getRequest.getFields.asScala.map(field => Parameter(field.name, Schema.fromJava(field.schema))),
      Schema.fromJava(message.getResponse),
      message.getErrors.getTypes.asScala.filter(_.getType != JSchema.Type.STRING).map(error => ErrorSchema(error.getName, Schema.toFields(error), Option(error.getNamespace))))
    ret.jMessage = message
    ret
  }
}

case class Protocol(name: String, declarations: Seq[NamedSchema], messages: Seq[Message], namespace: Option[String]) { var jProtocol: JProtocol = null } // hack

object Protocol {
  def fromJava(protocol: JProtocol): Protocol = {
    val ret = Protocol(protocol.getName,
      protocol.getTypes.asScala.map(Schema.fromJava).toSeq.asInstanceOf[Seq[NamedSchema]],
      protocol.getMessages.values.asScala.toSeq.map(Message.fromJava(_)),
      Option(protocol.getNamespace)
    )
    ret.jProtocol = protocol
    ret
  }
}

package object jschema {
  implicit class RichJSchema(jSchema: JSchema) {
    def encode(obj: AnyRef): Iterator[Byte] = {
      val output = new ByteIteratorOutputStream
      new GenericDatumWriter[AnyRef](jSchema).write(obj, EncoderFactory.get().directBinaryEncoder(output, null))
      output.iterator
    }
    def decode(input: Iterator[Byte]): AnyRef = new GenericDatumReader[AnyRef](jSchema).read(null, DecoderFactory.get().binaryDecoder(input.toArray, null))
  }
}

package object utf8 {
  implicit class RichString(s: String) {
    def toUtf8 = new Utf8(s)
  }
}

// TODO(alek): automatically create typesafe builders for case classes in shapeless - iso to HList, lift to Options, update via zipper or lens, enable build() method via implicit, lower from Options, iso to case class
// TODO(alek): lenses that map over nested containers (for Options, return None for all sub-lenses) - or arbitrary collection operation like filter, etc
// example: case class A(i: Int); case class B(bs: Seq[A]); val iLens = Lens[B] >> _0 >> _0; val is: Seq[Int] = iLens.get(B(Seq(A(1),A(2)))); is == Seq(1,2)
// TODO(alek): propose using different (new) operator for actual values in pattern matches, so non-standard identifiers can be used via backticks
// TODO(alek): raise an exception if someone tries to encode a null (at all) instead of a Unit (provide an easy conversion method, like object Unit { def apply[T <: AnyRef](t: T): (Unit | T)#L = if (t == null) () else t }
// TODO(alek): need a way to talk about polymorphic functions generically, e.g. "a function that takes these types and returns these types (has these type-type pairings) - use abstract methods and subclassing
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

// TODO(alek): add feature-level imports to shapeless (like higherKinds)
/*type TCLifter[I] = TCLifterAux[Any, I]

class TCLifterAux[T, I] extends Poly1

object TCLifterAux {
  implicit def foo[T, L <: T, I](implicit imp: I[L]) = forAt[TCLifter[T, I], L](imp(_))
}

trait ~>[-F[_], +G[_]] extends Poly1 {
  //implicit def foo[T, G0[_] <: ~>.Wild, F0[_], G1[_], P <: HList](implicit st: Pullback1[P, G0[T]], ev: Pullback1Aux[G0[T], F0[T], G1[T]]) = at[F0[T] :: P](params => st(params.tail).apply(params.head))
  //def apply[T](f0: F[T])(implicit st: Pullback1[F0[T] :: HNil, G[T]]): G[T] = st(f0 :: HNil)
  //def apply[T, G0[_], F1[_]](f0: F[T], f1: F1[T])(implicit st: Pullback1[F1[T] :: F[T] :: HNil, G0[T]]): G0[T] = st(f1 :: f0 :: HNil)
  //def apply[T, G0[_], F1[_], F2[_]](f0: F[T], f1: F1[T], f2: F2[T])(implicit st: Pullback1[F2[T] :: F1[T] :: F[T] :: HNil, G0[T]]): G0[T] = st(f2 :: f1 :: f0 :: HNil)
  // TODO(alek): version of varargs that preserves type information by passing a type param that extends Product - need to figure out syntax - below, F0 refines the type of the params
  //def apply[T, F0[_], FF <: Product](f: F0[T]**)(implicit op: Case1Aux[Compose1[TCLifter[HLister], TCLifter[ReversePoly]], FF]) = at[op.R](params => st(params.tail).apply(params.head))
}

object ~> {
  type Wild = X ~> Y forSome { type X[_]; type Y[_] }
}

// TODO(alek): resolve diverging implicit expansion same way typeclass.scala did it in shapeless
trait CurrierAux[C <: HList, P <: HList] {
  type Out[R]
  def apply[R](f: P => R, curried: C): Out[R]
}

object CurrierAux {
  implicit def currierAuxHCons[C <: HList, H, H2, T <: HList, CH <: HList, P <: HList](implicit tailCurrierAux: CurrierAux[CH, P], prependP: PrependAux[C, H :: H2 :: T, P], prependCH: PrependAux[C, H :: HNil, CH]) = new CurrierAux[C, P] {
    type Out[R] = H => tailCurrierAux.Out[R]
    def apply[R](f: P => R, curried: C) = (h: H) => tailCurrierAux(f, curried :+ h)
  }

  implicit def currierAuxHNil[C <: HList, H, CH <: HList](implicit prepend: PrependAux[C, H :: HNil, CH]) = new CurrierAux[C, CH] {
    type Out[R] = H => R
    def apply[R](f: CH => R, curried: C) = (h: H) => f(curried :+ h)
  }
}

trait Currier[F] {
  type Out
  type R
  def apply(f: F): Out
}

object Currier {
  implicit def currier[F, P <: HList, R0](implicit hlisterAux: FnHListerAux[F, P => R0], currierAux: CurrierAux[HNil, P]) = new Currier[F] {
    type Out = currierAux.Out[R0]
    type R = R0
    def apply(f: F) = currierAux(hlisterAux(f), HNil)
  }
}

trait HFunction[P <: HList, R]

// TODO(alek): fix variance
abstract class :~>:[F[_], G](implicit params: TransParams[G]) extends (F ~> Const[G]#L) {
  def at[T](f: params.P[T])(implicit currier: Currier[params.P[T]]) = new Case1[F[T]] { type R = currier.Out; val value = currier(f) }
}

trait TransParams[G] {
  type P[T] <: HList
  type R[T]
}

type TransParamsAux[G0, P0[_] <: HList, R0[_]] = TransParams[G0] { type P[T] = P0[T]; type R[T] = R0[T] }

implicit def foo[F[_], G[_]] = new TransParams[F ~> G] { type P[T] = F[T] :: HNil; type R[T] = G[T] }

implicit def bar[F[_], G, P0[_] <: HList, R0[_]](implicit tailParams: TransParamsAux[G, P0, R0]) = new TransParams[F :~>: G] { type P[T] = F[T] :: P0[T]; type R[T] = R0[T] }

object fold1 extends (Seq :~>: Seq ~> Seq) { def apply[T](f: Seq[T]) = new (Seq ~> Seq) { def apply[U](g: Seq[U]) = g } }

object fold2 extends (Seq :~>: Id :~>: (Id :~>: Id ~> Id)#F ~> Id) {
  implicit def bar[T] = at[T]((list, start, f) => list.fold(start)(f))
}

object fold3 extends (Seq @| _0 :~~>: Id @| _1 :~~>: (Id @| _1 :~~>: Id @| _0 ~~> Id @| _1)#F ~~> Id @| _1) {
  implicit def bar[T] = at[T]((list, start, f) => list.fold(start)(f))
}*/

//fold(Seq(1, 2), "", { case (total, cur) => s"$total, $cur" })

//def fold[A, B](list: Seq[A], start: B, f: (A, B) => B): B = list.fold(start)(f)

// TODO(alek): suggest supporting names.map(s"Hello, $_!"), or at least ${_} (for backward compatibility?)
// TODO(alek): template string processor, like ERB (or Tornado templates) for Scala - builtins like map, etc
// import TemplateProcessor._ or val templateProcessor = TemplateProcessor(autoescape = Html.escape); import templateProcessor._
// tpl"""
//   %{include #otherStuff}
//   %{option autoescape ${Html.escape}} @{figure out precedence of inline vs. user-specified}
//   %{macro foo ${fooMacro}} @{where fooMacro: (header: StringProcessor, body: StringProcessor) => String; case class StringProcessor(ctx: StringContext, args: Seq[Any]) - need to also supply values of #variables (and pre-parse them out, like $variables?)}
//   %{foo a #b $c}d #e $f%{end} @{fooMacro(StringProcessor(StringContext("a #b ", ""), c), StringProcessor(StringContext("d #e ", ""), f))}
//   %{def greeting(names, body)} @{untyped - every object in Scala has toString}
//     #{names #map<name>} @{essentially map(names, name => s"Hello, $name!") - to use this notation, the applied function must take as its last parameter an n-ary function - each id }
//       Hello, #name!
//     %{end #mkString(', and ')}
//     #body
//   %end
//   @{make generic with transformations (extends what shapeless has)}
//   %{var fold ${(list: Seq[String], start: String, f: (String, String) => String) => list.foldLeft(start)(f)}}
//   #{names #fold<total,cur>("")}#total, and #cur%{end}
//   #{names #greeting}
//     Welcome to Alek's wonderland
//   %{end -} @{suppress newline with trailing hyphen}
//   @{
//     if a block takes more than one inner block, they can be specified with:
//     #{foo #bar}
//       hi%{block #transform}bye
//     %{end}
//     escape percent signs or hashes by doubling them
//   }
//   @{this is an inline comment}
//   %{var foo 5} @{support some literals}
//   %{var bar}yoyoyo%{end} @{can't enforce val/var distinction at compile-time, so only support var}
//   %{set bar "dog"}
//   %{apply ${capitalize _} #foo}
//   %{apply ${escape _}}yo dog%{end}
//   %{var concat ${_+_}} %{var inc ${_+1}} %{apply #inc #foo} #{foo #inc} #{foo #inc ${escape _} #concat('b')} @{concat(escape(inc(foo)), 'b')
//   %{if #foo}/%{else if #bar}/%{else}/%{end}
//   %{match #foo}
//     %{case #bar}matched exact value #bar%{end}
//     %{case baz}matched anything, and we're calling it 'baz'%{end}
//     %{case qux: ${classOf[Int]}%{end}
//     %{case Hello(bye, dog)}%{end} @{use unapply? might need reflection - can substitute specific values with Hello(#bye, dog) as well}
//   %end
//   %try/%catch%{case ioe: ${classOf[IOException]}}...%end/%finally/%end
//   #{names ${_.filter(_.length < 5)} #map<name>}...%end
//   %{while #foo}/%end
//   %{for foo <- %bar; baz <- $qux}this is #foo, #baz%end @{transform to map/flatMap/etc calls - other statements in for comprehension deprecated, so don't have to support them}
// """