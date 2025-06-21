package com.mycorp.dsl

/**
 * Workers must implement this functional interface.
 */
fun interface Worker<I, O> {
    fun execute(input: I): O
}