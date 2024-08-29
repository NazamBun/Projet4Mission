package com.aura.viewmodel.login

sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
    object ShowSuccessAndNavigate : NavigationEvent()
}