// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
   object RecordWithString$Record {
      object string_field extends _root_.shapeless.Field[String] {
         implicit def singleton = new _root_.org.apache.avro.scala.Singleton[string_field.type] {
           val instance = string_field
         }
       }
    
      type Descriptor = (string_field.type, (String)) ::
                         _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"RecordWithString","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"string_field","type":"string"}]}"""))
    }
    
    case class RecordWithString(string_field: String)
    
    object RecordWithString {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[RecordWithString, _root_.org.apache.avro.scala.RecordThing[RecordWithString$Record.Descriptor]] = new _root_.org.apache.avro.scala.generated.Presenter[RecordWithString] {
        type R = _root_.org.apache.avro.scala.RecordThing[RecordWithString$Record.Descriptor]
        override def to(repr: RecordWithString): R = new _root_.org.apache.avro.scala.RecordThing[RecordWithString$Record.Descriptor]((RecordWithString$Record.string_field, (repr.string_field)) ::
       _root_.shapeless.HNil)
    
        override def from(record: R): RecordWithString = RecordWithString(record.instance.get(RecordWithString$Record.string_field))
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[RecordWithString$Record.Descriptor]] })#L](repr: RecordWithString): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[RecordWithString$Record.Descriptor]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[RecordWithString$Record.Descriptor]] })#L](record: Iterator[Byte]): RecordWithString = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[RecordWithString$Record.Descriptor]]].decode(record))
      }
    }
 }
