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

object DatumEncoder {
  def apply(schema: Schema, value: String, encoder: String): String = {
    schema match {
      case NullSchema => "()"
      case BooleanSchema => value
      case IntSchema => value
      case LongSchema => value
      case FloatSchema => value
      case DoubleSchema => value
      case StringSchema => value
      case FixedSchema(name, size, namespace) => value
      case BytesSchema => value
      case EnumSchema(name, symbols, namespace) => s"$encoder.writeEnum($name.values.zipWithIndex.find(_._1 == $value).get._2)"
      case ArraySchema(elements) => s"new org.apache.avro.scala.AvroArray(${value})"
      case MapSchema(value) => s"new org.apache.avro.scala.AvroMap(${value})"
      case UnionSchema(types) => ""
      // TODO(alek): raise an exception if someone tries to encode a null (at all) instead of a Unit (provide an easy conversion method, like object Unit { def apply[T <: AnyRef](t: T): (Unit | T)#L = if (t == null) () else t }
      // TODO(alek): ErrorSchema
      case RecordSchema(name, fields, namespace) => s"implicitly[org.apache.avro.scala.Codec[T${"$"}$name, ${name}${"$"}Record.Descriptor]].encode($value)"
    }
  }
}
