package com.aura.viewmodel.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.bo.transfer.TransferRequest
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
class TransferViewModel @Inject constructor(private val repository: MyRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()
    private val _navigationEvents = Channel<NavigationEvent>(
        Channel.CONFLATED
    )
    val navigationEvents = _navigationEvents.receiveAsFlow()

    fun onRecipientChanged(recipient: String) {
        _uiState.update { currentState ->
            currentState.copy(
                recipient = recipient,
                isTransferButtonEnabled = recipient.isNotEmpty() && currentState.amount > 0.0
            )

        }
    }

    fun onAmountChanged(amount: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                amount = amount,
                isTransferButtonEnabled = amount > 0.0 && currentState.recipient.isNotEmpty()
            )

        }
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    private fun onError(message: String) {
        _uiState.update { currentState ->
            currentState.copy(
                error = message,
                isLoading = false,
                isTransferButtonEnabled = true
            )
        }
    }

    fun resetError() {
        _uiState.update { currentState -> currentState.copy(error = "") }
    }

    fun onTransferClicked(sender: String, recipient: String, amount: Double) {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isLoading = true, error = "") }
            if (amount <= 0.0) {
                onError("Le montant doit être supérieur à 0")

            } else if (recipient.isEmpty()) {
                onError("Le destinataire ne peut pas être vide")

            } else {
                // Appeler le repository pour effectuer le transfert
                val response = repository.transfer(
                    TransferRequest(
                        recipient = recipient,
                        amount = amount,
                        sender = sender
                    )
                )
                if (response.isSuccessful) {
                    if (response.body()?.result == true) {
                        _uiState.update { currentState -> currentState.copy(isLoading = false) }
                        _navigationEvents.send(NavigationEvent.NavigateToHome)
                    } else {
                        onError("balance insuffisante")
                    }

                } else {
                    onError("Erreur lors du transfert")
                }


            }

        }
    }
}