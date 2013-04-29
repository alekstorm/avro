// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
     object UnionContained$Record {
        object data extends _root_.shapeless.Field[Int] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[data.type] {
             val instance = data
           }
         }
         object map_field extends _root_.shapeless.Field[Map[String, String]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[map_field.type] {
             val instance = map_field
           }
         }
    
        type Shape = (data.type, (Int)) ::
                      (map_field.type, (Map[String, String])) ::
                      _root_.shapeless.HNil
    
        val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"UnionContained","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"data","type":"int"},{"name":"map_field","type":{"type":"map","values":"string"}}]}"""))
      }
    
      case class UnionContained(data: Int, map_field: Map[String, String])
    
      object UnionContained {
        implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[UnionContained, _root_.org.apache.avro.scala.RecordDescriptor[UnionContained$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[UnionContained] {
          type D = _root_.org.apache.avro.scala.RecordDescriptor[UnionContained$Record.Shape]
          override def to(repr: UnionContained): D = new _root_.org.apache.avro.scala.RecordDescriptor[UnionContained$Record.Shape]((UnionContained$Record.data, (repr.data)) ::
    (UnionContained$Record.map_field, (repr.map_field.mapValues(value => value))) ::
    _root_.shapeless.HNil)
    
          override def from(record: D): UnionContained = UnionContained(record.instance.get(UnionContained$Record.data), record.instance.get(UnionContained$Record.map_field).mapValues(value => value))
        }
    
        def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionContained$Record.Shape]] })#L](repr: UnionContained): Iterator[Byte] = {
          implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionContained$Record.Shape]]].encode(presenter.to(repr))
        }
    
        def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionContained$Record.Shape]] })#L](record: Iterator[Byte]): UnionContained = {
          presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionContained$Record.Shape]]].decode(record))
        }
      }
 }
 
 package org.apache.avro.scala.test.generated {
   object UnionContainer$Record {
      
    
      type Shape = _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"UnionContainer","namespace":"org.apache.avro.scala.test.generated","fields":[]}"""))
    }
    
    case class UnionContainer()
    
    object UnionContainer {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[UnionContainer, _root_.org.apache.avro.scala.RecordDescriptor[UnionContainer$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[UnionContainer] {
        type D = _root_.org.apache.avro.scala.RecordDescriptor[UnionContainer$Record.Shape]
        override def to(repr: UnionContainer): D = new _root_.org.apache.avro.scala.RecordDescriptor[UnionContainer$Record.Shape](_root_.shapeless.HNil)
    
        override def from(record: D): UnionContainer = UnionContainer()
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionContainer$Record.Shape]] })#L](repr: UnionContainer): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionContainer$Record.Shape]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionContainer$Record.Shape]] })#L](record: Iterator[Byte]): UnionContainer = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionContainer$Record.Shape]]].decode(record))
      }
    }
 }
