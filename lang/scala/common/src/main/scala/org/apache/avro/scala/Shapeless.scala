package org.apache.avro.scala

import shapeless.{Case1Aux, Case2Aux, HList, Nat, Poly, Poly1, Selector, ToInt}
import shapeless.TypeOperators.|∨|

import Poly.Pullback1Aux

abstract class Case0Aux[-P] {
  type R
  val value: R
  def apply() = value
}

object Case0Aux {
  def apply[P, R0](v: => R0) = new Case0Aux[P] {
    type R = R0
    val value = v
  }

  implicit def inst[P, R0](c: Case0Aux[P] { type R = R0 }): R0 = c.value
}

// TODO(alek): fix Case0 - shouldn't take any type or value parameters, always returns constant
abstract class Phantom1Aux[-P, T] {
  type R
  val value: R
  def apply() = value
}

object Phantom1Aux {
  def apply[P, T, R0](v: => R0) = new Phantom1Aux[P, T] {
    type R = R0
    val value = v
  }

  implicit def inst[P, T, R0](c: Phantom1Aux[P, T] { type R = R0 }): R0 = c.value
}

abstract class Phantom2Aux[-P, T, U] {
  type R
  val value: R
  def apply() = value
}

object Phantom2Aux {
  def apply[P, T, U, R0](v: => R0) = new Phantom2Aux[P, T, U] {
    type R = R0
    val value = v
  }

  implicit def inst[P, T, U, R0](c: Phantom2Aux[P, T, U] { type R = R0 }): R0 = c.value
}

trait PhantomPoly1 extends PhantomPoly {
  def at[T] = new Phantom1AuxBuilder[this.type, T]
  def forAt[P, T] = new Phantom1AuxBuilder[P, T]
  class Phantom1AuxBuilder[P, T] {
    def apply[R0](v: => R0) = new Phantom1Aux[P, T] { type R = R0 ; val value = v }
  }
}

// example: transforming an HList of types for which there are monoids into an HList of the monadic zeros for those types
trait PhantomPoly {
  type Phantom1[T] = Phantom1Aux[this.type, T]
  type Phantom2[T, U] = Phantom2Aux[this.type, T, U]

  def apply[T]()(implicit c : Phantom1[T]) : c.R = c()
  def apply[T, U]()(implicit c : Phantom2[T, U]) : c.R = c()

  type Hom[T] = Phantom1[T] { type R = T }
  type Pullback1[T, R0] = Phantom1[T] { type R = R0 }
  type Pullback2[T, U, R0] = Phantom2[T, U] { type R = R0 }

  //trait Self1[T, R0 <: Self1[T, R0]] extends Phantom1[T] { type R = R0 ; val value = this }
}

// TODO(alek): acting on singleton types is basically like acting on values, but purely in the type system
// TODO(alek): functions that return types, via type members?
object PhantomPoly {
  //implicit def pinst1[P <: PhantomPoly, T](p : P)(implicit c: p.Phantom1[T]) : c.R = c.value

  type Pullback1Aux[-P, T, R0] = Phantom1Aux[P, T] { type R = R0 }
  type Pullback2Aux[-P, T, U, R0] = Phantom2Aux[P, T, U] { type R = R0 }
}

// TODO(alek): higher-order mapping over phantom HLists (able to return another phantom HList?) - basically mapping over implicit parameters
// TODO(alek): Case0 machinery wrong - input and output type don't have to be identical
/*trait TCM[C[_]] extends Poly0 {
  implicit def m[T](implicit tc: C[T]) = at[T](tc)
}

type TC[C[_], L <: HList] = PMapper[TCM[C], L]

trait TC[C[_], L <: HList, Out <: HList] {
  def apply(): Out
}

object TC {
  implicit def tc[C[_], L <: HList]()(implicit mapper: PMapper[TCM[C], L]) = new TC[C, L, mapper.Out] {
    def apply() = mapper()
  }
}

trait PMapper[HF, In <: HList] {
  type Out <: HList
  def apply(): Out
}

trait PMapperAux[HF, In <: HList, Out <: HList] {
  def apply(): Out
}

object PMapper {
  implicit def tcMapper[HF, In <: HList, Out0 <: HList](implicit mapper: PMapperAux[HF, In, Out0]) = new PMapper[HF, In] {
    type Out = Out0
    def apply(): Out = mapper()
  }
}

object PMapperAux {
  import Poly._

  implicit def hnilPMapper1[HF] = new PMapperAux[HF, HNil, HNil] {
    def apply() = HNil
  }

  implicit def hlistPMapper1[HF <: Poly, InH, OutH, InT <: HList, OutT <: HList]
  (implicit hc : Pullback0Aux[HF, InH, OutH], mt : PMapperAux[HF, InT, OutT]) = new PMapperAux[HF, InH :: InT, OutH :: OutT] {
    def apply() = hc[InH]() :: mt()
  }
}*/

trait Constraint[C <: Constraint[C]] extends Poly1 {
  type T
}

class Constrained[C <: Constraint[C]](var value: C#T)(implicit st: Pullback1Aux[C, C#T, Option[String]]) {
  import Poly._

  this := value
  def :=(other: C#T) {
    st(other) match {
      case Some(message) => throw new Exception(message)
      case None => value = other
    }
  }
}

object Constrained {
  implicit def unwrap[C <: Constraint[C], S <: Constrained[C]](s: S): C#T = s.value
}

class MaxNat[N <: Nat] extends Constraint[MaxNat[N]] {
  type T = Int
}

object MaxNat {
  implicit def check[N <: Nat](implicit natToInt: ToInt[N]) = new Case1Aux[MaxNat[N], Int] { type R = Option[String]; val value = (i: Int) => if (i > natToInt()) Some("number too large") else None }
}

package object whatevs {
  def forAt[P, T] = new Case1AuxBuilder[P, T]
  class Case1AuxBuilder[P, T] {
    def apply[R0](v: T => R0) = new Case1Aux[P, T] { type R = R0 ; val value = v }
  }
}

package object pimps {
  class RichPoly[P <: Poly] {
    // TODO(alek): if there were a way to resurrect a value from a singleton type (using TypeTags and InstanceMirrors?), then this could become its own class (like Compose1), and have the awesome property that calling X.curry(Y) twice would return instances of the same type, while calling X.curry(Z) would return a different function type altogether
    def curry[T](t: T) = new Poly1 {
      implicit def foo[U](u: U)(implicit c: Case2Aux[P, T, U]): c.R = c(t, u)
    }
  }
  implicit def enrichPoly[P <: Poly](p: P) = new RichPoly[P]
}

// for 'class X extends (Int --> String) { val func = ??? }'
trait -->[T, R] extends Poly1 {
  val func: T => R
  implicit def subT[U <: T] = at[U](func)
}

object --> {
  def apply[T, R](f: T => R) = new -->[T, R] { val func = f }
}

package object union {
  type |[T, U] = {
    type L[X] = (T |∨| U)#λ[X]
  }
}

package object record {
  import tag._

  // TODO(alek): consider Tag[Key[V]]
  trait Field[V] extends Tag {
    def ->(value: V) = tag[this.type](value)
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
// also solves problem of cyclic aliasing with tag[A](tag[A](new B))
package object tag {
  trait Tag
  //object Tag extends Tag[Covariant[Any]]

  type @@[+T, +U] = T with U

  class Tagger[U <: Tag] {
    def apply[T](t : T) : T with U = t.asInstanceOf[T with U]
  }

  /*def tag[T, V >: T, U <: Tag[V]](t: T) : T @@ U = t.asInstanceOf[T @@ U]

  implicit class RichTagged[T](t: T) {
    def @@[V >: T, U <: Tag[V]](u: U) : T @@ U = t.asInstanceOf[T @@ U]
  } */

  def tag[U <: Tag] = new Tagger[U]

  implicit class RichTagged[T](t: T) {
    def @@[U <: Tag](u: U) : T with U = t.asInstanceOf[T with U]
  }

  type Covariant[A] = X forSome { type X <: A }
  type Contravariant[A] = X forSome { type X >: A }
}
