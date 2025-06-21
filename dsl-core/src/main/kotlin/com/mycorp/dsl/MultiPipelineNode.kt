package com.mycorp.dsl

class MultiPipelineNode<A : IO, C : IO>(private val head: Node<A, *>, private val tail: Node<*, C>)
    : PipelineNode<A, C>() {

    override fun <D: IO> next(next: Node<C, D>) : PipelineNode<A, D> {
        this.tail.next = next
        return MultiPipelineNode(this.head, next)
    }

}