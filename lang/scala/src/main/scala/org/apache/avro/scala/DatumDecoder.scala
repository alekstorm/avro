package org.apache.avro.scala

import CodeStringProcessor._

object DatumDecoder {
  def apply(schema: Schema, decoder: String): String = {
    schema match {
      case NullSchema => code"{$decoder.readNull(); null}"
      case BooleanSchema => code"$decoder.readBoolean()"
      case IntSchema => code"$decoder.readInt()"
      case LongSchema => code"$decoder.readLong()"
      case FloatSchema => code"$decoder.readFloat()"
      case DoubleSchema => code"$decoder.readDouble()"
      case StringSchema => code"$decoder.readString()"
      case FixedSchema(name, size, namespace) => code"{ val bytes = new Array[Byte]($size); $decoder.readFixed(bytes); bytes }"
      case BytesSchema => code"$decoder.readBytes(null).array.toBuffer" // TODO(taton) this is far from optimal
      case EnumSchema(name, symbols, namespace) => code"$name($decoder.readEnum())" // FIXME(alek)
      case ArraySchema(elements) => code"""
        {
          val array = ${TypeMap(elements)}
          var blockSize: Long = $decoder.readArrayStart()
          while(blockSize != 0L) {
            for (_ <- 0L until blockSize) {
              val arrayItem = (
                ${this(elements, decoder)})
              array.append(arrayItem)
            }
            blockSize = $decoder.arrayNext()
          }
          array
        }"""
      case MapSchema(values) => code"""
        {
          val map = ${TypeMap(schema)}
          var blockSize: Long = $decoder.readMapStart()
          while (blockSize != 0L) {
            for (_ <- 0L until blockSize) {
              val key: String = $decoder.readString()
              val value = (
                ${this(values, decoder)})
              map += (key -> value)
            }
            blockSize = $decoder.mapNext()
          }
          map
        }"""
      case RecordSchema(name, fields, namespace) => code"implicitly[T${"$"}$name].decode($decoder)"
      case UnionSchema(types) => code""
    }
  }
}
