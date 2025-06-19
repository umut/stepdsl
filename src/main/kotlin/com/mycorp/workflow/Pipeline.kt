package com.mycorp.cdk

import com.mycorp.workflow.ExecuteFulfillmentPlanTask
import com.mycorp.workflow.ProcessOrderTask
import com.mycorp.workflow.dsl.then
import com.mycorp.workflow.dsl.workflow
import software.amazon.awscdk.App
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps

fun main() {
    val app = App()
    val stack = Stack(app, "KotlinDslWorkflowStack", StackProps.builder().build())

    val builder = workflow {
        val processOrderTask = task("ProcessOrder", ProcessOrderTask::class)
        val executeFulfillmentPlanTask = task("ExecuteFulfillmentPlan", ExecuteFulfillmentPlanTask::class)

        startWith(processOrderTask) then
                executeFulfillmentPlanTask then
                end("End")

    }

    System.out.println(builder)

    app.synth()
}