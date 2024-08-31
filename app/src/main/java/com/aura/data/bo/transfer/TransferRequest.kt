package com.aura.data.bo.transfer

data class TransferRequest(
    val sender: String,
    val recipient: String,
    val amount: Double
)
