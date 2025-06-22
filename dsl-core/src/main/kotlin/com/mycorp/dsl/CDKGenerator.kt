package com.mycorp.dsl

import software.amazon.awscdk.Stack
import software.amazon.awscdk.services.stepfunctions.State
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke

class CDKGenerator(private val stack: Stack) {

    val lambdas = mutableMapOf<String, software.amazon.awscdk.services.lambda.Function>()

    fun generate(startNode: Node<*, *>) {
        instantiateNodes(startNode, mutableSetOf())
    }

    private fun instantiateNodes(node: Node<*, *>, visited: MutableSet<String>) {
        if (node.name() in visited) return
        visited.add(node.name())

        val cdkState: State = when (node) {
            is LambdaWorkerNode<*, *> -> createLambdaTask(node)
//            is ChoiceNode -> Choice(stack, node.name)
//            is EndNode -> Succeed(stack, node.name)
//            // Add other node types like ParallelNode here
            else -> throw IllegalStateException("Unsupported node")
        }

        when (node) {
            is LambdaWorkerNode<*, *> -> node.next?.let { instantiateNodes(it, visited) }
//            is ChoiceNode -> {
//                node.branches.forEach { instantiateAllNodes(it.next, visited) }
//                node.defaultBranch?.let { instantiateAllNodes(it, visited) }
//            }
//            is EndNode -> {} // Terminal node, no next state
            else -> throw IllegalStateException("Unsupported node")
        }
    }

    fun createLambdaTask(node: LambdaWorkerNode<*, *>) : State {
        val myLambdaFunction = software.amazon.awscdk.services.lambda.Function.Builder.create(this.stack, node.descriptor.name)
            .runtime(node.runtime)
            .memorySize(node.memorySize)
            .timeout(node.timeout)
            .handler(node.descriptor.type.qualifiedName + ":execute")
            .code(software.amazon.awscdk.services.lambda.Code.fromAsset("/Users/umututkan/Projects/step-dsl/example-workflow/build/libs/example-workflow.jar"))
            .build()
        val myLambdaTask = LambdaInvoke.Builder.create(this.stack, node.descriptor.name + "Task")
            .lambdaFunction(myLambdaFunction)
            .comment("Invokes MyLambdaFunction")
            // Optional: Configure input/output paths, payload, etc.
            // .payload(TaskInput.fromObject(mapOf("key" to "value")))
            // .outputPath("$.Payload")
            .build()
        return myLambdaTask
    }

}