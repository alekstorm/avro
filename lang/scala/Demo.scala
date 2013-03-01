package org.apache.avro.scala

import generated._

object Demo extends App {
  val cocoa = Pet(species = Species.DOG)
  val bt = Pet(species = Species.HAMSTER)
  val alek = Person(name = "Alek Storm", age = 22, pets = Map("Cocoa" -> cocoa, "BT" -> bt), onion = 6.7)
  val bytes = Person.encode(alek)
  val alek2 = Person.decode(bytes)
  println(alek == alek2)
  println(alek2)
}

object Foo extends OrgHandler {
  def createUser(name: String, age: Int, pets: Map[String, Pet]): Person = {
    println("User created")
    Person(name, age, pets)
  }

  def removeUser(user: Person) {
    println("User removed")
  }
}
