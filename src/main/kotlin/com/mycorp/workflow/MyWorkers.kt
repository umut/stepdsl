package com.mycorp.workers

import com.mycorp.workflow.task.Task

data class Order(val orderId: String, val type: String, val customerId: String)
data class OrderResult(val status: String)

class GetOrderWorker : Task<String, Order> {
    override fun execute(input: String): Order {
        println("WORKER: Getting details for order $input")
        return Order(orderId = input, type = "PHYSICAL", customerId = "c-123")
    }
}

class ShipProductWorker : Task<Order, OrderResult> {
    override fun execute(input: Order): OrderResult {
        println("WORKER: Shipping product for order ${input.orderId}")
        return OrderResult(status = "SHIPPED")
    }
}

class NotifyCustomerWorker : Task<OrderResult, Unit> {
    override fun execute(input: OrderResult) {
        println("WORKER: Notifying customer about status: ${input.status}")
    }
}