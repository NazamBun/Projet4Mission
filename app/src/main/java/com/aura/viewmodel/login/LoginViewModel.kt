package com.aura.viewmodel.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.login.LoginRequest
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
                currentState.copy(
                    identifier = identifier,
                    isLoginButtonEnabled = identifier.isNotEmpty() && currentState.password.isNotEmpty())
        }
    }

    fun onPasswordChanged(password: String){
        _uiState.update { currentState ->
                currentState.copy(
                    password = password,
                    isLoginButtonEnabled = password.isNotEmpty() && currentState.identifier.isNotEmpty())
        }
    }

    fun onLoginClicked(){
        Log.d(TAG, "onLoginClicked called")
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isLoading = true) }

            Log.d(TAG, "Sending login request to repository")

            // Appeler le repository pour effectuer la connexion
            val response = try {
                repository.login(LoginRequest(_uiState.value.identifier, _uiState.value.password))
            } catch (e: Exception) {
                Log.e(TAG, "Login request failed with exception: ${e.localizedMessage}")
                // Gestion des exceptions, par exemple, pour les erreurs de réseau
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Erreur de connexion : ${e.localizedMessage}"
                    )
                }
                return@launch
            }

            Log.d(TAG, "Received response: ${response.isSuccessful}, ${response.body()?.granted}")

            if (response.isSuccessful && response.body()?.granted == true) {
                Log.d(TAG, "Login successful, navigating to home")
                _navigationEvent.send(NavigationEvent.NavigateToHome)
            } else {
                Log.d(TAG, "Login failed, showing error message")
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Identifiants incorrects"
                    )
                }
            }
            _uiState.update { currentState -> currentState.copy(isLoading = false) }
        }
    }

}