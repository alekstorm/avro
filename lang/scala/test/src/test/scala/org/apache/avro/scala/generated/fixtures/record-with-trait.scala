// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
   object RecordWithTrait$Record {
      
    
      type Shape = _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"RecordWithTrait","namespace":"org.apache.avro.scala.test.generated","fields":[],"scalaTrait":"org.apache.avro.scala.SampleTrait"}"""))
    }
    
    case class RecordWithTrait()
    
    object RecordWithTrait {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[RecordWithTrait, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithTrait$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[RecordWithTrait] {
        type D = _root_.org.apache.avro.scala.RecordDescriptor[RecordWithTrait$Record.Shape]
        override def to(repr: RecordWithTrait): D = new _root_.org.apache.avro.scala.RecordDescriptor[RecordWithTrait$Record.Shape](_root_.shapeless.HNil)
    
        override def from(record: D): RecordWithTrait = RecordWithTrait()
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithTrait$Record.Shape]] })#L](repr: RecordWithTrait): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithTrait$Record.Shape]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithTrait$Record.Shape]] })#L](record: Iterator[Byte]): RecordWithTrait = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithTrait$Record.Shape]]].decode(record))
      }
    }
 }
