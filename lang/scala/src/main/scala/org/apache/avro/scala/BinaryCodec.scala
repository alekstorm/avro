package org.apache.avro.scala

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.ByteBuffer

import scala.collection.mutable
import scala.reflect.ClassTag

import org.apache.avro.io.{BinaryEncoder, DecoderFactory, EncoderFactory}
import shapeless.{-> => _, Field => _, _}

import record.{Field => TField, _}
import tag._

//object JsonCodec extends Codec

trait BinaryCodec[V] extends ValueCodec[V]

// TODO(alek): propose that implicits whose types are covariant be treated as such when searching implicit scope - trait A[+T]; implicit def foo = new A[Any] {}; implicitly[A[String]] should work
object BinaryCodec extends Codec {
  type SubCodec[V] = BinaryCodec[V]

  trait UnionCodec[L <: HList] {
    def encode(value: L, index: Int): Iterator[Byte]
    def decode(input: Iterator[Byte], index: Int): L
  }

  object UnionCodec {
    // TODO(alek): ensure no duplicates
    implicit def hcons[H, T <: HList](implicit headCodec: BinaryCodec[H], tailCodec: UnionCodec[T]) = new UnionCodec[Option[H] :: T] {
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

  trait RecordCodec[R <: HList] extends BinaryCodec[R]

  object RecordCodec {
    implicit def hcons[V, K <: TField[V], T <: HList](implicit headCodec: BinaryCodec[V], tailCodec: RecordCodec[T]) = new RecordCodec[(K -> V) :: T] {
      def encode(value: (K -> V) :: T) = headCodec.encode(value.head) ++ tailCodec.encode(value.tail)
      def decode(input: Iterator[Byte]) = tag.tag[V, K](headCodec.decode(input)) :: tailCodec.decode(input)
    }

    implicit def hnil = new RecordCodec[HNil] {
      def encode(last: HNil) = Iterator.empty
      def decode(input: Iterator[Byte]) = HNil // TODO(alek): check string empty
    }
  }

  private val underlyingEncoder = EncoderFactory.get().directBinaryEncoder(new ByteArrayOutputStream(), null) // dummy OutputStream
  private def encoder(f: BinaryEncoder => Unit) = {
    val output = new ByteArrayOutputStream()
    f(EncoderFactory.get().directBinaryEncoder(output, underlyingEncoder))
    output.toByteArray.toIterator
  }

  private val underlyingDecoder = DecoderFactory.get().directBinaryDecoder(new ByteArrayInputStream(Array()), null) // dummy InputStream
  private def decoder(input: Iterator[Byte]) = DecoderFactory.get().directBinaryDecoder(new ByteArrayInputStream(input.toArray), underlyingDecoder)

  implicit def recordCodec[R <: HList, T <: R @@ AvroRecord](implicit recordCodec: RecordCodec[R]) = new BinaryCodec[T] {
    def encode(value: T) = recordCodec.encode(value)
    def decode(input: Iterator[Byte]) = recordCodec.decode(input).asInstanceOf[T]
  }

  // TODO(alek): convert to Poly1
  // TODO(alek): look into HLists as monads (syntactic sugar)
  // TODO(alek): figure out how to make Option[HList].map(Poly1) work
  // TODO(alek): map with multiple functions, like encode and decode
  implicit def arrayCodec[E](implicit codec: BinaryCodec[E], m: ClassTag[E]) = new BinaryCodec[Seq[E] @@ AvroArray] {
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
      array.toSeq @@ AvroArray
    }
  }

  implicit def mapCodec[V](implicit codec: BinaryCodec[V], m: ClassTag[V]) = new BinaryCodec[Map[String, V] @@ AvroMap] {
    def encode(map: Map[String, V] @@ AvroMap) = ???
    def decode(input: Iterator[Byte]) = {
      var blockSize = decoder(input).readMapStart().toInt
      val map = mutable.Map[String, V]()
      while (blockSize > 0) {
        for (_ <- 0 until blockSize)
          map += (decoder(input).readString() -> codec.decode(input))
        blockSize = decoder(input).mapNext().toInt
      }
      map.toMap @@ AvroMap
    }
  }

  implicit def unionCodec[L <: HList @@ AvroUnion](implicit unionCodec: UnionCodec[L]) = new BinaryCodec[L] {
    def encode(union: L) = unionCodec.encode(union, 0)
    def decode(input: Iterator[Byte]) = unionCodec.decode(input, decoder(input).readIndex())
  }

  implicit def booleanCodec = new BinaryCodec[Boolean] {
    def encode(boolean: Boolean) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readBoolean()
  }

  implicit def bytesCodec = new BinaryCodec[Seq[Byte] @@ AvroBytes] {
    def encode(bytes: Seq[Byte] @@ AvroBytes) = ???
    def decode(input: Iterator[Byte]) = {
      val buffer = ByteBuffer.allocate(0)
      decoder(input).readBytes(buffer)
      buffer.array.toSeq @@ AvroBytes
    }
  }

  implicit def doubleCodec = new BinaryCodec[Double] {
    def encode(double: Double) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readDouble()
  }

  implicit def readFloatCodec = new BinaryCodec[Float] {
    def encode(float: Float) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readFloat()
  }

  implicit def enumCodec[L <: HList @@ AvroUnion](implicit enumCodec: EnumCodec[L]) = new BinaryCodec[L] {
    def encode(union: L) = enumCodec.encode(union, 0)
    def decode(input: Iterator[Byte]) = enumCodec.decode(input, decoder(input).readEnum())
  }

  /*implicit def readFixed = new BinaryCodec[FixedArray[Byte]] {
    val buffer = new Array[Byte](1024) // TODO(alek): fix this
    decoder(input).readFixed(buffer)
    buffer
  }*/

  implicit def intCodec = new BinaryCodec[Int] {
    def encode(value: Int) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readInt()
  }

  implicit def longCodec = new BinaryCodec[Long] {
    def encode(value: Long) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readLong()
  }

  implicit def nullCodec = new BinaryCodec[Unit] {
    def encode(unit: Unit) = ???
    def decode(input: Iterator[Byte]) = { decoder(input).readNull(); () }
  }

  implicit def stringCodec = new BinaryCodec[String] {
    def encode(string: String) = ???
    def decode(input: Iterator[Byte]) = decoder(input).readString()
  }
}
