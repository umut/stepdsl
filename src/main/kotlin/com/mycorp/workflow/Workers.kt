package com.mycorp.workflow

import com.mycorp.workflow.messages.FulfillmentPlan
import com.mycorp.workflow.messages.Order
import com.mycorp.workflow.task.Task

class ProcessOrderTask : Task<Order, FulfillmentPlan> {
    override fun execute(input: Order): FulfillmentPlan {
        println("Processed order: " + input.orderId)

        return FulfillmentPlan(1)
    }
}

class ExecuteFulfillmentPlanTask : Task<FulfillmentPlan, String> {
    override fun execute(input: FulfillmentPlan): String {
        println("Executed FulfillmentPlan: " + input.planId)
        return ""
    }
}