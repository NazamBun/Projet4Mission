package com.aura.viewmodel.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val showRetryButton: Boolean = false,
    val error: String = "",
    val showErrorMessage: Boolean = false,

)
