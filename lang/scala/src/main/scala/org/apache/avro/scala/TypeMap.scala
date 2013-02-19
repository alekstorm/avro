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

object TypeMap {
  def apply(schema: Schema, inside: Boolean = true): String = {
    schema match {
      case NullSchema => "Unit"
      case BooleanSchema => "Boolean"
      case IntSchema => "Int"
      case LongSchema => "Long"
      case FloatSchema => "Float"
      case DoubleSchema => "Double"
      case EnumSchema(name, symbols, namespace) => name
      case StringSchema => "String"
      case FixedSchema(name, size, namespace) => "FixedSeq[Byte]" //FixedSeq[N${size}][Byte] // TODO(alek): use name
      case BytesSchema => "Seq[Byte]"
      case ArraySchema(elements) => "AvroArray[%s]".format(this(elements))
      case MapSchema(values) => "AvroMap[%s]".format(this(values))
      case record: RecordBaseSchema => if (inside) "T$"+record.name else record.name+"$Record.Descriptor"
      case UnionSchema(types) => {
        val mappedTypes = types.map(this(_))
        if ( mappedTypes.size == 0 )
          "Nothing"
        else if ( mappedTypes.size == 1 )
          mappedTypes.head
        else
          "(" + mappedTypes.mkString(" | ") + ")#L"
      }
    }
  }
}
