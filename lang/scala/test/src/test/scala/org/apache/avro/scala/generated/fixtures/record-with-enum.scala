// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
     object RecordWithEnum$Record {
        object enum_field extends _root_.shapeless.Field[_root_.org.apache.avro.scala.AvroEnum] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[enum_field.type] {
             val instance = enum_field
           }
         }
    
        type Shape = (enum_field.type, (_root_.org.apache.avro.scala.AvroEnum)) ::
                      _root_.shapeless.HNil
    
        val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"RecordWithEnum","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"enum_field","type":{"type":"enum","name":"ColorEnum","symbols":["Red","Green","Blue"]}}]}"""))
      }
    
      case class RecordWithEnum(enum_field: ColorEnum.Value)
    
      object RecordWithEnum {
        implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[RecordWithEnum, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithEnum$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[RecordWithEnum] {
          type D = _root_.org.apache.avro.scala.RecordDescriptor[RecordWithEnum$Record.Shape]
          override def to(repr: RecordWithEnum): D = new _root_.org.apache.avro.scala.RecordDescriptor[RecordWithEnum$Record.Shape]((RecordWithEnum$Record.enum_field, (new _root_.org.apache.avro.scala.AvroEnum(repr.enum_field match {
                case ColorEnum.Red => 0
                 case ColorEnum.Green => 1
                 case ColorEnum.Blue => 2
                    }))) ::
    _root_.shapeless.HNil)
    
          override def from(record: D): RecordWithEnum = RecordWithEnum(record.instance.get(RecordWithEnum$Record.enum_field).value match {
             case 0 => ColorEnum.Red
              case 1 => ColorEnum.Green
              case 2 => ColorEnum.Blue
             })
        }
    
        def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithEnum$Record.Shape]] })#L](repr: RecordWithEnum): Iterator[Byte] = {
          implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithEnum$Record.Shape]]].encode(presenter.to(repr))
        }
    
        def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithEnum$Record.Shape]] })#L](record: Iterator[Byte]): RecordWithEnum = {
          presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithEnum$Record.Shape]]].decode(record))
        }
      }
 }
