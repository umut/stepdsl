package com.mycorp.dsl

class UniPipelineNode<A: IO, B: IO>(public val node: Node<A, B>) : PipelineNode<A, B>() {
    override fun <C : IO> next(next: Node<B, C>): PipelineNode<A, C> {
        when (next) {
            is UniPipelineNode -> {
                node.next = next.node
                return MultiPipelineNode(node, next)
            }
            is MultiPipelineNode -> {
                node.next = next.head
                return MultiPipelineNode(node, next.tail)
            }
            else -> {
                return MultiPipelineNode(node, next)
            }
        }
    }

    override fun toString() : String {
        return node.toString()
    }
}