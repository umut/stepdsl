package com.mycorp.workflow.dsl

import com.mycorp.dsl.*
import com.mycorp.workflow.ProcessOrderTaskDescriptor

fun wf(block: WorkflowBuilder.() -> Unit): WorkflowBuilder {
    val builder = WorkflowBuilder()
    builder.block()
    return builder
}

class WorkflowBuilder {

    fun <A: IO, B: IO> startWith(node: Node<A, B>) : PipelineNode<A, B> {
        return UniPipelineNode(node)
    }

    fun <A: IO, B: IO> node(workerDescriptor: WorkerDescriptor<A, B>): WorkerNode<A, B> {
        return WorkerNode(workerDescriptor)
    }
}

fun main() {
    println("Test");

    wf {
        val processOrderNode = node(ProcessOrderTaskDescriptor).retry()

        val a = startWith(processOrderNode)

        println(a)
    }
}