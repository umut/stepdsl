package com.mycorp.workflow

import com.mycorp.dsl.CDKGenerator
import com.mycorp.dsl.runAsLambda
import com.mycorp.dsl.wf
import software.amazon.awscdk.*
import software.amazon.awscdk.services.lambda.Runtime

fun main() {
    val app = App(AppProps.builder().outdir("./cdk").build())
    val stack = Stack(app, "KotlinDslWorkflowStack", StackProps.builder().build())

    println("Test");

    val start = wf {
        val processOrderNode = node(ProcessOrderTaskDescriptor)
            .retry()
            .runAsLambda()
            .runtime(Runtime.JAVA_21)
            .memorySize(128)
            .timeout(Duration.minutes(10))

        val executeFulfillmentPlanNode = node(ExecuteFulfillmentPlanTaskDescriptor)
            .retry()
            .runAsLambda()
            .runtime(Runtime.JAVA_21)
            .memorySize(128)
            .timeout(Duration.minutes(10))

        val a = startWith(processOrderNode) next executeFulfillmentPlanNode

        println(a)
    }.startNode()

    println(start)

    CDKGenerator(stack).generate(start)

    app.synth()
}