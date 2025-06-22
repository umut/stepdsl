package com.mycorp.dsl

fun <A: IO, B: IO> WorkerNode<A, B>.runAsLambda() : LambdaWorkerNode<A, B> {
    return LambdaWorkerNode(descriptor)
}