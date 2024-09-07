package com.aura.viewmodel.transfer

data class TransferUiState(
    val isLoading: Boolean = false,
    val showRetryButton: Boolean = false,
    val error: String = "",
    val showErrorMessage: Boolean = false,
    val recipient: String = "",
    val amount: Double = 0.0,
)
