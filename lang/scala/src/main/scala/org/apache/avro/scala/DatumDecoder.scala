/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
