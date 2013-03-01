package org.apache.avro.scala

import shapeless.{Field => SField, _}

import ConstrainedTag._

trait ValueCodec[V] {
  def encode(value: V): Iterator[Byte]
  def decode(input: Iterator[Byte]): V
}

trait Codec {
  def encode[V](value: V)(implicit codec: ValueCodec[V]): Iterator[Byte] = codec.encode(value)
  def decode[V](input: Iterator[Byte])(implicit codec: ValueCodec[V]): V = codec.decode(input)
}

trait RecordCodec[V] extends ValueCodec[V]

object RecordCodec {
  implicit def hcons[V, K <: SField[V], T <: HList](implicit headCodec: ValueCodec[V], tailCodec: RecordCodec[T], singleton: Singleton[K]) = new ValueCodec[(K, V) :: T] {
    def encode(value: (K, V) :: T) = headCodec.encode(value.head._2) ++ tailCodec.encode(value.tail)
    def decode(input: Iterator[Byte]) = (singleton.instance -> headCodec.decode(input)) :: tailCodec.decode(input)
  }

  implicit def hnil = new ValueCodec[HNil] {
    def encode(last: HNil) = Iterator.empty
    def decode(input: Iterator[Byte]) = HNil // TODO(alek): check string empty
  }
}

// TODO(alek): propose unboxed wrapper classes for AnyRefs in addition to AnyVals (basically newtype) - mostly syntactic sugar for the following
trait AvroArray extends Tag[Covariant[Seq[_]]] // TODO(alek): Tag[Seq[_ <: AvroValue]]
object AvroArray extends AvroArray
trait AvroBytes extends Tag[Covariant[Seq[Byte]]]
object AvroBytes extends AvroBytes
trait AvroEnum extends Tag[Covariant[Nat]]
object AvroEnum extends AvroEnum
trait AvroMap extends Tag[Covariant[Map[String, _]]]
object AvroMap extends AvroMap
trait AvroRecord extends Tag[Covariant[HList]]
object AvroRecord extends AvroRecord
trait AvroUnion extends Tag[Covariant[HList]]
object AvroUnion extends AvroUnion

// TODO(alek): propose Manifest-style typeclass to get singleton instance of companion object type at runtime - useful for this, at least
trait Singleton[S] {
  def instance: S
}

trait RecordDescriptor extends Tag[Covariant[HList]]

trait Presenter[T, R <: RecordDescriptor] extends Iso[T, R]

// TODO(alek): it would be great if we could create a newtype for Map[String, ...], so we didn't have to wrap it
// TODO(alek): use shapeless.Newtype for this, bring back RecordDescriptor as well
trait Rpc[P <: HList] {
  def handlers: Map[String, (P, Seq[Iterator[Byte]], Codec) => String]
}

trait ProtocolHandler[P <: HList] {
  def impl$: P
}
