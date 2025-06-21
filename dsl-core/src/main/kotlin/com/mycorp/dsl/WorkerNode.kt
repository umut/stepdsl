package com.mycorp.dsl

class WorkerNode<A: IO, B: IO>(val descriptor: WorkerDescriptor<A, B>) : Node<A, B>() {

}