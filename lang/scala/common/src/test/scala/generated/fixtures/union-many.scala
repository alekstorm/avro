// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
   object UnionMany$Record {
      
    
      type Descriptor = _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"UnionMany","namespace":"org.apache.avro.scala.test.generated","fields":[]}"""))
    }
    
    case class UnionMany()
    
    object UnionMany {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[UnionMany, _root_.org.apache.avro.scala.RecordThing[UnionMany$Record.Descriptor]] = new _root_.org.apache.avro.scala.generated.Presenter[UnionMany] {
        type R = _root_.org.apache.avro.scala.RecordThing[UnionMany$Record.Descriptor]
        override def to(repr: UnionMany): R = new _root_.org.apache.avro.scala.RecordThing[UnionMany$Record.Descriptor](_root_.shapeless.HNil)
    
        override def from(record: R): UnionMany = UnionMany()
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[UnionMany$Record.Descriptor]] })#L](repr: UnionMany): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[UnionMany$Record.Descriptor]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[UnionMany$Record.Descriptor]] })#L](record: Iterator[Byte]): UnionMany = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[UnionMany$Record.Descriptor]]].decode(record))
      }
    }
 }
