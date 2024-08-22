package com.aura.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.aura.data.repository.LoginRepository

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository): ViewModel(){

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent> (Channel.CONFLATED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onIdentifierChanged(identifier: String){
        _uiState.update { currentState ->
                currentState.copy(identifier = identifier,
                                  isLoginButtonEnabled = identifier.isNotEmpty() && currentState.password.isNotEmpty())
        }
    }

    fun onPasswordChanged(password: String){
        _uiState.update { currentState ->
                currentState.copy(password = password,
                                  isLoginButtonEnabled = password.isNotEmpty() && currentState.identifier.isNotEmpty())
        }
    }

    fun onLoginClicked(){
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isLoading = true) }
            //TODO: Appeler le repository pour effectuer la connexion
            _navigationEvent.send(NavigationEvent.NavigateToHome)
            _uiState.update { currentState -> currentState.copy(isLoading = false) }
        }

    }
}