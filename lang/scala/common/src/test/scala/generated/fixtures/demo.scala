// This file is machine-generated.

import _root_.org.apache.avro.scala.jschema._
import _root_.org.apache.avro.scala.union.|
import _root_.shapeless.::
import _root_.shapeless.Record._

package org.apache.avro.scala.test.generated {
        object Species extends Enumeration {
            type Species = Value
            val Dog, Hamster = Value
        }
        
             object Pet$Record {
               object species extends _root_.shapeless.Field[_root_.org.apache.avro.scala.AvroEnum] {
                  implicit def singleton = new _root_.org.apache.avro.scala.Singleton[species.type] {
                    val instance = species
                  }
                }
        
               type Descriptor = (species.type, (_root_.org.apache.avro.scala.AvroEnum)) ::
                                  _root_.shapeless.HNil
        
               val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"Pet","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"species","type":{"type":"enum","name":"Species","symbols":["Dog","Hamster"]}}]}"""))
             }
        
             case class Pet(species: Species.Value)
        
             object Pet {
               implicit def presenter: _root_.org.apache.avro.scala.generated.PresenterAux[Pet, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]] = new _root_.org.apache.avro.scala.generated.Presenter[Pet] {
                 type R = _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]
                 override def to(repr: Pet): R = new _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]((Pet$Record.species, (new _root_.org.apache.avro.scala.AvroEnum(repr.species match {
                      case Species.Dog => 0
                       case Species.Hamster => 1
                    }))) ::
                _root_.shapeless.HNil)
        
                 override def from(record: R): Pet = Pet(record.instance.get(Pet$Record.species).value match {
          case 0 => Species.Dog
           case 1 => Species.Hamster
        })
               }
        
               def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]] })#L](repr: Pet): Iterator[Byte] = {
                 implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]].encode(presenter.to(repr))
               }
        
               def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]] })#L](record: Iterator[Byte]): Pet = {
                 presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]].decode(record))
               }
             }
        
        object Person$Record {
          object name extends _root_.shapeless.Field[String] {
             implicit def singleton = new _root_.org.apache.avro.scala.Singleton[name.type] {
               val instance = name
             }
           }
           object age extends _root_.shapeless.Field[Int] {
             implicit def singleton = new _root_.org.apache.avro.scala.Singleton[age.type] {
               val instance = age
             }
           }
           object pets extends _root_.shapeless.Field[Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]] {
             implicit def singleton = new _root_.org.apache.avro.scala.Singleton[pets.type] {
               val instance = pets
             }
           }
        
          type Descriptor = (name.type, (String)) ::
                             (age.type, (Int)) ::
                             (pets.type, (Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]])) ::
                             _root_.shapeless.HNil
        
          val schema$ = _root_.org.apache.avro.scala.Schema.fromJava(new _root_.org.apache.avro.Schema.Parser().parse("""{"type":"record","name":"Person","namespace":"org.apache.avro.scala.test.generated","fields":[{"name":"name","type":"string"},{"name":"age","type":"int"},{"name":"pets","type":{"type":"map","values":{"type":"record","name":"Pet","fields":[{"name":"species","type":{"type":"enum","name":"Species","symbols":["Dog","Hamster"]}}]}}}]}"""))
        }
        
        case class Person[T$Pet : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]] })#L : _root_.scala.reflect.ClassTag](name: String, age: Int, pets: Map[String, T$Pet])
        
        object Person {
          implicit def presenter[T$Pet : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]] })#L : _root_.scala.reflect.ClassTag]: _root_.org.apache.avro.scala.generated.PresenterAux[Person[T$Pet], _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]] = new _root_.org.apache.avro.scala.generated.Presenter[Person[T$Pet]] {
            type R = _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]
            override def to(repr: Person[T$Pet]): R = new _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]((Person$Record.name, (repr.name)) ::
           (Person$Record.age, (repr.age)) ::
           (Person$Record.pets, (repr.pets.mapValues(value => implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Pet, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]].to(value)))) ::
           _root_.shapeless.HNil)
        
            override def from(record: R): Person[T$Pet] = Person[T$Pet](record.instance.get(Person$Record.name), record.instance.get(Person$Record.age), record.instance.get(Person$Record.pets).mapValues(value => implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Pet, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]].from(value)))
          }
        
          def encode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]] })#L, T$Pet : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]] })#L : _root_.scala.reflect.ClassTag](repr: Person[T$Pet]): Iterator[Byte] = {
            implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].encode(presenter.to(repr))
          }
        
          def decode[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]] })#L, T$Pet : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]] })#L : _root_.scala.reflect.ClassTag](record: Iterator[Byte]): Person[T$Pet] = {
            presenter.from(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].decode(record))
          }
        }
    
       trait Demo$Protocol extends _root_.org.apache.avro.scala.ProtocolThing {
         type Descriptor = (Demo$Protocol.createPerson.type, ((String, Int, Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]) => _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor])) ::
                            (Demo$Protocol.removePerson.type, ((_root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]) => Unit)) ::
                            _root_.shapeless.HNil
       }
    
       object Demo$Protocol {
         object createPerson extends _root_.shapeless.Field[(String, Int, Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]) => _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]
          object removePerson extends _root_.shapeless.Field[(_root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]) => Unit]
    
         implicit def server[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, String] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Unit] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Int] })#L] = new _root_.org.apache.avro.scala.Server[Demo$Protocol, C$] {
           override final val handlers = Map("createPerson" -> { (impl: Demo$Protocol#Descriptor, parameters: Seq[Iterator[Byte]]) => implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].encode(impl.get(createPerson).apply(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, String]].decode(parameters(0)), implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Int]].decode(parameters(1)), implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]]].decode(parameters(2)))) }, "removePerson" -> { (impl: Demo$Protocol#Descriptor, parameters: Seq[Iterator[Byte]]) => implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Unit]].encode(impl.get(removePerson).apply(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].decode(parameters(0)))) })
           override final val protocol = protocol$
       }
    
         implicit def client[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, String] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Unit] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Int] })#L] = new _root_.org.apache.avro.scala.Client[Demo$Protocol, C$] {
           val createPerson$ = protocol$.messages.filter(_.name == "createPerson").head
            val removePerson$ = protocol$.messages.filter(_.name == "removePerson").head
    
           override final def senders(transceiver: _root_.org.apache.avro.ipc.Transceiver) = (createPerson, ((name: String, age: Int, pets: Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]) => implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].decode(new RichJSchema(createPerson$.response.jSchema).encode(new _root_.org.apache.avro.ipc.generic.GenericRequestor(protocol$.jProtocol, transceiver).request("createPerson", _root_.org.apache.avro.scala.compiler.createObject(createPerson$.jMessage.getRequest, Map("name" -> new RichJSchema(createPerson$.parameters.filter(_.name == "name").head.value.jSchema).decode(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, String]].encode(name)), "age" -> new RichJSchema(createPerson$.parameters.filter(_.name == "age").head.value.jSchema).decode(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Int]].encode(age)), "pets" -> new RichJSchema(createPerson$.parameters.filter(_.name == "pets").head.value.jSchema).decode(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]]].encode(pets))))))))) ::
                                                                                              (removePerson, ((person: _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]) => implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, Unit]].decode(new RichJSchema(removePerson$.response.jSchema).encode(new _root_.org.apache.avro.ipc.generic.GenericRequestor(protocol$.jProtocol, transceiver).request("removePerson", _root_.org.apache.avro.scala.compiler.createObject(removePerson$.jMessage.getRequest, Map("person" -> new RichJSchema(removePerson$.parameters.filter(_.name == "person").head.value.jSchema).decode(implicitly[_root_.org.apache.avro.scala.ValueCodec[C$, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].encode(person))))))))) ::
                                                                                              _root_.shapeless.HNil
         }
    
         val protocol$ = _root_.org.apache.avro.scala.Protocol.fromJava(_root_.org.apache.avro.Protocol.parse("""{"protocol":"Demo","namespace":"org.apache.avro.scala.test.generated","types":[{"type":"enum","name":"Species","symbols":["Dog","Hamster"]},{"type":"record","name":"Pet","fields":[{"name":"species","type":"Species"}]},{"type":"record","name":"Person","fields":[{"name":"name","type":"string"},{"name":"age","type":"int"},{"name":"pets","type":{"type":"map","values":"Pet"}}]}],"messages":{"createPerson":{"request":[{"name":"name","type":"string"},{"name":"age","type":"int"},{"name":"pets","type":{"type":"map","values":"Pet"}}],"response":"Person"},"removePerson":{"request":[{"name":"person","type":"Person"}],"response":"null"}}}"""))
       }
    
       abstract class Demo[T$Pet : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]] })#L : _root_.scala.reflect.ClassTag, T$Person : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]] })#L : _root_.scala.reflect.ClassTag] extends _root_.org.apache.avro.scala.ProtocolHandler[Demo$Protocol] {
         final def impl$ = (Demo$Protocol.createPerson, (((name: String, age: Int, pets: Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]) => implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Person, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].to(this.createPerson(name, age, pets.mapValues(value => implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Pet, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]].from(value))))))) ::
    (Demo$Protocol.removePerson, (((person: _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]) => { this.removePerson(implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Person, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].from(person)); () }))) ::
    _root_.shapeless.HNil
    
         def createPerson(name: String, age: Int, pets: Map[String, T$Pet]): T$Person
          
          def removePerson(person: T$Person): Unit
       }
    
       class DemoClient[C$ <: _root_.org.apache.avro.scala.Codec[C$] : ({ type L[X] = _root_.org.apache.avro.scala.Client[Demo$Protocol, X] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, String] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Map[String, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Unit] })#L : ({ type L[X <: _root_.org.apache.avro.scala.Codec[C$]] = _root_.org.apache.avro.scala.ValueCodec[X, Int] })#L, T$Pet : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]] })#L : _root_.scala.reflect.ClassTag, T$Person : ({ type L[X] = _root_.org.apache.avro.scala.generated.PresenterAux[X, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]] })#L : _root_.scala.reflect.ClassTag](transceiver: _root_.org.apache.avro.ipc.Transceiver) {
         def createPerson(name: String, age: Int, pets: Map[String, T$Pet]): T$Person = implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Person, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].from(implicitly[_root_.org.apache.avro.scala.Client[Demo$Protocol, C$]].senders(transceiver).get(Demo$Protocol.createPerson).apply(name, age, pets.mapValues(value => implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Pet, _root_.org.apache.avro.scala.RecordThing[Pet$Record.Descriptor]]].to(value))))
          
          def removePerson(person: T$Person): Unit = { implicitly[_root_.org.apache.avro.scala.Client[Demo$Protocol, C$]].senders(transceiver).get(Demo$Protocol.removePerson).apply(implicitly[_root_.org.apache.avro.scala.generated.PresenterAux[T$Person, _root_.org.apache.avro.scala.RecordThing[Person$Record.Descriptor]]].to(person)); () }
       }
 }
