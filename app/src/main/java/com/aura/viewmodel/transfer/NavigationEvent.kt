package com.aura.viewmodel.transfer

sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
}