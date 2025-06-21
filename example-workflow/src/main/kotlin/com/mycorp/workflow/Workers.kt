package com.mycorp.workflow

import com.mycorp.dsl.Worker
import com.mycorp.dsl.WorkerTask
import com.mycorp.workflow.messages.FulfillmentPlan
import com.mycorp.workflow.messages.Order
import com.mycorp.workflow.messages.StringIO

@WorkerTask(
    name = "process_order",
    description = "generates the fulfillment plan for a customer order"
)
class ProcessOrderTask : Worker<Order, FulfillmentPlan> {
    override fun execute(input: Order): FulfillmentPlan {
        println("Processed order: " + input.orderId)

        return FulfillmentPlan(1)
    }
}

@WorkerTask(
    name = "execute_plan",
    description = "executes the generated fulfillment plan"
)
class ExecuteFulfillmentPlanTask : Worker<FulfillmentPlan, StringIO> {
    override fun execute(input: FulfillmentPlan): StringIO {
        println("Executed FulfillmentPlan: " + input.planId)
        return StringIO("")
    }
}