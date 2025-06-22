package com.mycorp.dsl

class WorkerNode<A: IO, B: IO>(private val descriptor: WorkerDescriptor<A, B>) : Node<A, B>() {

    fun retry() : WorkerNode<A, B> {
        return this
    }

    override fun toString() : String {
        val current = "${descriptor.name}::${descriptor.inputType.simpleName}->${descriptor.outputType.simpleName}"
        return current + (if (this.next == null) "" else " ∘ " + this.next.toString())
    }

}