// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
     object RecordWithAllTypes$Record {
        object null_field extends _root_.shapeless.Field[Unit] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[null_field.type] {
             val instance = null_field
           }
         }
         object boolean_field extends _root_.shapeless.Field[Boolean] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[boolean_field.type] {
             val instance = boolean_field
           }
         }
         object int_field extends _root_.shapeless.Field[Int] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[int_field.type] {
             val instance = int_field
           }
         }
         object long_field extends _root_.shapeless.Field[Long] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[long_field.type] {
             val instance = long_field
           }
         }
         object float_field extends _root_.shapeless.Field[Float] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[float_field.type] {
             val instance = float_field
           }
         }
         object double_field extends _root_.shapeless.Field[Double] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[double_field.type] {
             val instance = double_field
           }
         }
         object string_field extends _root_.shapeless.Field[String] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[string_field.type] {
             val instance = string_field
           }
         }
         object bytes_field extends _root_.shapeless.Field[Array[Byte]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[bytes_field.type] {
             val instance = bytes_field
           }
         }
         object fixed_field extends _root_.shapeless.Field[Seq[Byte]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[fixed_field.type] {
             val instance = fixed_field
           }
         }
         object int_array_field extends _root_.shapeless.Field[Seq[Int]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[int_array_field.type] {
             val instance = int_array_field
           }
         }
         object int_map_field extends _root_.shapeless.Field[Map[String, Int]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[int_map_field.type] {
             val instance = int_map_field
           }
         }
         object int_array_array_field extends _root_.shapeless.Field[Seq[Seq[Int]]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[int_array_array_field.type] {
             val instance = int_array_array_field
           }
         }
         object int_map_map_field extends _root_.shapeless.Field[Map[String, Map[String, Int]]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[int_map_map_field.type] {
             val instance = int_map_map_field
           }
         }
    
        type Shape = (null_field.type, (Unit)) ::
                      (boolean_field.type, (Boolean)) ::
                      (int_field.type, (Int)) ::
                      (long_field.type, (Long)) ::
                      (float_field.type, (Float)) ::
                      (double_field.type, (Double)) ::
                      (string_field.type, (String)) ::
                      (bytes_field.type, (Array[Byte])) ::
                      (fixed_field.type, (Seq[Byte])) ::
                      (int_array_field.type, (Seq[Int])) ::
                      (int_map_field.type, (Map[String, Int])) ::
                      (int_array_array_field.type, (Seq[Seq[Int]])) ::
                      (int_map_map_field.type, (Map[String, Map[String, Int]])) ::
                      _root_.shapeless.HNil
    
        val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"RecordWithAllTypes","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"null_field","type":"null"},{"name":"boolean_field","type":"boolean"},{"name":"int_field","type":"int"},{"name":"long_field","type":"long"},{"name":"float_field","type":"float"},{"name":"double_field","type":"double"},{"name":"string_field","type":"string"},{"name":"bytes_field","type":"bytes"},{"name":"fixed_field","type":{"type":"fixed","name":"anon_fixed_16","size":16}},{"name":"int_array_field","type":{"type":"array","items":"int"}},{"name":"int_map_field","type":{"type":"map","values":"int"}},{"name":"int_array_array_field","type":{"type":"array","items":{"type":"array","items":"int"}}},{"name":"int_map_map_field","type":{"type":"map","values":{"type":"map","values":"int"}}}]}"""))
      }
    
      case class RecordWithAllTypes(null_field: Unit, boolean_field: Boolean, int_field: Int, long_field: Long, float_field: Float, double_field: Double, string_field: String, bytes_field: Array[Byte], fixed_field: Seq[Byte], int_array_field: Seq[Int], int_map_field: Map[String, Int], int_array_array_field: Seq[Seq[Int]], int_map_map_field: Map[String, Map[String, Int]])
    
      object RecordWithAllTypes {
        implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[RecordWithAllTypes, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithAllTypes$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[RecordWithAllTypes] {
          type D = _root_.org.apache.avro.scala.RecordDescriptor[RecordWithAllTypes$Record.Shape]
          override def to(repr: RecordWithAllTypes): D = new _root_.org.apache.avro.scala.RecordDescriptor[RecordWithAllTypes$Record.Shape]((RecordWithAllTypes$Record.null_field, ({ repr.null_field; () })) ::
    (RecordWithAllTypes$Record.boolean_field, (repr.boolean_field)) ::
    (RecordWithAllTypes$Record.int_field, (repr.int_field)) ::
    (RecordWithAllTypes$Record.long_field, (repr.long_field)) ::
    (RecordWithAllTypes$Record.float_field, (repr.float_field)) ::
    (RecordWithAllTypes$Record.double_field, (repr.double_field)) ::
    (RecordWithAllTypes$Record.string_field, (repr.string_field)) ::
    (RecordWithAllTypes$Record.bytes_field, (repr.bytes_field)) ::
    (RecordWithAllTypes$Record.fixed_field, (repr.fixed_field)) ::
    (RecordWithAllTypes$Record.int_array_field, (repr.int_array_field.map(elem => elem))) ::
    (RecordWithAllTypes$Record.int_map_field, (repr.int_map_field.mapValues(value => value))) ::
    (RecordWithAllTypes$Record.int_array_array_field, (repr.int_array_array_field.map(elem => elem.map(elem => elem)))) ::
    (RecordWithAllTypes$Record.int_map_map_field, (repr.int_map_map_field.mapValues(value => value.mapValues(value => value)))) ::
    _root_.shapeless.HNil)
    
          override def from(record: D): RecordWithAllTypes = RecordWithAllTypes({ record.instance.get(RecordWithAllTypes$Record.null_field); () }, record.instance.get(RecordWithAllTypes$Record.boolean_field), record.instance.get(RecordWithAllTypes$Record.int_field), record.instance.get(RecordWithAllTypes$Record.long_field), record.instance.get(RecordWithAllTypes$Record.float_field), record.instance.get(RecordWithAllTypes$Record.double_field), record.instance.get(RecordWithAllTypes$Record.string_field), record.instance.get(RecordWithAllTypes$Record.bytes_field), record.instance.get(RecordWithAllTypes$Record.fixed_field), record.instance.get(RecordWithAllTypes$Record.int_array_field).map(elem => elem), record.instance.get(RecordWithAllTypes$Record.int_map_field).mapValues(value => value), record.instance.get(RecordWithAllTypes$Record.int_array_array_field).map(elem => elem.map(elem => elem)), record.instance.get(RecordWithAllTypes$Record.int_map_map_field).mapValues(value => value.mapValues(value => value)))
        }
    
        def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithAllTypes$Record.Shape]] })#L](repr: RecordWithAllTypes): Iterator[Byte] = {
          implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithAllTypes$Record.Shape]]].encode(presenter.to(repr))
        }
    
        def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithAllTypes$Record.Shape]] })#L](record: Iterator[Byte]): RecordWithAllTypes = {
          presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[RecordWithAllTypes$Record.Shape]]].decode(record))
        }
      }
 }
