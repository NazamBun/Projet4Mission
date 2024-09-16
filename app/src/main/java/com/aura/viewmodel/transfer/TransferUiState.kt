package com.aura.viewmodel.transfer

data class TransferUiState(
    val isLoading: Boolean = false,
    val isTransferButtonEnabled : Boolean = false,
    val error: String = "",
    val recipient: String = "",
    val amount: Double = 0.0,
)
