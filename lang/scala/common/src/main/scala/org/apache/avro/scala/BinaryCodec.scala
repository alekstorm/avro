package org.apache.avro.scala

import java.io.{InputStream, OutputStream}
import java.nio.ByteBuffer

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

import shapeless.{::, Field => SField, HList, HNil}

import org.apache.avro.io.{DecoderFactory, Encoder, EncoderFactory}

class ByteIteratorInputStream(iterator: Iterator[Byte]) extends InputStream {
  override def read(): Int = if (iterator.hasNext) iterator.next() else -1
}

class ByteIteratorOutputStream extends OutputStream with Iterable[Byte] {
  private val buffer = ListBuffer[Byte]()
  override def write(i: Int) { buffer.append(i.toByte) }
  override def iterator: Iterator[Byte] = buffer.iterator /*new Iterator[Byte] {
    override def next(): Byte =
  }*/
}

// TODO(alek): object JsonCodec extends Codec

// TODO(alek): macros to generate Poly's of any arity
// TODO(alek): propose that implicits whose types are covariant be treated as such when searching implicit scope - trait A[+T]; implicit def foo = new A[Any] {}; implicitly[A[String]] should work
// TODO(alek): pool of previously-built objects (use finalizer to track when they would get garbage-collected), pass around as implicit parameter (with default) to decode functions
// TODO(alek): apparently finalizers add a huge performance penalty, so instead manage object lifecycle explicitly with Repr.release() and/or passing a block in which the repr is used, after which it is returned to the pool (multi-threading?)
// TODO(alek): use cases for overriding ints/strings: special classes for database IDs or unsanitized user input
trait BinaryCodec extends Codec[BinaryCodec]

object BinaryCodec extends BinaryCodec {
  trait UnionCodec[L <: HList] {
    def encode(value: L, index: Int): Iterator[Byte]
    def decode(input: Iterator[Byte], index: Int): L
  }

  object UnionCodec {
    // TODO(alek): ensure no duplicates
    implicit def unionHCons[H, T <: HList](implicit headCodec: ValueCodec[BinaryCodec, H], tailCodec: UnionCodec[T]) = new UnionCodec[Option[H] :: T] {
      def encode(value: Option[H] :: T, index: Int): Iterator[Byte] = value.head match {
        case Some(h) => encoder(_.writeIndex(index)) ++ headCodec.encode(h)
        case None => tailCodec.encode(value.tail, index + 1)
      }
      def decode(input: Iterator[Byte], index: Int) = (if (index == 0) Some(headCodec.decode(input)) else None) :: tailCodec.decode(input, index - 1)
    }

    implicit def unionHNil = new UnionCodec[HNil] {
      def encode(value: HNil, index: Int): Iterator[Byte] = sys.error("should never happen")
      def decode(input: Iterator[Byte], index: Int) = sys.error("should never happen")
    }
  }

  trait RecordCodec[R <: HList] {
    def encode(value: R): Iterator[Byte]
    def decode(input: Iterator[Byte]): R
  }

  object RecordCodec {
    implicit def recordHCons[V, K <: SField[V], T <: HList](implicit key: Singleton[K], headCodec: ValueCodec[BinaryCodec, V], tailCodec: RecordCodec[T]) = new RecordCodec[(K, V) :: T] {
      def encode(value: (K, V) :: T) = headCodec.encode(value.head._2) ++ tailCodec.encode(value.tail)
      def decode(input: Iterator[Byte]) = (key.instance -> headCodec.decode(input)) :: tailCodec.decode(input)
    }

    implicit def recordHNil = new RecordCodec[HNil] {
      def encode(last: HNil) = Iterator.empty
      def decode(input: Iterator[Byte]) = HNil
    }
  }

  private val underlyingEncoder = EncoderFactory.get().directBinaryEncoder(new ByteIteratorOutputStream, null) // dummy OutputStream
  private def encoder(f: Encoder => Unit): Iterator[Byte] = {
    val output = new ByteIteratorOutputStream
    f(EncoderFactory.get().directBinaryEncoder(output, underlyingEncoder))
    output.iterator
  }

  private val underlyingDecoder = DecoderFactory.get().directBinaryDecoder(new ByteIteratorInputStream(Iterator.empty), null) // dummy InputStream
  private def decoder(input: Iterator[Byte]) = DecoderFactory.get().directBinaryDecoder(new ByteIteratorInputStream(input), underlyingDecoder)

  // TODO(alek): report possible bug - if this doesn't get an explicit return type annotation, the compiler infers Phantom1[L with AvroRecord] with ValueCodec[L with AvroRecord]
  // TODO(alek): report bug that implicit search doesn't work when ValueCodec subclasses Phantom1 and either a BinaryCodec.Pullback1 or Pullback1Aux is searched for
  implicit def recordCodec[L <: HList](implicit recordCodec: RecordCodec[L]) = new ValueCodec[BinaryCodec, RecordThing[L]] {
    def encode(value: RecordThing[L]): Iterator[Byte] = recordCodec.encode(value.instance)
    def decode(input: Iterator[Byte]): RecordThing[L] = new RecordThing(recordCodec.decode(input))
  }

  // TODO(alek): look into HLists as monads (syntactic sugar)
  // TODO(alek): figure out how to make Option[HList].map(Poly1) work - lift Option into something
  // TODO(alek): map with multiple functions, like encode and decode
  // TODO(alek): enums need to be separate types
  implicit def arrayCodec[E <: AnyRef](implicit codec: ValueCodec[BinaryCodec, E], m: ClassTag[E]) = new ValueCodec[BinaryCodec, Seq[E]] {
    def encode(value: Seq[E]) = {
      val header = encoder { encoder =>
        encoder.writeArrayStart()
        encoder.setItemCount(value.size)
      }
      val items = value.map(item => encoder(_.startItem()) ++ codec.encode(item)).foldLeft(Iterator.empty: Iterator[Byte])(_ ++ _)
      header ++ items ++ encoder(_.writeArrayEnd())
    }

    def decode(input: Iterator[Byte]) = {
      var blockSize = decoder(input).readArrayStart().toInt // FIXME(alek): support longs
      val array = new Array[E](blockSize)
      while (blockSize > 0) {
        for (i <- intWrapper(0) until blockSize)
          array(i) = codec.decode(input)
        blockSize = decoder(input).arrayNext().toInt
      }
      // TODO(alek): report bug: type inference doesn't work here, even though implicitly[Seq[E] <:< Seq[_]]
      wrapRefArray(array).toSeq
    }
  }

  implicit def mapCodec[V](implicit codec: ValueCodec[BinaryCodec, V], m: ClassTag[V]) = new ValueCodec[BinaryCodec, Map[String, V]] {
    def encode(value: Map[String, V]) = {
      val header = encoder { encoder =>
        encoder.writeMapStart()
        encoder.setItemCount(value.size)
      }
      val items = value.map {
        case (mapKey, mapValue) => {
          val itemHeader = encoder { encoder =>
            encoder.startItem()
            encoder.writeString(mapKey)
          }
          itemHeader ++ codec.encode(mapValue)
        }
      }.foldLeft(Iterator.empty: Iterator[Byte])(_ ++ _)
      header ++ items ++ encoder(_.writeMapEnd())
    }

    def decode(input: Iterator[Byte]) = {
      var blockSize = decoder(input).readMapStart().toInt
      val map = mutable.Map[String, V]()
      while (blockSize > 0) {
        for (_ <- intWrapper(0) until blockSize)
          map(decoder(input).readString()) = codec.decode(input)
        blockSize = decoder(input).mapNext().toInt
      }
      map.toMap
    }
  }

  implicit def unionCodec[L <: HList](implicit unionCodec: UnionCodec[L]) = new ValueCodec[BinaryCodec, UnionThing[L]] {
    def encode(value: UnionThing[L]) = unionCodec.encode(value.instance, 0)
    def decode(input: Iterator[Byte]) = new UnionThing(unionCodec.decode(input, decoder(input).readIndex()))
  }

  implicit def booleanCodec = new ValueCodec[BinaryCodec, Boolean] {
    def encode(value: Boolean) = encoder(_.writeBoolean(value))
    def decode(input: Iterator[Byte]) = decoder(input).readBoolean()
  }

  implicit def bytesCodec = new ValueCodec[BinaryCodec, Array[Byte]] {
    def encode(value: Array[Byte]) = encoder(_.writeBytes(value))
    def decode(input: Iterator[Byte]) = {
      val buffer = ByteBuffer.allocate(0)
      decoder(input).readBytes(buffer)
      buffer.array
    }
  }

  implicit def doubleCodec = new ValueCodec[BinaryCodec, Double] {
    def encode(value: Double) = encoder(_.writeDouble(value))
    def decode(input: Iterator[Byte]) = decoder(input).readDouble()
  }

  implicit def floatCodec = new ValueCodec[BinaryCodec, Float] {
    def encode(value: Float) = encoder(_.writeFloat(value))
    def decode(input: Iterator[Byte]) = decoder(input).readFloat()
  }

  // TODO(alek): fix scala implicit search bugs so Constrained[MaxNat[N]] can be used instead
  implicit def enumCodec = new ValueCodec[BinaryCodec, AvroEnum] {
    def encode(value: AvroEnum) = encoder(_.writeEnum(value.value))
    def decode(input: Iterator[Byte]) = new AvroEnum(decoder(input).readEnum())
  }

  implicit def fixedCodec = new ValueCodec[BinaryCodec, AvroFixed] {
    def encode(value: AvroFixed) = encoder(_.writeFixed(value.bytes.toArray))
    def decode(input: Iterator[Byte]) = {
      val buffer = new Array[Byte](1024) // TODO(alek): fix this
      decoder(input).readFixed(buffer)
      new AvroFixed(buffer)
    }
  }

  implicit def intCodec = new ValueCodec[BinaryCodec, Int] {
    def encode(value: Int) = encoder(_.writeInt(value))
    def decode(input: Iterator[Byte]) = decoder(input).readInt()
  }

  implicit def longCodec = new ValueCodec[BinaryCodec, Long] {
    def encode(value: Long) = encoder(_.writeLong(value))
    def decode(input: Iterator[Byte]) = decoder(input).readLong()
  }

  implicit def nullCodec = new ValueCodec[BinaryCodec, Unit] {
    def encode(value: Unit) = encoder(_.writeNull())
    def decode(input: Iterator[Byte]) = { decoder(input).readNull(); () }
  }

  implicit def stringCodec = new ValueCodec[BinaryCodec, String] {
    def encode(value: String) = encoder(_.writeString(value))
    def decode(input: Iterator[Byte]) = decoder(input).readString()
  }
}
