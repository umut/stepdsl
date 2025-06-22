package com.mycorp.dsl

fun wf(block: WorkflowBuilder.() -> Unit): WorkflowBuilder {
    val builder = WorkflowBuilder()
    builder.block()
    return builder
}

class WorkflowBuilder {

    private var start: Node<*, *>? = null

    fun <A : IO, B : IO> startWith(node: Node<A, B>): PipelineNode<A, B> {
        if (start != null) {
            throw IllegalStateException("Workflow already has a start.")
        }
        start = node
        return UniPipelineNode(node)
    }

    fun <A : IO, B : IO> node(workerDescriptor: WorkerDescriptor<A, B>): WorkerNode<A, B> {
        return WorkerNode(workerDescriptor)
    }

    fun startNode() : Node<*, *> {
        return start!!
    }

}