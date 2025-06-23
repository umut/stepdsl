package com.mycorp.dsl

class UniPipelineNode<A: IO, B: IO>(val node: Node<A, B>) : PipelineNode<A, B>() {
    override fun <C : IO> next(next: Node<B, C>): PipelineNode<A, C> {
        when (next) {
            is UniPipelineNode -> {
                this.node.next = next.node
                next.node.prev = this.node
                return MultiPipelineNode(node, next)
            }
            is MultiPipelineNode -> {
                this.node.next = next.head
                next.head.prev = this.node
                return MultiPipelineNode(node, next.tail)
            }
            else -> {
                this.node.next = next
                next.prev = this.node
                return MultiPipelineNode(node, next)
            }
        }
    }

    override fun name(): String {
        throw IllegalStateException("Not supposed to be called.")
    }

    override fun toString() : String {
        return node.toString()
    }
}