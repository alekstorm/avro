package org.apache.avro.scala

object DatumEncoder {
  def apply(schema: Schema, value: String): String = {
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
      case EnumSchema(name, symbols, namespace) => s"writeEnum($name.values.zipWithIndex.find(_._1 == $value).get._2)"
      case ArraySchema(elements) => s"new org.apache.avro.scala.AvroArray(${value})"
      case MapSchema(value) => s"new org.apache.avro.scala.AvroMap(${value})"
      case UnionSchema(types) => ""
      // TODO(alek): raise an exception if someone tries to encode a null (at all) instead of a Unit (provide an easy conversion method, like object Unit { def apply[T <: AnyRef](t: T): (Unit | T)#L = if (t == null) () else t }
      // TODO(alek): ErrorSchema
      case RecordSchema(name, fields, namespace) => s"implicitly[org.apache.avro.scala.Codec[T${"$"}$name, ${name}${"$"}Record.Descriptor]].encode($value)"
    }
  }
}
