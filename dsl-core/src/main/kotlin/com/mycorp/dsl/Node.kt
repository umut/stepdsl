package com.mycorp.dsl

abstract class Node<A : IO, B : IO> {
    var next: Node<B, *>? = null
    var prev: Node<*, A>? = null
    abstract fun name() : String
}