package com.mycorp.workflow.messages

import software.amazon.awscdk.services.ec2.Instance
import java.time.Instant

data class Order(
    val orderId : Long,
    val customerId: String,
    val skus : List<String>
)  : IO

data class FulfillmentPlan(
    val planId: Long,
) : IO