package org.apache.avro.scala

import scala.collection.JavaConverters._

import org.apache.avro.{Protocol => JProtocol, Schema => JSchema}

case class Field(name: String, value: Schema)

trait Schema { var jSchema: JSchema = null } // hack
case class UnionSchema(types: Seq[SingleSchema]) extends Schema // TODO(alek): use shapeless for stronger guarantees about types

trait SingleSchema extends Schema
case class ArraySchema(elements: Schema) extends SingleSchema
case class MapSchema(values: Schema) extends SingleSchema
case object BooleanSchema extends SingleSchema
case object BytesSchema extends SingleSchema
case object DoubleSchema extends SingleSchema
case object FloatSchema extends SingleSchema
case object IntSchema extends SingleSchema
case object LongSchema extends SingleSchema
case object NullSchema extends SingleSchema
case object StringSchema extends SingleSchema

trait NamedSchema extends SingleSchema { val name: String; val namespace: Option[String] }
case class FixedSchema(name: String, size: Int, namespace: Option[String]) extends NamedSchema
case class EnumSchema(name: String, symbols: Seq[String], namespace: Option[String]) extends NamedSchema

trait FieldsSchema extends SingleSchema { val fields: Seq[Field] }
trait RecordBaseSchema extends NamedSchema with FieldsSchema
case class ErrorSchema(name: String, fields: Seq[Field], namespace: Option[String]) extends RecordBaseSchema
case class RecordSchema(name: String, fields: Seq[Field], namespace: Option[String]) extends RecordBaseSchema

object Schema {
  def toFields(schema: JSchema): Seq[Field] = schema.getFields.asScala.map { field => Field(field.name, fromJava(field.schema)) }

  def fromJava(schema: JSchema): Schema = {
    val ret = schema.getType match {
      case JSchema.Type.NULL => NullSchema
      case JSchema.Type.BOOLEAN => BooleanSchema
      case JSchema.Type.INT => IntSchema
      case JSchema.Type.LONG => LongSchema
      case JSchema.Type.FLOAT => FloatSchema
      case JSchema.Type.DOUBLE => DoubleSchema
      case JSchema.Type.ENUM => EnumSchema(schema.getName, schema.getEnumSymbols.asScala, Option(schema.getNamespace))
      case JSchema.Type.STRING => StringSchema
      case JSchema.Type.FIXED => FixedSchema(schema.getName, schema.getFixedSize, Option(schema.getNamespace))
      case JSchema.Type.BYTES => BytesSchema
      case JSchema.Type.ARRAY => ArraySchema(fromJava(schema.getElementType))
      case JSchema.Type.MAP => MapSchema(fromJava(schema.getValueType))
      case JSchema.Type.RECORD => if (schema.isError) ErrorSchema(schema.getName, toFields(schema), Option(schema.getNamespace)) else RecordSchema(schema.getName, toFields(schema), Option(schema.getNamespace))
      case JSchema.Type.UNION => UnionSchema(schema.getTypes.asScala.map(fromJava).asInstanceOf[Seq[SingleSchema]])
    }
    ret.jSchema = schema
    ret
  }
}

case class Parameter(name: String, value: Schema) // TODO(alek): default value

case class Message(name: String, parameters: Seq[Parameter], response: Schema, errors: Seq[ErrorSchema])

object Message {
  def fromJava(message: JProtocol#Message): Message = {
    Message(message.getName,
      message.getRequest.getFields.asScala.map { field => Parameter(field.name, Schema.fromJava(field.schema)) },
      Schema.fromJava(message.getResponse),
      message.getErrors.getTypes.asScala.filter(_.getType != JSchema.Type.STRING).map { error => ErrorSchema(error.getName, Schema.toFields(error), Option(error.getNamespace)) })
  }
}

case class Protocol(name: String, declarations: Seq[NamedSchema], messages: Seq[Message], namespace: Option[String]) { var jProtocol: JProtocol = null } // hack

object Protocol {
  def fromJava(protocol: JProtocol): Protocol = {
    val ret = Protocol(protocol.getName,
      protocol.getTypes.asScala.map(Schema.fromJava).toSeq.asInstanceOf[Seq[NamedSchema]],
      protocol.getMessages.values.asScala.toSeq.map { message =>
        Message(message.getName,
          message.getRequest.getFields.asScala.map { field => Parameter(field.name, Schema.fromJava(field.schema)) },
          Schema.fromJava(message.getResponse),
          message.getErrors.getTypes.asScala.filter(_.getType != JSchema.Type.STRING).map { error => ErrorSchema(error.getName, Schema.toFields(error), Option(error.getNamespace)) })
      },
      Option(protocol.getNamespace)
    )
    ret.jProtocol = protocol
    ret
  }
}
