package org.apache.avro.scala

import shapeless._

import org.apache.avro.ipc.Transceiver

trait ValueCodec[C <: Codec[C], V] {
  def encode(value: V): Iterator[Byte]
  def decode(input: Iterator[Byte]): V
}

trait Codec[C <: Codec[C]] {
  def encode[V](value: V)(implicit codec: ValueCodec[C, V]): Iterator[Byte] = codec.encode(value)
  def decode[V](input: Iterator[Byte])(implicit codec: ValueCodec[C, V]): V = codec.decode(input)
}

// TODO(alek): propose unboxed wrapper classes for AnyRefs in addition to AnyVals (basically newtype)

class AvroEnum(val value: Int)
class AvroFixed(val bytes: Seq[Byte])

class RecordThing[+L <: HList](val instance: L)
class UnionThing[+L <: HList](val instance: L)

// TODO(alek): propose Manifest-style typeclass to get singleton instance of companion object type at runtime - useful for this, at least
trait Singleton[S] {
  val instance: S
}

package object generated {
  trait Presenter[T] {
    type R
    def to(t: T): R
    def from(r: R): T
  }
  object Presenter {
    implicit def iso[R0] = new Presenter[R0] {
      type R = R0
      def to(r: R0): R = r
      def from(r: R): R0 = r
    }
  }
  type PresenterAux[T, R0] = Presenter[T] { type R = R0 }
}

trait Server[P <: ProtocolThing, C <: Codec[C]] {
  val handlers: Map[String, (P#Descriptor, Seq[Iterator[Byte]]) => Iterator[Byte]]
  val protocol: Protocol
}

trait Client[P <: ProtocolThing, C <: Codec[C]] {
  def senders(transceiver: Transceiver): P#Descriptor
}

trait ProtocolHandler[P <: ProtocolThing] {
  def impl$: P#Descriptor
}

trait ProtocolThing {
  type Descriptor <: HList
}
