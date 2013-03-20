// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
   object UnionEmpty$Record {
      
    
      type Descriptor = _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"UnionEmpty","namespace":"org.apache.avro.scala.test.generated","fields":[]}"""))
    }
    
    case class UnionEmpty()
    
    object UnionEmpty {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[UnionEmpty, _root_.org.apache.avro.scala.RecordThing[UnionEmpty$Record.Descriptor]] = new _root_.org.apache.avro.scala.generated.Presenter[UnionEmpty] {
        type R = _root_.org.apache.avro.scala.RecordThing[UnionEmpty$Record.Descriptor]
        override def to(repr: UnionEmpty): R = new _root_.org.apache.avro.scala.RecordThing[UnionEmpty$Record.Descriptor](_root_.shapeless.HNil)
    
        override def from(record: R): UnionEmpty = UnionEmpty()
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[UnionEmpty$Record.Descriptor]] })#L](repr: UnionEmpty): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[UnionEmpty$Record.Descriptor]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[UnionEmpty$Record.Descriptor]] })#L](record: Iterator[Byte]): UnionEmpty = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[UnionEmpty$Record.Descriptor]]].decode(record))
      }
    }
 }
