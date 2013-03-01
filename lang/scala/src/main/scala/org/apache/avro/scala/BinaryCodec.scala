package org.apache.avro.scala

import scala.collection.mutable
import scala.reflect.ClassTag

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, OutputStream}
import java.nio.ByteBuffer

import org.apache.avro.io.{DecoderFactory, EncoderFactory}
import shapeless.{Field => SField, _}

//object JsonCodec extends Codec

// TODO(alek): propose that implicits whose types are covariant be treated as such when searching implicit scope - trait A[+T]; implicit def foo = new A[Any] {}; implicitly[A[String]] should work
object BinaryCodec extends Codec {
  import ConstrainedTag._

  trait UnionDecoder[L <: HList] {
    def apply(input: Iterator[Byte], index: Int): L
  }

  object UnionDecoder {
    implicit def hcons[H, T <: HList](implicit headDecoder: ValueCodec[H], tailCodec: UnionDecoder[T]) = new UnionDecoder[Option[H] :: T] {
      def apply(input: Iterator[Byte], index: Int) = (if (index == 0) Some(headDecoder.decode(input)) else None) :: tailCodec(input, index-1)
    }
    implicit def hnil = new UnionDecoder[HNil] {
      def apply(input: Iterator[Byte], index: Int) = HNil
    }
  }

  private val underlyingEncoder = EncoderFactory.get().directBinaryEncoder(new ByteArrayOutputStream(), null) // dummy OutputStream
  private def encoder(implicit output: OutputStream) = EncoderFactory.get().directBinaryEncoder(output, underlyingEncoder)

  private val underlyingDecoder = DecoderFactory.get().directBinaryDecoder(new ByteArrayInputStream(Array()), null) // dummy InputStream
  private def decoder(input: Iterator[Byte]) = DecoderFactory.get().directBinaryDecoder(new ByteArrayInputStream(input.toArray), underlyingDecoder)

  implicit def recordCodec[R <: Tag[AvroRecord]](implicit recordCodec: RecordCodec[R]) = new ValueCodec[R] {
    def encode(value: R) = recordCodec.encode(value)
    def decode(input: Iterator[Byte]) = recordCodec.decode(input)
  }

  // TODO(alek): convert to Poly1
  // TODO(alek): look into HLists as monads (syntactic sugar)
  // TODO(alek): figure out how to make Option[HList].map(Poly1) work
  // TODO(alek): map with multiple functions, like encode and decode
  implicit def arrayCodec[E](implicit codec: ValueCodec[E], m: ClassTag[E]) = new ValueCodec[Seq[E] @@ AvroArray] {
    def encode(array: Seq[E] @@ AvroArray) = ???
    def decode(input: Iterator[Byte]) = {
      var blockSize = decoder(input).readArrayStart().toInt // FIXME(alek): support longs
      val array = new Array[E](blockSize)
      while (blockSize > 0) {
        for (i <- 0 until blockSize)
          array(i) = codec.decode(input)
        blockSize = decoder(input).arrayNext().toInt
      }
      // TODO(alek): report bug: type inference doesn't work here, even though implicitly[Seq[E] <:< Seq[_]]
      array.toSeq.@@[Seq[_],AvroArray.type](AvroArray)
    }
  }

  implicit def mapCodec[V](implicit codec: ValueCodec[V], m: ClassTag[V]) = new ValueCodec[Map[String, V] @@ AvroMap] {
    def encode(map: Map[String, V] @@ AvroMap) = ???
    def decode(input: Iterator[Byte]) = {
      var blockSize = decoder(input).readMapStart().toInt
      val map = mutable.Map[String, V]()
      while (blockSize > 0) {
        for (_ <- 0 until blockSize)
          map += (decoder(input).readString() -> codec.decode(input))
        blockSize = decoder(input).mapNext().toInt
      }
      map.toMap.@@[Map[String,_],AvroMap.type](AvroMap)
    }
  }

  implicit def unionCodec[L <: HList with Tag[AvroUnion]](implicit unionDecoder: UnionDecoder[L]) = new ValueCodec[L] {
    def encode(union: L) = ???
    def decode(input: Iterator[Byte]) = unionDecoder(input, decoder(input).readIndex())
  }

  implicit def booleanCodec = new ValueCodec[Boolean] {
    def encode(boolean: Boolean) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readBoolean()
  }

  implicit def bytesCodec = new ValueCodec[Seq[Byte] @@ AvroBytes] {
    def encode(bytes: Seq[Byte] @@ AvroBytes) = ???
    def decode(input: Iterator[Byte]) = {
      val buffer = ByteBuffer.allocate(0)
      decoder(input).readBytes(buffer)
      buffer.array.toSeq @@ AvroBytes
    }
  }

  implicit def doubleCodec = new ValueCodec[Double] {
    def encode(double: Double) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readDouble()
  }

  implicit def readFloatCodec = new ValueCodec[Float] {
    def encode(float: Float) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readFloat()
  }

  /*implicit def enumCodec[E <: Tag[AvroEnum]] = new ValueCodec[E] {
    def encode(value: Nat) = ???
    def decode(input: Iterator[Byte]) = toNat[Nat._0](decoder(input).readEnum())
  }*/

  /*implicit def readFixed = new ValueCodec[FixedArray[Byte]] {
    val buffer = new Array[Byte](1024) // TODO(alek): fix this
    decoder(input).readFixed(buffer)
    buffer
  }*/

  implicit def intCodec = new ValueCodec[Int] {
    def encode(value: Int) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readInt()
  }

  implicit def longCodec = new ValueCodec[Long] {
    def encode(value: Long) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readLong()
  }

  implicit def nullCodec = new ValueCodec[Unit] {
    def encode(unit: Unit) = ???
    def decode(input: Iterator[Byte]) = { decoder(input).readNull(); () }
  }

  implicit def stringCodec = new ValueCodec[String] {
    def encode(string: String) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readString()
  }
}
