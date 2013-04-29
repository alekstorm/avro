// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
     object UnionOptional$Record {
        object optional_field extends _root_.shapeless.Field[_root_.org.apache.avro.scala.UnionDescriptor[Option[Unit] ::
                           Option[String] ::
                           _root_.shapeless.HNil]] {
           implicit def singleton = new _root_.org.apache.avro.scala.Singleton[optional_field.type] {
             val instance = optional_field
           }
         }
    
        type Shape = (optional_field.type, (_root_.org.apache.avro.scala.UnionDescriptor[Option[Unit] ::
                      Option[String] ::
                      _root_.shapeless.HNil])) ::
                      _root_.shapeless.HNil
    
        val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"UnionOptional","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"optional_field","type":["null","string"]}]}"""))
      }
    
      case class UnionOptional[U$String$Unit : (Unit | String)#L](optional_field: U$String$Unit)
    
      object UnionOptional {
        implicit def presenter[U$String$Unit : (Unit | String)#L]: _root_.org.apache.avro.scala.generated.PresenterAux[UnionOptional[U$String$Unit], _root_.org.apache.avro.scala.RecordDescriptor[UnionOptional$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[UnionOptional[U$String$Unit]] {
          type D = _root_.org.apache.avro.scala.RecordDescriptor[UnionOptional$Record.Shape]
          override def to(repr: UnionOptional[U$String$Unit]): D = new _root_.org.apache.avro.scala.RecordDescriptor[UnionOptional$Record.Shape]((UnionOptional$Record.optional_field, (new _root_.org.apache.avro.scala.UnionDescriptor(repr.optional_field match {
      case v: Unit => Some({ repr.optional_field.asInstanceOf[Unit]; () }) :: None :: _root_.shapeless.HNil
       case v: String => None :: Some(repr.optional_field.asInstanceOf[String]) :: _root_.shapeless.HNil
      }))) ::
    _root_.shapeless.HNil)
    
          override def from(record: D): UnionOptional[U$String$Unit] = UnionOptional[U$String$Unit](record.instance.get(UnionOptional$Record.optional_field).instance.apply(_root_.shapeless.Nat._1).getOrElse(record.instance.get(UnionOptional$Record.optional_field).instance.apply(_root_.shapeless.Nat._0).getOrElse(throw new Exception("impossible"))).asInstanceOf[U$String$Unit])
        }
    
        def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionOptional$Record.Shape]] })#L, U$String$Unit : (Unit | String)#L](repr: UnionOptional[U$String$Unit]): Iterator[Byte] = {
          implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionOptional$Record.Shape]]].encode(presenter.to(repr))
        }
    
        def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[UnionOptional$Record.Shape]] })#L, U$String$Unit : (Unit | String)#L](record: Iterator[Byte]): UnionOptional[U$String$Unit] = {
          presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[UnionOptional$Record.Shape]]].decode(record))
        }
      }
 }
