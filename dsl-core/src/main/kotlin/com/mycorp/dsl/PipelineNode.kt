package com.mycorp.dsl

abstract class PipelineNode<A: IO, B: IO> : Node<A, B>() {

    abstract infix fun <C: IO> next(next: Node<B, C>) : PipelineNode<A, C>

}