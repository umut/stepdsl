package com.mycorp.workflow

import com.mycorp.dsl.wf
import com.mycorp.workflow.ExecuteFulfillmentPlanTask
import com.mycorp.workflow.ExecuteFulfillmentPlanTaskDescriptor
import com.mycorp.workflow.ProcessOrderTask
import com.mycorp.workflow.ProcessOrderTaskDescriptor
import com.mycorp.workflow.dsl.then
import com.mycorp.workflow.dsl.workflow
import software.amazon.awscdk.App
import software.amazon.awscdk.AppProps
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps

fun main() {
    val app = App(AppProps.builder().outdir("/Users/umututkan/Desktop/cdk").build())
    val stack = Stack(app, "KotlinDslWorkflowStack", StackProps.builder().build())

    println("Test");

    val start = wf {
        val processOrderNode = node(ProcessOrderTaskDescriptor).retry()
        val executeFulfillmentPlanNode = node(ExecuteFulfillmentPlanTaskDescriptor).retry()

        val a = startWith(processOrderNode) next executeFulfillmentPlanNode

        println(a)
    }.startNode()

    println(start)

    app.synth()
}