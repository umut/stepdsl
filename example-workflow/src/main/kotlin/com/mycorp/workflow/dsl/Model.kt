package com.mycorp.workflow.dsl

import com.mycorp.dsl.Worker
import kotlin.reflect.KClass

sealed interface State {
    val name: String
    val type: String
}

data class TaskState<T : Worker<*, *>>(
    override val name: String,
    val workerClass: KClass<T>,
    override val type: String = "Task",
    var next: String? = null
) : State

// Add other data classes like ChoiceState, ParallelState, etc. if needed
data class EndState(
    override val name: String,
    override val type: String = "End"
) : State