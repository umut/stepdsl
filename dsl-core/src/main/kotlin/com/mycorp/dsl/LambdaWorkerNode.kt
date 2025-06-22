package com.mycorp.dsl

import software.amazon.awscdk.Duration
import software.amazon.awscdk.services.lambda.Runtime

class LambdaWorkerNode<A: IO, B:IO>(override val descriptor: WorkerDescriptor<A, B>) : WorkerNode<A, B>(descriptor) {

    var runtime: Runtime? = null
    var memorySize: Number? = null
    var timeout: Duration? = null

    fun runtime(runtime: Runtime) : LambdaWorkerNode<A, B> {
        this.runtime = runtime
        return this
    }

    fun memorySize(memorySize: Number) : LambdaWorkerNode<A, B> {
        this.memorySize = memorySize
        return this
    }

    fun timeout(timeout: Duration) : LambdaWorkerNode<A, B> {
        this.timeout = timeout
        return this
    }

}