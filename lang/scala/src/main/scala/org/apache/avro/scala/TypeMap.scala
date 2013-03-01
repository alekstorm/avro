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
