package com.aura.viewmodel.login

sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
    data class ShowSuccessAndNavigate(val message: String) : NavigationEvent()
    data class ShowSnackbar(val message: String) : NavigationEvent()
}