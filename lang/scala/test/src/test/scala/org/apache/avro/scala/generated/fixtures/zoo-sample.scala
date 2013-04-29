// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
        object Request$Record {
            object name extends _root_.shapeless.Field[String] {
               implicit def singleton = new _root_.org.apache.avro.scala.Singleton[name.type] {
                 val instance = name
               }
             }
        
            type Shape = (name.type, (String)) ::
                          _root_.shapeless.HNil
        
            val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"Request","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"name","type":"string"}]}"""))
          }
        
          case class Request(name: String)
        
          object Request {
            implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[Request, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[Request] {
              type D = _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]
              override def to(repr: Request): D = new _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]((Request$Record.name, (repr.name)) ::
        _root_.shapeless.HNil)
        
              override def from(record: D): Request = Request(record.instance.get(Request$Record.name))
            }
        
            def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]] })#L](repr: Request): Iterator[Byte] = {
              implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]]].encode(presenter.to(repr))
            }
        
            def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]] })#L](record: Iterator[Byte]): Request = {
              presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]]].decode(record))
            }
          }
        
          object Response$Record {
            object name extends _root_.shapeless.Field[String] {
               implicit def singleton = new _root_.org.apache.avro.scala.Singleton[name.type] {
                 val instance = name
               }
             }
        
            type Shape = (name.type, (String)) ::
                          _root_.shapeless.HNil
        
            val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"Response","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"name","type":"string"}]}"""))
          }
        
          case class Response(name: String)
        
          object Response {
            implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[Response, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[Response] {
              type D = _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]
              override def to(repr: Response): D = new _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]((Response$Record.name, (repr.name)) ::
        _root_.shapeless.HNil)
        
              override def from(record: D): Response = Response(record.instance.get(Response$Record.name))
            }
        
            def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]] })#L](repr: Response): Iterator[Byte] = {
              implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]]].encode(presenter.to(repr))
            }
        
            def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]] })#L](record: Iterator[Byte]): Response = {
              presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]]].decode(record))
            }
          }
        
             object Error$Record {
               object name extends _root_.shapeless.Field[String] {
                  implicit def singleton = new _root_.org.apache.avro.scala.Singleton[name.type] {
                    val instance = name
                  }
                }
        
               type Shape = (name.type, (String)) ::
                             _root_.shapeless.HNil
        
               val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"error","name":"Error","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"name","type":"string"}]}"""))
             }
        
             case class Error(name: String)
        extends Throwable
        
             object Error {
               implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[Error, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]] = new _root_.org.apache.avro.scala.generated.Presenter[Error] {
                 type D = _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]
                 override def to(repr: Error): D = new _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]((Error$Record.name, (repr.name)) ::
           _root_.shapeless.HNil)
        
                 override def from(record: D): Error = Error(record.instance.get(Error$Record.name))
               }
        
               def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]] })#L](repr: Error): Iterator[Byte] = {
                 implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]]].encode(presenter.to(repr))
               }
        
               def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]] })#L](record: Iterator[Byte]): Error = {
                 presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]]].decode(record))
               }
             }
    
       trait Zoo$Protocol extends _root_.org.apache.avro.scala.ProtocolDescriptor {
         type Shape = (Zoo$Protocol.message.type, ((_root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape], Long) => _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape])) ::
                       (Zoo$Protocol.empty.type, (() => Unit)) ::
                       _root_.shapeless.HNil
       }
    
       object Zoo$Protocol {
         object message extends _root_.shapeless.Field[(_root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape], Long) => _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]]
          object empty extends _root_.shapeless.Field[() => Unit]
    
         implicit def server[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Long] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Unit] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]] })#L] = new _root_.org.apache.avro.scala.Server[Zoo$Protocol, C$] {
           override final val handlers = Map("message" -> { (impl: Zoo$Protocol#Shape, parameters: Seq[Iterator[Byte]]) => implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]]].encode(impl.get(message).apply(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]]].decode(parameters(0)), implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Long]].decode(parameters(1)))) }, "empty" -> { (impl: Zoo$Protocol#Shape, parameters: Seq[Iterator[Byte]]) => implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Unit]].encode(impl.get(empty).apply()) })
           override final val protocol = protocol$
       }
    
         implicit def client[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Long] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Unit] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]] })#L] = new _root_.org.apache.avro.scala.Client[Zoo$Protocol, C$] {
           val message$ = protocol$.messages.filter(_.name == "message").head
            val empty$ = protocol$.messages.filter(_.name == "empty").head
    
           override final def senders(transceiver: _root_.org.apache.avro.ipc.Transceiver) = (message, ((request: _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape], timestamp: Long) => implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]]].decode(new RichJSchema(message$.response.jSchema).encode(new _root_.org.apache.avro.ipc.generic.GenericRequestor(protocol$.jProtocol, transceiver).request("message", _root_.org.apache.avro.scala.generated.createObject(message$.jMessage.getRequest, Map("request" -> new RichJSchema(message$.parameters.filter(_.name == "request").head.value.jSchema).decode(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]]].encode(request)), "timestamp" -> new RichJSchema(message$.parameters.filter(_.name == "timestamp").head.value.jSchema).decode(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Long]].encode(timestamp))))))))) ::
                                                                                              (empty, (() => implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Unit]].decode(new RichJSchema(empty$.response.jSchema).encode(new _root_.org.apache.avro.ipc.generic.GenericRequestor(protocol$.jProtocol, transceiver).request("empty", _root_.org.apache.avro.scala.generated.createObject(empty$.jMessage.getRequest, Map())))))) ::
                                                                                              _root_.shapeless.HNil
         }
    
         val protocol$ = _root_.org.apache.avro.scala.Protocol.fromJava(_root_.org.apache.avro.Protocol.parse("""{"protocol":"Zoo","namespace":"org.apache.avro.scala.test.generated","types":[{"type":"record","name":"Request","fields":[{"name":"name","type":"string"}]},{"type":"record","name":"Response","fields":[{"name":"name","type":"string"}]},{"type":"error","name":"Error","fields":[{"name":"name","type":"string"}]}],"messages":{"message":{"request":[{"name":"request","type":"Request"},{"name":"timestamp","type":"long"}],"response":"Response","errors":["Error"]},"empty":{"request":[],"response":"null"}}}"""))
       }
    
       abstract class Zoo[T$Request : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]] })#L : _root_.scala.reflect.ClassTag, T$Error <: Throwable : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]] })#L : _root_.scala.reflect.ClassTag, T$Response : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]] })#L : _root_.scala.reflect.ClassTag] extends _root_.org.apache.avro.scala.ProtocolHandler[Zoo$Protocol] {
         final def impl$ = (Zoo$Protocol.message, (((request: _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape], timestamp: Long) => implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Response, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]]].to(this.message(implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Request, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]]].from(request), timestamp))))) ::
    (Zoo$Protocol.empty, ((() => { this.empty(); () }))) ::
    _root_.shapeless.HNil
    
         @throws[T$Error] def message(request: T$Request, timestamp: Long): T$Response
          
          def empty(): Unit
       }
    
       class ZooClient[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X] = _root_.org.apache.avro.scala.Client[Zoo$Protocol, X] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Long] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Unit] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]] })#L, T$Request : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]] })#L : _root_.scala.reflect.ClassTag, T$Error <: Throwable : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordDescriptor[Error$Record.Shape]] })#L : _root_.scala.reflect.ClassTag, T$Response : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]] })#L : _root_.scala.reflect.ClassTag](transceiver: _root_.org.apache.avro.ipc.Transceiver) {
         @throws[T$Error] def message(request: T$Request, timestamp: Long): T$Response = implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Response, _root_.org.apache.avro.scala.RecordDescriptor[Response$Record.Shape]]].from(implicitly[_root_.org.apache.avro.scala.Client[Zoo$Protocol, C$]].senders(transceiver).get(Zoo$Protocol.message).apply(implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Request, _root_.org.apache.avro.scala.RecordDescriptor[Request$Record.Shape]]].to(request), timestamp))
          
          def empty(): Unit = { implicitly[_root_.org.apache.avro.scala.Client[Zoo$Protocol, C$]].senders(transceiver).get(Zoo$Protocol.empty).apply(); () }
       }
 }
