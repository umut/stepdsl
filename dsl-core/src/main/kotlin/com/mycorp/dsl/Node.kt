package com.mycorp.dsl

abstract class Node<A : IO, B : IO> {
    var next: Node<B, *>? = null
    abstract fun name() : String
}