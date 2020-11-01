package seqlab.util

trait AutoCloneable[T <: AnyRef] extends Cloneable { this: T =>
  override final def clone(): T = super.clone().asInstanceOf[T]
}
