package com.mycorp.dsl

@Target(AnnotationTarget.CLASS)
annotation class WorkerTask(
    val name: String,
    val description: String = "",
)