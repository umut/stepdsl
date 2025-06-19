package com.mycorp.workflow.task

fun interface Task<I, O> {
    fun execute(input: I): O
}