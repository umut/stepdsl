package com.mycorp.workflow.dsl

import com.mycorp.workflow.messages.IO
import com.mycorp.workflow.task.Task
import com.mycorp.workflow.util.GenericsUtils
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun workflow(block: DagBuilder.() -> Unit): DagBuilder {
    val builder = DagBuilder()
    builder.block()
    return builder
}

class DagBuilder {
    val graph = mutableMapOf<String, State>()
    lateinit var startState: String

    fun startWith(state: TaskNode) : TaskNode {
        this.startState = state.name
        return state
    }

    fun <I : IO, O : IO, T : Task<I, O>> task(name: String, workerClass: KClass<T>): TaskNode {
        if (!graph.containsKey(name)) {
            graph[name] = TaskState(name, workerClass)
        }
        return TaskNode(name, this)
    }

    fun end(name: String): TaskNode {
        if (!graph.containsKey(name)) {
            graph[name] = EndState(name)
        }
        return TaskNode(name, this)
    }
}

class TaskNode(val name: String, val builder: DagBuilder) {

    fun retry() : TaskNode {
        return this;
    }

}

infix fun TaskNode.then(nextNode: TaskNode) : TaskNode {
    val sourceState = this.builder.graph[this.name]
    val destState = nextNode.builder.graph[nextNode.name]

    // Check if both are tasks we can validate
    if (sourceState is TaskState<*> && destState is TaskState<*>) {
        // --- THIS IS THE CORRECTED LOGIC ---

        // 1. Get the full I/O pair for the source task
        val sourceIOTypes = GenericsUtils.getWorkerIOTypes(sourceState.workerClass)
        // 2. Get the full I/O pair for the destination task
        val destIOTypes = GenericsUtils.getWorkerIOTypes(destState.workerClass)

        // 3. Extract the specific types we need to compare
        val sourceOutput = sourceIOTypes.second // The Output type 'O' from the source worker
        val destInput = destIOTypes.first      // The Input type 'I' from the destination worker

        // 4. Now, perform the compatibility check
        if (!destInput.isSubclassOf(sourceOutput)) {
            throw IllegalArgumentException(
                "Type Mismatch: Task '${sourceState.name}' produces a '${sourceOutput.simpleName}' " +
                        "which cannot be assigned to task '${destState.name}' which requires a '${destInput.simpleName}'."
            )
        }
    }

    // If validation passes, update the graph to create the dependency
    if (sourceState is TaskState<*>) {
        sourceState.next = nextNode.name
    }

    return nextNode
}