// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
   object RecordWithNestedMap$Record {
      object nested_map_field extends _root_.shapeless.Field[Map[String, Map[String, Int]]] {
         implicit def singleton = new _root_.org.apache.avro.scala.Singleton[nested_map_field.type] {
           val instance = nested_map_field
         }
       }
    
      type Descriptor = (nested_map_field.type, (Map[String, Map[String, Int]])) ::
                         _root_.shapeless.HNil
    
      val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"RecordWithNestedMap","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"nested_map_field","type":{"type":"map","values":{"type":"map","values":"int"}}}]}"""))
    }
    
    case class RecordWithNestedMap(nested_map_field: Map[String, Map[String, Int]])
    
    object RecordWithNestedMap {
      implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[RecordWithNestedMap, _root_.org.apache.avro.scala.RecordThing[RecordWithNestedMap$Record.Descriptor]] = new _root_.org.apache.avro.scala.generated.Presenter[RecordWithNestedMap] {
        type R = _root_.org.apache.avro.scala.RecordThing[RecordWithNestedMap$Record.Descriptor]
        override def to(repr: RecordWithNestedMap): R = new _root_.org.apache.avro.scala.RecordThing[RecordWithNestedMap$Record.Descriptor]((RecordWithNestedMap$Record.nested_map_field, (repr.nested_map_field.mapValues(value => value.mapValues(value => value)))) ::
       _root_.shapeless.HNil)
    
        override def from(record: R): RecordWithNestedMap = RecordWithNestedMap(record.instance.get(RecordWithNestedMap$Record.nested_map_field).mapValues(value => value.mapValues(value => value)))
      }
    
      def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[RecordWithNestedMap$Record.Descriptor]] })#L](repr: RecordWithNestedMap): Iterator[Byte] = {
        implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[RecordWithNestedMap$Record.Descriptor]]].encode(presenter.to(repr))
      }
    
      def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[RecordWithNestedMap$Record.Descriptor]] })#L](record: Iterator[Byte]): RecordWithNestedMap = {
        presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[RecordWithNestedMap$Record.Descriptor]]].decode(record))
      }
    }
 }
