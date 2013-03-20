package org.apache.avro.scala

import org.apache.avro.{Protocol => JProtocol}
import org.apache.avro.generic.GenericRecord
import org.apache.avro.ipc.generic.GenericResponder

import jschema._

object Responder {
  def apply[C <: Codec[C]] = new ResponderBuilder[C]
  class ResponderBuilder[C <: Codec[C]] {
    def apply[P <: ProtocolThing](handler: ProtocolHandler[P])(implicit server: Server[P, C]) = new Responder(handler)
  }
}

class Responder[P <: ProtocolThing, C <: Codec[C]](handler: ProtocolHandler[P])(implicit server: Server[P, C]) extends GenericResponder(server.protocol.jProtocol) {
  @throws[Exception]
  override def respond(avroMessage: JProtocol#Message, genericRequest: AnyRef): AnyRef = {
    val message = Message.fromJava(avroMessage)
    val request = genericRequest.asInstanceOf[GenericRecord]
    val parameters = message.parameters.map(param => param.value.jSchema.encode(request.get(param.name)))
    avroMessage.getResponse.decode(server.handlers(message.name)(handler.impl$, parameters))
  }
}

// TODO(alek): propose some way to express "return type T fulfilling some condition", where the condition is an implicit value, but its type is unknown to the caller - as in, "I will return a variable of some type A such that there exists a typeclass B[A] that you can call methods on later" - type parameters won't work here, because the caller doesn't know beforehand what's going to be returned
// TODO(alek): submit change to avro to make Responder an interface exposing only `public List<ByteBuffer> respond(List<ByteBuffer> buffers) throws IOException` and `public List<ByteBuffer> respond(List<ByteBuffer> buffers, Transceiver connection) throws IOException`, rename current Responder to something like 'BinaryResponder', make `Object respond(Message, Object) throws Exception`, `readRequest`, `writeRequest`, and `writeError` protected (also gets rid of requirement to pass Protocol or Class instance to constructor)
// TODO(alek): propose using a Request record: record Request { string name; array<string> parameters } instead, rather than creating an ad-hoc record to hold the parameters (allows partially decoding message into at least its name and list of parameters)
