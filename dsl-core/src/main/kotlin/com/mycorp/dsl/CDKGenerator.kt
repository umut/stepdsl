package com.mycorp.dsl

import software.amazon.awscdk.Stack
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.stepfunctions.State
import software.amazon.awscdk.services.stepfunctions.Succeed
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke

class CDKGenerator(private val stack: Stack) {

    val lambdas = mutableMapOf<String, software.amazon.awscdk.services.lambda.Function>()
    val states = mutableMapOf<String, State>()

    fun generate(startNode: Node<*, *>) {
        discoverWorkers(startNode, mutableSetOf())
        //wireAll(startNode, mutableSetOf<String>(), lambdas)
    }

    private fun wireAll(node: Node<*, *>, visited: MutableSet<String>,
                        lambdas: Map<String, software.amazon.awscdk.services.lambda.Function>) {
        if (node.name() in visited) return
        visited.add(node.name())

        when (node) {
            is LambdaWorkerNode<*, *> -> createLambdaState(node)
            else -> throw IllegalStateException("Unsupported node")
        }
    }

    private fun discoverWorkers(node: Node<*, *>, visited: MutableSet<String>) {
        if (node.name() in visited) return
        visited.add(node.name())

        when (node) {
            is LambdaWorkerNode<*, *> -> createLambdaState(node)
//            is ChoiceNode -> Choice(stack, node.name)
//            is EndNode -> Succeed(stack, node.name)
//            // Add other node types like ParallelNode here
            else -> Succeed(stack, "end")
        }.let { states[node.name()] = it }

        when (node) {
            is LambdaWorkerNode<*, *> -> node.next?.let { discoverWorkers(it, visited) }
//            is ChoiceNode -> {
//                node.branches.forEach { instantiateAllNodes(it.next, visited) }
//                node.defaultBranch?.let { instantiateAllNodes(it, visited) }
//            }
//            is EndNode -> {} // Terminal node, no next state
            else -> node.next?.let { discoverWorkers(it, visited) }
        }
    }

    private fun createLambdaState(node: LambdaWorkerNode<*, *>) : LambdaInvoke {
        val functionName = node.descriptor.name
        var lambdaFunction = lambdas[functionName]
        if (lambdaFunction == null) {
            lambdaFunction = software.amazon.awscdk.services.lambda.Function.Builder.create(this.stack, node.descriptor.name)
                .runtime(node.runtime)
                .memorySize(node.memorySize)
                .timeout(node.timeout)
                .handler(node.descriptor.type.qualifiedName + ":execute")
                .code(Code.fromAsset("/Users/umututkan/Projects/step-dsl/example-workflow/build/libs/example-workflow.jar"))
                .description(node.descriptor.description)
                .build()
            lambdas[functionName] = lambdaFunction
        }

        val myLambdaTask = LambdaInvoke.Builder.create(this.stack, node.descriptor.name + "_task")
            .lambdaFunction(lambdaFunction)
            .comment("Invokes MyLambdaFunction")
            // Optional: Configure input/output paths, payload, etc.
            // .payload(TaskInput.fromObject(mapOf("key" to "value")))
            // .outputPath("$.Payload")
            .build()
        return myLambdaTask
    }

}