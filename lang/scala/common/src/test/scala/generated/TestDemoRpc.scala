package org.apache.avro.scala
package test.generated

import java.net.InetSocketAddress

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import org.apache.avro.ipc.NettyServer
import org.apache.avro.ipc.NettyTransceiver

@RunWith(classOf[JUnitRunner])
class TestDemoRpc extends FunSuite {
  test("run demo rpc") {
    object DemoImpl extends Demo[Pet, Person[Pet]] {
      def createPerson(name: String, age: Int, pets: Map[String, Pet]): Person[Pet] = {
        val person = Person(name, age, pets)
        println(s"Person created: $person")
        person
      }

      def removePerson(person: Person[Pet]) {
        println(s"Person removed: $person")
      }
    }

    val address = new InetSocketAddress(65111)
    println("Starting server")
    val server = new NettyServer(Responder[BinaryCodec](DemoImpl), address)
    println("Server started")

    val transceiver = new NettyTransceiver(address)
    val client = new DemoClient[BinaryCodec, Pet, Person[Pet]](transceiver)
    println("Client created")

    val name = "Alek Storm"
    val age = 22
    val pets = Map("Cocoa" -> Pet(Species.Dog), "BT" -> Pet(Species.Hamster))
    println(s"Calling createPerson($name, $age, $pets)")
    println(s"Result: " + client.createPerson(name, age, pets))

    transceiver.close()
    server.close()
  }
}
