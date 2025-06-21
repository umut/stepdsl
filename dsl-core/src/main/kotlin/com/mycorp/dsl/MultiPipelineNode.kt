package com.mycorp.dsl

class MultiPipelineNode<A : IO, C : IO>(public val head: Node<A, *>, public val tail: Node<*, C>) :
    PipelineNode<A, C>() {

    override fun <D : IO> next(next: Node<C, D>): PipelineNode<A, D> {
        when (next) {
            is UniPipelineNode -> {
                this.tail.next = next.node
                return MultiPipelineNode(this.head, next)
            }

            is MultiPipelineNode -> {
                this.tail.next = next.head
                return MultiPipelineNode(this.head, next.tail)
            }

            else -> {
                this.tail.next = next
                return MultiPipelineNode(this.head, next)
            }
        }
    }

    override fun toString() : String {
        return head.toString()
    }

}