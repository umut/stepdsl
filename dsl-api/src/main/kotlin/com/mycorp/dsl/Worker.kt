package com.mycorp.dsl

fun interface Worker<I, O> {
    fun execute(input: I): O
}