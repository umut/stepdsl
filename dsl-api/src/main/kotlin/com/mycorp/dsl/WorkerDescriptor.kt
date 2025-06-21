package com.mycorp.dsl

import kotlin.reflect.KClass

interface WorkerDescriptor {
    val name: String
    val workerType: KClass<out Worker<*, *>>
    val inputType: KClass<*>
    val outputType: KClass<*>
}