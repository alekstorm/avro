package org.apache.avro.scala

import scala.util.parsing.json.JSONType

import shapeless._

import tag._
import reflect.ClassTag

trait ValueCodec[V] {
  def encode(value: V): Iterator[Byte]
  def decode(input: Iterator[Byte]): V
}

trait Codec {
  type SubCodec[V] <: ValueCodec[V]

  def encode[V](value: V)(implicit codec: SubCodec[V]): Iterator[Byte] = codec.encode(value)
  def decode[V](input: Iterator[Byte])(implicit codec: SubCodec[V]): V = codec.decode(input)

  /*trait UnionCodec[L <: HList] {
    def encode(value: L, index: Int): Iterator[Byte]
    def decode(input: Iterator[Byte], index: Int): L
  }

  object UnionCodec {
    // TODO(alek): ensure no duplicates
    implicit def hcons[H, T <: HList](implicit headCodec: ValueCodec[H], tailCodec: UnionCodec[T]) = new UnionCodec[Option[H] :: T] {
      def encode(value: Option[H] :: T, index: Int): Iterator[Byte] = value.head match {
        case Some(h) => encoder(_.writeIndex(index)) ++ headCodec.encode(h)
        case None => tailCodec.encode(value.tail, index + 1)
      }
      def decode(input: Iterator[Byte], index: Int) = (if (index == 0) Some(headCodec.decode(input)) else None) :: tailCodec.decode(input, index - 1)
    }
    implicit def hnil = new UnionCodec[HNil] {
      def encode(value: HNil, index: Int): Iterator[Byte] = sys.error("should never happen")
      def decode(input: Iterator[Byte], index: Int) = sys.error("should never happen")
    }
  }

  trait EnumCodec[L <: HList] {
    def encode(value: L, index: Int): Iterator[Byte]
    def decode(input: Iterator[Byte], index: Int): L
  }

  object EnumCodec {
    // TODO(alek): ensure no duplicates
    implicit def hcons[T <: HList](implicit tailCodec: EnumCodec[T]) = new EnumCodec[Boolean :: T] {
      def encode(value: Boolean :: T, index: Int): Iterator[Byte] = if (value.head) encoder(_.writeEnum(index)) else tailCodec.encode(value.tail, index + 1)
      def decode(input: Iterator[Byte], index: Int) = (index == 0) :: tailCodec.decode(input, index - 1)
    }
    implicit def hnil = new EnumCodec[HNil] {
      def encode(value: HNil, index: Int): Iterator[Byte] = Iterator.empty
      def decode(input: Iterator[Byte], index: Int) = HNil
    }
  }

  trait RecordCodec[R <: HList] extends ValueCodec[R]

  object RecordCodec {
    implicit def hcons[V, K <: TField[V], T <: HList](implicit headCodec: ValueCodec[V], tailCodec: RecordCodec[T]) = new RecordCodec[(K -> V) :: T] {
      def encode(value: (K -> V) :: T) = headCodec.encode(value.head) ++ tailCodec.encode(value.tail)
      def decode(input: Iterator[Byte]) = tag.tag[V, K](headCodec.decode(input)) :: tailCodec.decode(input)
    }

    implicit def hnil = new RecordCodec[HNil] {
      def encode(last: HNil) = Iterator.empty
      def decode(input: Iterator[Byte]) = HNil // TODO(alek): check string empty
    }
  }

  implicit def recordCodec[R <: HList, T <: R @@ AvroRecord](implicit recordCodec: RecordCodec[R]): ValueCodec[T]
  implicit def arrayCodec[E](implicit codec: ValueCodec[E], m: ClassTag[E]): ValueCodec[Seq[E] @@ AvroArray]
  implicit def mapCodec[V](implicit codec: ValueCodec[V], m: ClassTag[V]): ValueCodec[Map[String, V] @@ AvroMap]
  implicit def unionCodec[L <: HList @@ AvroUnion](implicit unionCodec: UnionCodec[L]): ValueCodec[L]
  implicit def booleanCodec: ValueCodec[Boolean]
  implicit def bytesCodec: ValueCodec[Seq[Byte] @@ AvroBytes]
  implicit def doubleCodec: ValueCodec[Double]
  implicit def readFloatCodec: ValueCodec[Float]
  implicit def enumCodec[L <: HList @@ AvroUnion](implicit enumCodec: EnumCodec[L]): ValueCodec[L]
  implicit def intCodec: ValueCodec[Int]
  implicit def longCodec: ValueCodec[Long]
  implicit def nullCodec: ValueCodec[Unit]
  implicit def stringCodec: ValueCodec[String]*/
}

// TODO(alek): propose unboxed wrapper classes for AnyRefs in addition to AnyVals (basically newtype) - mostly syntactic sugar for the following
trait AvroArray extends Tag//[Seq[_]] // TODO(alek): Tag[Seq[_ <: AvroValue]]
object AvroArray extends AvroArray
trait AvroBytes extends Tag//[Covariant[Seq[Byte]]]
object AvroBytes extends AvroBytes
trait AvroEnum extends Tag//[Covariant[Nat]]
object AvroEnum extends AvroEnum
trait AvroMap extends Tag//[Covariant[Map[String, _]]]
object AvroMap extends AvroMap
trait AvroRecord extends Tag//[HList]
object AvroRecord extends AvroRecord
trait AvroUnion extends Tag//[Covariant[HList]]
object AvroUnion extends AvroUnion

// TODO(alek): propose Manifest-style typeclass to get singleton instance of companion object type at runtime - useful for this, at least
trait Singleton[S] {
  def instance: S
}

trait Presenter[T, R <: AvroRecord] extends Iso[T, R] {
  def fromJson(json: JSONType): T
}

// TODO(alek): it would be great if we could create a newtype for Map[String, ...], so we didn't have to wrap it
// TODO(alek): use shapeless.Newtype for this, bring back RecordDescriptor as well
trait Rpc[P <: HList] {
  def handlers[C <: Codec]: Map[String, (P, Seq[Iterator[Byte]], C) => String]
}

trait ProtocolHandler[P <: HList] {
  def impl$: P
}
