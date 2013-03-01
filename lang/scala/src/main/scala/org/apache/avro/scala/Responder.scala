package org.apache.avro.scala

import java.io.ByteArrayOutputStream

import org.apache.avro.{Protocol => JProtocol}
import org.apache.avro.generic.{GenericRecord, GenericDatumWriter}
import org.apache.avro.io.EncoderFactory
import org.apache.avro.ipc.generic.GenericResponder

import shapeless._

// TODO(alek): passing Encoder and Decoder instances doesn't make sense anymore
class Responder[P <: HList](handler: ProtocolHandler[P], local: Protocol)(implicit rpc: Rpc[P], codec: Codec) extends GenericResponder(local.jProtocol) {
  @throws[Exception]
  override def respond(avroMessage: JProtocol#Message, genericRequest: AnyRef): AnyRef = {
    val message = Message.fromJava(avroMessage)
    val request = genericRequest.asInstanceOf[GenericRecord]
    val parameters = message.parameters.map { param =>
      val output = new ByteArrayOutputStream()
      new GenericDatumWriter[AnyRef](avroMessage.getRequest).write(request.get(param.name), EncoderFactory.get().binaryEncoder(output, null))
      output.toByteArray.toIterator
    }
    rpc.handlers(message.name)(handler.impl$, parameters, codec)
  }
}

// TODO(alek): propose some way to express "return type T fulfilling some condition", where the condition is an implicit value, but its type is unknown to the caller - as in, "I will return a variable of some type A such that there exists a typeclass B[A] that you can call methods on later" - type parameters won't work here, because the caller doesn't know beforehand what's going to be returned
// TODO(alek): submit change to avro to make Responder an interface exposing only `public List<ByteBuffer> respond(List<ByteBuffer> buffers) throws IOException` and `public List<ByteBuffer> respond(List<ByteBuffer> buffers, Transceiver connection) throws IOException`, rename current Responder to something like 'BinaryResponder', make `Object respond(Message, Object) throws Exception`, `readRequest`, `writeRequest`, and `writeError` protected (also gets rid of requirement to pass Protocol or Class instance to constructor)
// TODO(alek): propose using a Request record: record Request { string name; array<string> parameters } instead, rather than creating an ad-hoc record to hold the parameters (allows partially decoding message into at least its name and list of parameters)
