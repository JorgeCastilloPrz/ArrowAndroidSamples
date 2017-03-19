package com.github.jorgecastillo.kotlinandroid.lang

import org.funktionale.collections.tail

interface NonEmptyCollection<out A> : Collection<A> {
  val head: A
  val tail: Collection<A>

  override fun contains(element: @UnsafeVariance A): Boolean {
    return (head == element).or(tail.contains(element))
  }

  override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean =
      elements.all { contains(it) }

  override fun isEmpty(): Boolean = false
}

class NonEmptyList<out A> private constructor(
    override val head: A,
    override val tail: List<A>,
    val all: List<A>) : NonEmptyCollection<A> {

  constructor(head: A, tail: List<A>) : this(head, tail, listOf(head) + tail)
  private constructor(list: List<A>) : this(list[0], list.tail(), list)

  override val size: Int = all.size

  inline fun <reified B> map(f: (A) -> B): NonEmptyList<B> =
      NonEmptyList(f(head), tail.map(f))

  inline fun <reified B> flatMap(f: (A) -> List<B>): NonEmptyList<B> =
      unsafeFromList(all.flatMap(f))

  override fun iterator(): Iterator<A> = all.iterator()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other?.javaClass != javaClass) return false

    other as NonEmptyList<*>

    if (all != other.all) return false

    return true
  }

  override fun hashCode(): Int {
    return all.hashCode()
  }

  override fun toString(): String {
    return "NonEmptyList(all=$all)"
  }

  companion object Factory {
    inline fun <reified A> of(head: A, vararg t: A): NonEmptyList<A> = NonEmptyList(head,
        t.asList())

    fun <A> unsafeFromList(l: List<A>): NonEmptyList<A> = NonEmptyList(l)
  }
}
