// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
   object UnionEmpty$Record {
      
    
      type Shape = _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"UnionEmpty","namespace":"org.apache.avro.scala.test.generated","fields":[]}"""))
    }
    
    case class UnionEmpty()
    
    object UnionEmpty {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[UnionEmpty, _root_.org.apache.avro.scala.RecordDescriptor[UnionEmpty$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[UnionEmpty] {
        type D = _root_.org.apache.avro.scala.RecordDescriptor[UnionEmpty$Record.Shape]
        override def to(repr: UnionEmpty): D = new _root_.org.apache.avro.scala.RecordDescriptor[UnionEmpty$Record.Shape](_root_.shapeless.HNil)
    
        override def from(record: D): UnionEmpty = UnionEmpty()
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionEmpty$Record.Shape]] })#L](repr: UnionEmpty): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionEmpty$Record.Shape]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionEmpty$Record.Shape]] })#L](record: Iterator[Byte]): UnionEmpty = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionEmpty$Record.Shape]]].decode(record))
      }
    }
 }
