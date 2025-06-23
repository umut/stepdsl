package com.mycorp.dsl

/**
 * All Worker implementations must use this annotation to be auto discovered
 * and be usable by the DSL.
 */
@Target(AnnotationTarget.CLASS)
annotation class WorkerTask(
    val name: String = "",
    val description: String = "",
)