package com.mycorp.dsl

import kotlin.reflect.KClass

interface WorkerDescriptor {
    val taskName: String
    val workerClass: KClass<out Worker<*, *>>
    val inputType: KClass<*>
    val outputType: KClass<*>
}