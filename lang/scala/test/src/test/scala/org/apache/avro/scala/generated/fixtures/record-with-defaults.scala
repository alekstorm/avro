// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
     object RecordDefaultField$Record {
        object a extends _root_.shapeless.Field[String] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[a.type] {
             val instance = a
           }
         }
    
        type Shape = (a.type, (String)) ::
                      _root_.shapeless.HNil
    
        val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"RecordDefaultField","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"a","type":"string"}]}"""))
      }
    
      case class RecordDefaultField(a: String)
    
      object RecordDefaultField {
        implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[RecordDefaultField, _root_.org.apache.avro.scala.RecordDescriptor[RecordDefaultField$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[RecordDefaultField] {
          type D = _root_.org.apache.avro.scala.RecordDescriptor[RecordDefaultField$Record.Shape]
          override def to(repr: RecordDefaultField): D = new _root_.org.apache.avro.scala.RecordDescriptor[RecordDefaultField$Record.Shape]((RecordDefaultField$Record.a, (repr.a)) ::
    _root_.shapeless.HNil)
    
          override def from(record: D): RecordDefaultField = RecordDefaultField(record.instance.get(RecordDefaultField$Record.a))
        }
    
        def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordDefaultField$Record.Shape]] })#L](repr: RecordDefaultField): Iterator[Byte] = {
          implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordDefaultField$Record.Shape]]].encode(presenter.to(repr))
        }
    
        def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordDefaultField$Record.Shape]] })#L](record: Iterator[Byte]): RecordDefaultField = {
          presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordDefaultField$Record.Shape]]].decode(record))
        }
      }
 }
 
 package org.apache.avro.scala.test.generated {
     object RecordWithDefaults$Record {
        object string_field extends _root_.shapeless.Field[String] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[string_field.type] {
             val instance = string_field
           }
         }
         object map_field_empty_default extends _root_.shapeless.Field[Map[String, Int]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[map_field_empty_default.type] {
             val instance = map_field_empty_default
           }
         }
         object map_field_nonempty_default extends _root_.shapeless.Field[Map[String, String]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[map_field_nonempty_default.type] {
             val instance = map_field_nonempty_default
           }
         }
         object array_field_empty_default extends _root_.shapeless.Field[Seq[String]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[array_field_empty_default.type] {
             val instance = array_field_empty_default
           }
         }
    
        type Shape = (string_field.type, (String)) ::
                      (map_field_empty_default.type, (Map[String, Int])) ::
                      (map_field_nonempty_default.type, (Map[String, String])) ::
                      (array_field_empty_default.type, (Seq[String])) ::
                      _root_.shapeless.HNil
    
        val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"RecordWithDefaults","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"string_field","type":"string","default":"default string"},{"name":"map_field_empty_default","type":{"type":"map","values":"int"},"default":{}},{"name":"map_field_nonempty_default","type":{"type":"map","values":"string"},"default":{"a":"aa","b\"b":"bb\"bb"}},{"name":"array_field_empty_default","type":{"type":"array","items":"string"},"default":[]}]}"""))
      }
    
      case class RecordWithDefaults(string_field: String, map_field_empty_default: Map[String, Int], map_field_nonempty_default: Map[String, String], array_field_empty_default: Seq[String])
    
      object RecordWithDefaults {
        implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[RecordWithDefaults, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithDefaults$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[RecordWithDefaults] {
          type D = _root_.org.apache.avro.scala.RecordDescriptor[RecordWithDefaults$Record.Shape]
          override def to(repr: RecordWithDefaults): D = new _root_.org.apache.avro.scala.RecordDescriptor[RecordWithDefaults$Record.Shape]((RecordWithDefaults$Record.string_field, (repr.string_field)) ::
    (RecordWithDefaults$Record.map_field_empty_default, (repr.map_field_empty_default.mapValues(value => value))) ::
    (RecordWithDefaults$Record.map_field_nonempty_default, (repr.map_field_nonempty_default.mapValues(value => value))) ::
    (RecordWithDefaults$Record.array_field_empty_default, (repr.array_field_empty_default.map(elem => elem))) ::
    _root_.shapeless.HNil)
    
          override def from(record: D): RecordWithDefaults = RecordWithDefaults(record.instance.get(RecordWithDefaults$Record.string_field), record.instance.get(RecordWithDefaults$Record.map_field_empty_default).mapValues(value => value), record.instance.get(RecordWithDefaults$Record.map_field_nonempty_default).mapValues(value => value), record.instance.get(RecordWithDefaults$Record.array_field_empty_default).map(elem => elem))
        }
    
        def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithDefaults$Record.Shape]] })#L](repr: RecordWithDefaults): Iterator[Byte] = {
          implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithDefaults$Record.Shape]]].encode(presenter.to(repr))
        }
    
        def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithDefaults$Record.Shape]] })#L](record: Iterator[Byte]): RecordWithDefaults = {
          presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithDefaults$Record.Shape]]].decode(record))
        }
      }
 }
