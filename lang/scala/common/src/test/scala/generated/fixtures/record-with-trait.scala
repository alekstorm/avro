// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
   object RecordWithTrait$Record {
      
    
      type Descriptor = _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"RecordWithTrait","namespace":"org.apache.avro.scala.test.generated","fields":[],"scalaTrait":"org.apache.avro.scala.SampleTrait"}"""))
    }
    
    case class RecordWithTrait()
    
    object RecordWithTrait {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[RecordWithTrait, _root_.org.apache.avro.scala.RecordThing[RecordWithTrait$Record.Descriptor]] = new _root_.org.apache.avro.scala.generated.Presenter[RecordWithTrait] {
        type R = _root_.org.apache.avro.scala.RecordThing[RecordWithTrait$Record.Descriptor]
        override def to(repr: RecordWithTrait): R = new _root_.org.apache.avro.scala.RecordThing[RecordWithTrait$Record.Descriptor](_root_.shapeless.HNil)
    
        override def from(record: R): RecordWithTrait = RecordWithTrait()
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[RecordWithTrait$Record.Descriptor]] })#L](repr: RecordWithTrait): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[RecordWithTrait$Record.Descriptor]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[RecordWithTrait$Record.Descriptor]] })#L](record: Iterator[Byte]): RecordWithTrait = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[RecordWithTrait$Record.Descriptor]]].decode(record))
      }
    }
 }
