package com.sumologic.org.service

import com.sumologic.org.api._
import com.sumologic.org.java.api.{Pet => JPet}
import shapeless._

object Demo {
  val cocoa = Pet(species = Species.Dog)
  val bt = Pet(species = Species.Hamster)
  val alek = Person(name = "Alek Storm", age = 22, pets = Map("Cocoa" -> cocoa, "BT" -> bt), onion = 6.7)

  val bytes = Person.encode(alek)
  val alek2 = Person.decode(bytes)
  alek == alek2
}

object Foo extends OrgHandler[OrgTypes { type T$Pet = JPet }] {
  def createPerson(name: String, age: Int, pets: Map[String, JPet], onion: (String | Double)#L): Person = {
    onion match {
      case string: String => println("Stringy onion!")
      case double: Double => println("Double onion!")
    }
    Person(name, age, pets)
  }

  def removePerson(person: Person) {
    println("Person removed")
  }
}

// ----- generated ----- //

object Species extends Enumeration {
  val Dog, Hamster = Value
}

object Pet$Record {
  object species
  type Descriptor = (species -> (Boolean :: Boolean :: HNil)) :: HNil
}

case class Pet(species: Species#Value)

object Person$Record {
  object name
  object age
  object pets
  object onion
  type Descriptor = (name -> String :: age -> Int :: pets -> Map[String, Pet$Record.Descriptor] :: onion -> (Option[String] :: Option[Double] :: HNil) :: HNil
}

case class Person[T$Pet : DescriptorIso[Pet$Record.Descriptor]]
    (name: String, age: Int, pets: Map[String, T$Pet], onion: (String | Double)#L)

trait OrgTypes {
  type T$Pet = Pet
  type T$Person = Person
}

object Org$Protocol {
  object createPerson
  object removePerson
  type Descriptor = (createPerson -> (String, Int, Map[String, Pet$Record.Descriptor], (Option[String] :: Option[Double] :: HNil)) => Person$Record.Descriptor)
                 :: (removePerson -> (Person$Record.Descriptor) => Unit) :: HNil
}

abstract class OrgHandler[X](implicit petIso: DescriptorIso[Pet$Record.Descriptor, X#T$Pet], personIso: DescriptorIso[Person$Record.Descriptor, X#T$Person]) extends ProtocolHandler[Org$Protocol.Descriptor] {
  type T$Pet = X#T$Pet
  type T$Person = X#T$Person

  def $impl = (createPerson -> (name: String, age: Int, pets: Map[String, Pets$Record.Descriptor], onion: (Option[String] :: Option[Double] :: HNil)) => personIso.from(this.createPerson(name, age, pets, onionIso.to(onion))))
           :: (removePerson -> (person: Person$Record.Descriptor) => this.removePerson(personIso.to(person)))

  def createPerson(name: String, age: Int, pets: Map[String, T$Pet], onion: (String | Double)#L): T$Person
  def removePerson(person: T$Person): Unit
}

// ----- util ----- //

trait DescriptorIso[D <: HList, R] {
  def to(descriptor: D): R
  def from(repr: R): D
}

trait ProtocolHandler[P <: HList] {
  def $impl: P
}

// ----- applications ------ //

implicit class RichRepr[D <: HList, R1 : DescriptorIso[D, R1]](r1: R1) {
  def reprEquals[R2 : DescriptorIso[D, R2]](r2: R2) = implicitly[DI[R1]].from(r1) == implicitly[DI[R2]].from(r2)
}

class ReprCache[D <: HList] {
  private val _cache = Map[Int, D]
  def apply[R : DescriptorIso[D, R]](key: Int): R = implicitly[DI[R]].to(_cache(key))
}

object RecordUtils {
  private object size extends Poly1 {
    implicit def caseInt = at[Int](x => 1)
    implicit def caseString = at[String](_.length)
    implicit def caseList[T] = at[List[T]](_.length)
    // ...
  }

  def recordSize[D <: HList, R : DescriptorIso[D, R]](repr: R): Int = implicitly[DI[R]].from(repr).map(size).fold(sum)
}

class ProtocolDelegator[P <: HList](handlers: Seq[ProtocolHandler[P]]) extends ProtocolHandler[P] {
  var which = 0
  def $impl = handlers(which).$impl
}

// ----- cool future stuff ----- //

class PersonBuilder[HN, HA, HP, HO](val name: Option[String], age: Option[Int], pets: Option[Map[String, Pet]], onion: Option[(String | Double)#L]) {
  def withName(n: String) = PersonBuilder[TRUE, HA, HP, HO](Some(n), age, pets, onion)
  def withAge(a: Int) = PersonBuilder[HN, TRUE, HP, HO](name, Some(a), pets, onion)
  def withPets(p: Map[String, Pet]) = PersonBuilder[HN, HA, TRUE, HO](name, age, Some(p), onion)
  def withOnion(o: (String | Double)#L) = PersonBuilder[HN, HA, HP, TRUE](name, age, pets, Some(o))
}

object PersonBuilder {
  def apply() = new PersonBuilder[FALSE, FALSE, FALSE, FALSE]
  implicit def enableBuild(builder: PersonBuilder[TRUE, TRUE, TRUE, TRUE]) = new {
    def build = Person(builder.name.get, builder.age.get, builder.pets.get)
  }
}

object BuilderDemo {
  val builder = PersonBuilder().withName("Alek").withAge(22)
  val person = builder.build // error
  val person = builder.withPets(Map()).withOnion(5.0).build
}

object LensDemo {
  val speciesLens = Lens[Person] >> _2 >> _0
  val speciesList = speciesLens.get(alek)
  speciesList == Seq(Species.Dog, Species.Hamster)
}

/*equality across reprs
map (cache) of reprs
reprs: scala, java, json (so far)
iterate over fields
write encoders for binary, json, custom independently of repr
proto impl switcher
out-of-band proto information
builders, lenses*/
