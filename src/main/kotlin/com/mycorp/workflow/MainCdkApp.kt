package com.mycorp.cdk

import com.mycorp.workers.GetOrderWorker
import com.mycorp.workers.NotifyCustomerWorker
import com.mycorp.workers.ShipProductWorker
import com.mycorp.workflow.dsl.then
import com.mycorp.workflow.dsl.workflow
import software.amazon.awscdk.App
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps

fun main() {
    val app = App()
    val stack = Stack(app, "KotlinDslWorkflowStack", StackProps.builder().build())

    val builder = workflow {
        val getOrder = task("GetOrder", GetOrderWorker::class)
        val shipProduct = task("ShipProduct", ShipProductWorker::class)
        val notifyCustomer = task("NotifyCustomer", NotifyCustomerWorker::class)
        val end = end("End")

        startWith(getOrder)

        getOrder.retry() then shipProduct
        shipProduct then notifyCustomer
        notifyCustomer then end
    }

    System.out.println(builder)

    app.synth()
}