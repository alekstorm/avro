package org.apache.avro.scala

import java.io.{InputStream, OutputStream}
import java.nio.ByteBuffer

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import shapeless.{::, Field => SField, HList, HNil}

import org.apache.avro.io.{DecoderFactory, Encoder, EncoderFactory}

import union._

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

abstract class NamedField[T](val name: String) extends SField[T]

package object codecs {
  // point-free type combinators
  type Const1[T] = { type L[U] = T }
  type Const2[T] = { type L[U, V] = T }
  type Id[T] = T
  type Curry2[C[_,_], T] = { type L[U] = C[T, U] }
  type Curry3[C[_,_,_], T] = { type L[U, V] = C[T, U, V] }
  type Switch[C[_,_]] = { type L[T, U] = C[U, T] }
  type Compose1[C[_], A[_]] = { type L[T] = C[A[T]] }
  type Compose2[C[_,_], A[_], B[_]] = { type L[T] = C[A[T], B[T]] }

  sealed class Sentinel
  implicit def sentinel = new Sentinel

  trait RecordCodec[R <: HList] {
    def build(writer: RecordEncoder, value: R)
  }

// TODO(alek): HMap -> Map conversion, HMap -> HList of values conversion
object RecordCodec {
  implicit def recordHCons[V, K <: NamedField[V], T <: HList](implicit key: Singleton[K], headTranslator: Translator[V], tailTranslator: RecordCodec[T]) = new RecordCodec[(K, V) :: T] {
    def build(builder: RecordEncoder, value: (K, V) :: T) {
      headTranslator.write(builder.field(value.head._1.name), value.head._2)
      tailTranslator.build(builder, value.tail)
    }
  }

  implicit def recordHNil = new RecordCodec[HNil] {
    def build(builder: RecordEncoder, value: HNil) {}
  }
}

// TODO(alek): object JsonCodec extends Codec

// TODO(alek): macros to generate Poly's of any arity
// TODO(alek): propose that implicits whose types are covariant be treated as such when searching implicit scope - trait A[+T]; implicit def foo = new A[Any] {}; implicitly[A[String]] should work
// TODO(alek): pool of previously-built objects (use finalizer to track when they would get garbage-collected), pass around as implicit parameter (with default) to decode functions
// TODO(alek): apparently finalizers add a huge performance penalty, so instead manage object lifecycle explicitly with Repr.release() and/or passing a block in which the repr is used, after which it is returned to the pool (multi-threading?)
// TODO(alek): use cases for overriding ints/strings: special classes for database IDs or unsanitized user input
trait Translator[V] {
  def write(writer: Writer, value: V)
}

object Translator {
  trait UnionCodec[L <: HList] {
    def build(builder: UnionEncoder, value: L, idx: Int)
  }

  object UnionCodec {
    // TODO(alek): ensure no duplicates
    implicit def unionHCons[H, T <: HList](implicit headTranslator: Translator[H], tailTranslator: UnionCodec[T]) = new UnionCodec[Option[H] :: T] {
      def build(builder: UnionEncoder, value: Option[H] :: T, idx: Int) {
        value.head match {
          case Some(v) => headTranslator.write(builder.create(idx), v)
          case None => tailTranslator.build(builder, value.tail, idx + 1)
        }
      }
    }

    implicit def unionHNil = new UnionCodec[HNil] {
      def build(builder: UnionEncoder, value: HNil, idx: Int) { sys.error("should never happen") }
    }
  }

  // TODO(alek): report possible bug - if this doesn't get an explicit return type annotation, the compiler infers Phantom1[L with AvroRecord] with Translator[L with AvroRecord]
  // TODO(alek): report bug that implicit search doesn't work when Translator subclasses Phantom1 and either a BinaryCodec.Pullback1 or Pullback1Aux is searched for
  implicit def recordCodec[L <: HList](implicit recordTranslator: RecordCodec[L]) = new Translator[RecordDescriptor[L]] {
    def write(writer: Writer, value: RecordDescriptor[L]) {
      writer.writeRecord(recordEncoder => recordTranslator.build(recordEncoder, value.instance))
    }
  }

  // TODO(alek): look into HLists as monads (syntactic sugar)
  // TODO(alek): figure out how to make Option[HList].map(Poly1) work - lift Option into something
  // TODO(alek): map with multiple functions, like encode and decode
  // TODO(alek): enums need to be separate types
  // TODO(alek): report bug: type inference doesn't work here, even though implicitly[Seq[E] <:< Seq[_]]
  implicit def arrayCodec[E](implicit translator: Translator[E]) = new Translator[Seq[E]] {
    def write(writer: Writer, elements: Seq[E]) {
      writer.writeArray(arrayEncoder => elements.foreach(element => translator.write(arrayEncoder.item(), element)))
    }
  }
  implicit def mapCodec[V](implicit translator: Translator[V]) = new Translator[Map[String, V]] {
    def write(writer: Writer, elements: Map[String, V]) {
      writer.writeMap(mapEncoder => elements.foreach { case (key, value) => translator.write(mapEncoder.item(key), value) })
    }
  }
  implicit def unionCodec[L <: HList](implicit unionTranslator: UnionCodec[L]) = new Translator[UnionDescriptor[L]] {
    def write(writer: Writer, value: UnionDescriptor[L]) {
      writer.writeUnion(unionEncoder => unionTranslator.build(unionEncoder, value.instance, 0))
    }
  }
  // TODO(alek): fix scala implicit search bugs so Constrained[MaxNat[N]] can be used for enums
  implicit def primitiveCodec[T](implicit ev: Primitives[T]) = new Translator[T] {
    def write(writer: Writer, value: T) {
      writer.writePrimitive(value)
    }
  }
}

  /*implicit class RichHList(l: HList) {
    def toList: List[Any] = {
      l match {
        case head :: tail => head +: tail.toList
        case HNil => List()
      }
    }
  }*/

  type Primitives[T] = (Int | Long | String | Float | Double | Unit | Boolean | Array[Byte])#L[T]

  // TODO(alek): && and || logical operators for implicits - literal 'true' and 'false' represented by a sealed typeclass with one implicit method and a sealed typeclass with two ambiguous implicit methods, respectively
  trait Writer {
    type W1[T]; type W2[T]; type W3[T]; type W4; type W5
    def iterator: Iterator[Byte]
    def writePrimitive[V](value: V)(implicit w1: Primitives[V], w2: W1[V])
    def writeMap[E](f: MapEncoder[E] => Unit)(implicit w: W2[E])
    def writeArray[E](f: ArrayEncoder[E] => Unit)(implicit w: W3[E])
    def writeUnion(f: UnionEncoder => Unit)(implicit w: W4)
    def writeRecord(f: RecordEncoder => Unit)(implicit w: W5)
  }

  trait MapEncoder[T] {
    def item(key: String): Writer
  }

  trait ArrayEncoder[T] {
    def item(): Writer
  }

  trait UnionEncoder {
    def create(idx: Int): Writer
  }

  trait RecordEncoder {
    def field(key: String): Writer
  }

  trait SingleEncoder {
    def create(): Writer
  }

  class BinarySingleEncoder extends SingleEncoder {
    protected val writer = new BinaryWildcardWriter(new ListBuffer[Byte]().iterator)
    def create() = writer
  }

  protected val underlyingEncoder = EncoderFactory.get().directBinaryEncoder(new ByteIteratorOutputStream, null) // dummy OutputStream
  protected def jEncoder(f: Encoder => Unit): Iterator[Byte] = {
    val output = new ByteIteratorOutputStream
    f(EncoderFactory.get().directBinaryEncoder(output, underlyingEncoder))
    output.iterator
  }

  protected val underlyingDecoder = DecoderFactory.get().directBinaryDecoder(new ByteIteratorInputStream(Iterator.empty), null) // dummy InputStream
  protected def jDecoder(input: Iterator[Byte]) = DecoderFactory.get().directBinaryDecoder(new ByteIteratorInputStream(input), underlyingDecoder)

  abstract class BinaryWriter(buffer: Iterator[Byte]) extends Writer {
    def iterator = buffer
    def writePrimitive[T](value: T)(implicit w1: Primitives[T], w2: W1[T]) {
      buffer ++= (value match {
        case v: Int => jEncoder(_.writeInt(v))
        case v: Long => jEncoder(_.writeLong(v))
        case v: String => jEncoder(_.writeString(v))
        case v: Float => jEncoder(_.writeFloat(v))
        case v: Double => jEncoder(_.writeDouble(v))
        case v: Unit => jEncoder(_.writeNull())
        case v: Boolean => jEncoder(_.writeBoolean(v))
        case v: Array[Byte] => jEncoder(_.writeBytes(v))
        case v: AvroEnum => jEncoder(_.writeEnum(v.value))
        case v: AvroFixed => jEncoder(_.writeFixed(v.bytes))
      })
    }
    def writeMap[E](f: MapEncoder[E] => Unit)(implicit w: W2[E]) {
      val writer = new BinaryMapEncoder[E]
      f(writer)
      jEncoder(e => {
        e.writeMapStart()
        // TODO(alek): ripe for optimization
        e.setItemCount(writer.size)
      }) ++ writer.buffer ++ jEncoder(_.writeMapEnd())
    }
    def writeArray[E](f: ArrayEncoder[E] => Unit)(implicit w: W3[E]) {
      val writer = new BinaryArrayEncoder[E]
      f(writer)
      buffer ++= jEncoder { e => e.writeArrayStart(); e.setItemCount(writer.size) } ++ writer.buffer ++ jEncoder(_.writeArrayEnd())
    }
    def writeUnion(f: UnionEncoder => Unit)(implicit w: W4) {
      val writer = new BinaryUnionEncoder
      f(writer)
      buffer ++= writer.buffer
    }
    def writeRecord(f: RecordEncoder => Unit)(implicit w: W5) {
      val writer = new BinaryRecordEncoder
      f(writer)
      buffer ++= writer.iterator
    }
  }

  class BinaryWildcardWriter(buffer: Iterator[Byte]) extends BinaryWriter(buffer) {
    override type W1[T] = Sentinel
    override type W2[T] = Sentinel
    override type W3[T] = Sentinel
    override type W4 = Sentinel
    override type W5 = Sentinel
  }

  class BinaryCollectionWriter[T](buffer: Iterator[Byte]) extends BinaryWriter(buffer) {
    override type W2[U] = MapWitness[T]
    override type W3[U] = ArrayWitness[T]
    override type W4 = UnionWitness[T]
    override type W5 = RecordWitness[T]
  }

  class BinaryMapEncoder[T] extends MapEncoder[T] {
    protected[codecs] val buffer = ListBuffer[Byte]()
    protected[codecs] var size = 0
    def item(key: String) = {
      size += 1
      buffer ++= jEncoder { e => e.startItem(); e.writeString(key) }
      new BinaryCollectionWriter(buffer.iterator)
    }
  }

  type MapWitnessAux[T, U] = Compose2[=:=, Const1[T]#L, Curry2[Map, String]#L]#L[U]
  type MapWitness[T] = Curry2[MapWitnessAux, T]
  type ArrayWitness[T] = Compose2[=:=, Const1[T]#L, Array]#L[T]
  type UnionWitness[T] = Curry2[=:=, UnionDescriptor]#L[T]
  type RecordWitness[T] = Curry2[=:=, RecordDescriptor]#L[T]

  class BinaryArrayEncoder[T] extends ArrayEncoder[T] {
    protected[codecs] val buffer = ListBuffer[Byte]()
    protected[codecs] var size = 0
    def item() = {
      size += 1
      buffer ++= jEncoder(_.startItem())
      new BinaryCollectionWriter(buffer.iterator)
    }
  }

  class BinaryUnionEncoder extends UnionEncoder {
    protected[codecs] val buffer = ListBuffer[Byte]()
    def create(idx: Int) = {
      buffer ++= jEncoder(_.writeIndex(idx))
      new BinaryWildcardWriter(buffer.iterator)
    }
  }

  // TODO(alek): use ST monad to prevent references to builders from leaking out
  class BinaryRecordEncoder extends RecordEncoder {
    protected[codecs] val buffer = ListBuffer[Byte]()
    def iterator = buffer.iterator
    def field(key: String) = new BinaryWildcardWriter(buffer.iterator)
  }

  implicit val binaryCodec = new Codec {
    def encoder = new BinarySingleEncoder
    //def decoder = new BinarySingleDecoder
  }

  trait Codec {
    def encoder: SingleEncoder
    //def decoder: SingleDecoder
  }

  object Codec {
    def encode[T](value: T)(implicit codec: Codec, translator: Translator[T]): Iterator[Byte] = {
      val writer = codec.encoder.create()
      translator.write(writer, value)
      writer.iterator
    }
  }

  implicit class RichList(l: List) {
    def toHList: HList = l.foldRight[HList](HNil)(_ :: _)
  }

  // TODO(alek): propose manifests typesafe in their number and upper bounds of type parameters (like Manifest2[Any :: HList], or Manifest2[::[_,_], Any, HList])
  implicit class RichHListManifest(m: Manifest[HList]) {
    def htypes: List[Manifest[_]] = {
      if (m <:< Manifest[Any :: HList]) // TODO(alek): support _ wildcards with infix types, like '_ :: HList'
        m.typeArguments(0) +: m.typeArguments(1).asInstanceOf[Manifest[HList]].htypes
      else
        List()
    }
  }

  // TODO(alek): auto-curried arguments in Scala 3 (or tools to achieve it), like Haskell
  /*implicit val binaryCodec = new Codec {
  // TODO(alek): find out if virtualized pattern matching can enable things like 'case Value(RecordBaseSchema(name, fields, namespace), value)', for 'object Value { def unapply(schema: Schema)(value: schema.T) = Some((schema, value)) }', and polymorphic unapply methods (basically, non-stable identifiers)
    def decodeFoo(manifest: Manifest[_], input: Iterator[Byte]): Any = {
      manifest match {
        case IntSchema.Reader() => decoder(input).readInt()
        case LongSchema.Reader() => decoder(input).readLong()
        case StringSchema.Reader() => decoder(input).readString()
        case RecordBaseSchema.Reader() => manifest.typeArguments.map(decodeFoo(_, input))
        case UnionSchema.Reader() => {
          val idx = decoder(input).readIndex()
          val unionManifest = manifest.typeArguments.head.asInstanceOf[Manifest[HList]].htypes(idx)
          decodeFoo(unionManifest, input)
        }
        case ArraySchema.Reader() => {
          var blockSize = decoder(input).readArrayStart().toInt // FIXME(alek): support longs
          val array = new Array[Any](blockSize)
          while (blockSize > 0) {
            for (i <- intWrapper(0) until blockSize)
              array(i) = decodeFoo(manifest.typeArguments(0), input)
            blockSize = decoder(input).arrayNext().toInt
          }
          wrapRefArray(array).toSeq
        }
        case MapSchema.Reader() => {
          var blockSize = decoder(input).readMapStart().toInt
          val map = mutable.Map[String, V]()
          while (blockSize > 0) {
            for (_ <- intWrapper(0) until blockSize)
              map(decoder(input).readString()) = decodeFoo(manifest.typeArguments(0), input)
            blockSize = decoder(input).mapNext().toInt
          }
          map.toMap
        }
        case BytesSchema.Reader() => {
          val buffer = ByteBuffer.allocate(0)
          decoder(input).readBytes(buffer)
          buffer.array
        }
        case FixedSchema.Reader() => {
          val buffer = new Array[Byte](1024) // TODO(alek): fix this
          decoder(input).readFixed(buffer)
          new AvroFixed(buffer)
        }
        case BooleanSchema.Reader() => decoder(input).readBoolean()
        case NullSchema.Reader() => { decoder(input).readNull(); () }
        case FloatSchema.Reader() => decoder(input).readFloat()
        case DoubleSchema.Reader() => decoder(input).readDouble()
      }
    }
  }*/
}
