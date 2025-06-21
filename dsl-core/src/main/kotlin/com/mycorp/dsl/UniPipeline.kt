package com.mycorp.dsl

class UniPipeline<A: IO, B: IO>(private val node: Node<A, B>) : PipelineNode<A, B>() {
    override fun <C : IO> next(next: Node<B, C>): PipelineNode<A, C> {
        return MultiPipelineNode(node, next)
    }
}