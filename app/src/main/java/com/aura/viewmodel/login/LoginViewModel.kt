package com.aura.viewmodel.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.bo.account.AccountRequest
import com.aura.data.bo.login.LoginRequest
import com.aura.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: MyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>(Channel.CONFLATED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onIdentifierChanged(identifier: String) {
        _uiState.update { currentState ->
            currentState.copy(
                identifier = identifier,
                isLoginButtonEnabled = identifier.isNotEmpty() && currentState.password.isNotEmpty()
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                isLoginButtonEnabled = password.isNotEmpty() && currentState.identifier.isNotEmpty()
            )
        }
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isLoading = true, showErrorMessage = false, showRetryButton = false) }



            val responseAccount = repository.account(AccountRequest("1234"))
            Log.d("LoginViewModel", "onLoginClicked: $responseAccount")



            // Appeler le repository pour effectuer la connexion
            val response = try {
                repository.login(LoginRequest(_uiState.value.identifier, _uiState.value.password))
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Erreur de connexion : ${e.localizedMessage}",
                        showErrorMessage = true,
                        showRetryButton = true
                    )
                }
                return@launch
            }

            if (response.isSuccessful && response.body()?.granted == true) {
                _navigationEvent.send(NavigationEvent.ShowSnackbar("Authentification réussie!"))
                _navigationEvent.send(NavigationEvent.NavigateToHome)
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Identifiants incorrects",
                        showErrorMessage = true,
                        showRetryButton = true
                    )
                }
            }

            _uiState.update { currentState -> currentState.copy(isLoading = false) }
        }
    }

    fun onRetryClicked() {
        onLoginClicked() // Réessayer la connexion
    }
}
