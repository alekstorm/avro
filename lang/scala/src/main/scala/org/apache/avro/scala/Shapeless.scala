package org.apache.avro.scala

import shapeless.{Selector, HList}
import shapeless.TypeOperators.|∨|

package object union {
  type |[T, U] = {
    type L[X] = (T |∨| U)#λ[X]
  }
}

package object record {
  import tag._

  // TODO(alek): consider Tag[Key[V]]
  trait Field[V] extends Tag {
    def ->(value: V) = tag[V, this.type](value)
  }

  type ->[K <: Field[V], V] = V @@ K

  // TODO(alek): add some friendlier missing implicit warnings to shapeless using that annotation
  // TODO(alek): use negative evidence to prevent same key being inserted multiple times
  // TODO(alek): different operators for inserting new key, replacing existing one, and updating (key -> func, where func is a function accepting the value's current type and returning a value of any type
  implicit class RecordOps[L <: HList](l: L) {
    def get[K <: Field[V], V](k: K = null)(implicit selector : Selector[L, K -> V]): V = selector(l).asInstanceOf[V]
  }
}

// TODO(alek): see if boxing can be avoided for AnyVals with new value classes
package object tag {
  trait Tag
  //object Tag extends Tag[Covariant[Any]]

  type @@[+T, +U] = T with U

  class Tagger[U] {
    def apply[T](t : T) : T @@ U = t.asInstanceOf[T @@ U]
  }

  /*def tag[T, V >: T, U <: Tag[V]](t: T) : T @@ U = t.asInstanceOf[T @@ U]

  implicit class RichTagged[T](t: T) {
    def @@[V >: T, U <: Tag[V]](u: U) : T @@ U = t.asInstanceOf[T @@ U]
  } */

  def tag[T, U <: Tag](t: T) : T @@ U = t.asInstanceOf[T @@ U]

  implicit class RichTagged[T](t: T) {
    def @@[U <: Tag](u: U) : T @@ U = t.asInstanceOf[T @@ U]
  }

  type Covariant[A] = X forSome { type X <: A }
  type Contravariant[A] = X forSome { type X >: A }
}
