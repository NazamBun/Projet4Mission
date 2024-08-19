package com.aura.viewmodel.login

data class LoginUiState (
    val identifier: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String = "",
    val isLoginButtonEnabled: Boolean = false
)