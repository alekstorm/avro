package org.apache.avro.scala

import shapeless.{Selector, HList}

package object trecord {
  import ctag._

  trait TField[V] extends Tag[Covariant[V]] {
    def ->(value: V) = value @@ this
  }

  // TODO(alek): use negative evidence to prevent same key being inserted multiple times
  // TODO(alek): different operators for inserting new key, replacing existing one, and updating (key -> func, where func is a function accepting the value's current type and returning a value of any type
  implicit class TRecordOps[L <: HList](l: L) {
    def get[V, F <: TField[V]](implicit selector : Selector[L, F]): V = selector(l).asInstanceOf[V]
    def get[V, F <: TField[V]](f: F)(implicit selector : Selector[L, F]): V = get[V, F]
  }
}

// TODO(alek): see if boxing can be avoided for AnyVals with new value classes
package object ctag {
  trait Tag[+U]
  object Tag extends Tag[Covariant[Any]]

  type @@[T, +U] = T with U

  class Tagger[U] {
    def apply[T](t : T) : T @@ U = t.asInstanceOf[T @@ U]
  }

  def tag[T, V >: T, U <: Tag[V]](t: T) : T @@ U = t.asInstanceOf[T @@ U]

  implicit class RichTagged[T](t: T) {
    def @@[V >: T, U <: Tag[V]](u: U) : T @@ U = t.asInstanceOf[T @@ U]
  }

  type Covariant[A] = X forSome { type X <: A }
  type Contravariant[A] = X forSome { type X >: A }
}
