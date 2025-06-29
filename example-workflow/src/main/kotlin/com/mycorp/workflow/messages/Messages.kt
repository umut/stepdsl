package com.mycorp.workflow.messages

import com.mycorp.dsl.IO

data class Order(
    val orderId : Long,
    val customerId: String,
    val skus : List<String>
)  : IO

data class FulfillmentPlan(
    val planId: Long,
) : IO

data class StringIO(val value: String) : IO