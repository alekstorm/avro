package org.apache.avro.scala

import shapeless._

import org.apache.avro.{Schema => JSchema}
import org.apache.avro.ipc.Transceiver
import org.apache.avro.generic.GenericData

import codecs._

trait Codec {
  def encode[L <: HList](value: RecordDescriptor[L])(implicit translator: Translator[RecordDescriptor[L]]): Iterator[Byte]
  def decode[L <: HList](input: Iterator[Byte])(implicit translator: Translator[RecordDescriptor[L]]): RecordDescriptor[L]
}

// TODO(alek): propose unboxed wrapper classes for AnyRefs in addition to AnyVals (basically newtype)

class AvroEnum(val value: Int)
class AvroFixed(val bytes: Array[Byte])

class RecordDescriptor[+S <: HList](val instance: S)
class UnionDescriptor[+S <: HList](val instance: S)
trait ProtocolDescriptor {
  type Shape <: HList
}

// TODO(alek): propose Manifest-style typeclass to get singleton instance of companion object type at runtime - useful for this, at least
trait Singleton[S] {
  val instance: S
}

package object generated {
  type AvroMap[V] = Map[String, V]

  def createObject(jSchema: JSchema, attrs: Map[String, AnyRef]): AnyRef = {
    val record = new GenericData.Record(jSchema)
    attrs.foreach { case (key, value) => record.put(key, value) }
    record
  }

  trait Presenter[R] {
    type D
    def to(repr: R): D
    def from(descriptor: D): R
  }
  object Presenter {
    implicit def iso[D0] = new Presenter[D0] {
      type D = D0
      def to(descriptor: D0): D = descriptor
      def from(descriptor: D): D0 = descriptor
    }
  }
  type PresenterAux[T, D0] = Presenter[T] { type D = D0 }
}

trait Server[P <: ProtocolDescriptor] {
  val handlers: Map[String, (P#Shape, Seq[Iterator[Byte]]) => Iterator[Byte]]
  val protocol: Protocol
}

trait Client[P <: ProtocolDescriptor] {
  def senders(transceiver: Transceiver): P#Shape
}

trait ProtocolHandler[P <: ProtocolDescriptor] {
  def impl$: P#Shape
}

