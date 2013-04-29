// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
     object UnionSingleton$Record {
        object union_field extends _root_.shapeless.Field[_root_.org.apache.avro.scala.UnionDescriptor[Option[Int] ::
                           _root_.shapeless.HNil]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[union_field.type] {
             val instance = union_field
           }
         }
    
        type Shape = (union_field.type, (_root_.org.apache.avro.scala.UnionDescriptor[Option[Int] ::
                      _root_.shapeless.HNil])) ::
                      _root_.shapeless.HNil
    
        val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"UnionSingleton","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"union_field","type":["int"]}]}"""))
      }
    
      case class UnionSingleton(union_field: Int)
    
      object UnionSingleton {
        implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[UnionSingleton, _root_.org.apache.avro.scala.RecordDescriptor[UnionSingleton$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[UnionSingleton] {
          type D = _root_.org.apache.avro.scala.RecordDescriptor[UnionSingleton$Record.Shape]
          override def to(repr: UnionSingleton): D = new _root_.org.apache.avro.scala.RecordDescriptor[UnionSingleton$Record.Shape]((UnionSingleton$Record.union_field, (new _root_.org.apache.avro.scala.UnionDescriptor(repr.union_field match {
      case v: Int => Some(repr.union_field.asInstanceOf[Int]) :: _root_.shapeless.HNil
      }))) ::
    _root_.shapeless.HNil)
    
          override def from(record: D): UnionSingleton = UnionSingleton(record.instance.get(UnionSingleton$Record.union_field).instance.apply(_root_.shapeless.Nat._0).getOrElse(throw new Exception("impossible")).asInstanceOf[Int])
        }
    
        def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionSingleton$Record.Shape]] })#L](repr: UnionSingleton): Iterator[Byte] = {
          implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionSingleton$Record.Shape]]].encode(presenter.to(repr))
        }
    
        def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionSingleton$Record.Shape]] })#L](record: Iterator[Byte]): UnionSingleton = {
          presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionSingleton$Record.Shape]]].decode(record))
        }
      }
 }
