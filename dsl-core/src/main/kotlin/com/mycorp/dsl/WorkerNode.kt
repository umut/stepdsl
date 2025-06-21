package com.mycorp.dsl

class WorkerNode<A: IO, B: IO>(val descriptor: WorkerDescriptor<A, B>) : Node<A, B>() {

    fun retry() : WorkerNode<A, B> {
        return this
    }

    override fun toString() : String {
        return "${descriptor.name}::${descriptor.inputType.simpleName}->${descriptor.outputType.simpleName}"
    }

}