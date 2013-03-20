// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
   object Contained$Record {
      object data extends _root_.shapeless.Field[Int] {
         implicit def singleton = new _root_.org.apache.avro.scala.Singleton[data.type] {
           val instance = data
         }
       }
    
      type Descriptor = (data.type, (Int)) ::
                         _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"Contained","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"data","type":"int"}]}"""))
    }
    
    case class Contained(data: Int)
    
    object Contained {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[Contained, _root_.org.apache.avro.scala.RecordThing[Contained$Record.Descriptor]] = new _root_.org.apache.avro.scala.generated.Presenter[Contained] {
        type R = _root_.org.apache.avro.scala.RecordThing[Contained$Record.Descriptor]
        override def to(repr: Contained): R = new _root_.org.apache.avro.scala.RecordThing[Contained$Record.Descriptor]((Contained$Record.data, (repr.data)) ::
       _root_.shapeless.HNil)
    
        override def from(record: R): Contained = Contained(record.instance.get(Contained$Record.data))
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Contained$Record.Descriptor]] })#L](repr: Contained): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Contained$Record.Descriptor]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Contained$Record.Descriptor]] })#L](record: Iterator[Byte]): Contained = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Contained$Record.Descriptor]]].decode(record))
      }
    }
 }
 
 package org.apache.avro.scala.test.generated {
   object Container$Record {
      
    
      type Descriptor = _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"Container","namespace":"org.apache.avro.scala.test.generated","fields":[]}"""))
    }
    
    case class Container()
    
    object Container {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[Container, _root_.org.apache.avro.scala.RecordThing[Container$Record.Descriptor]] = new _root_.org.apache.avro.scala.generated.Presenter[Container] {
        type R = _root_.org.apache.avro.scala.RecordThing[Container$Record.Descriptor]
        override def to(repr: Container): R = new _root_.org.apache.avro.scala.RecordThing[Container$Record.Descriptor](_root_.shapeless.HNil)
    
        override def from(record: R): Container = Container()
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Container$Record.Descriptor]] })#L](repr: Container): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Container$Record.Descriptor]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Container$Record.Descriptor]] })#L](record: Iterator[Byte]): Container = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Container$Record.Descriptor]]].decode(record))
      }
    }
 }
