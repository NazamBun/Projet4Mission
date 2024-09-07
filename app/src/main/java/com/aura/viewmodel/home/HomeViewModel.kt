package com.aura.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.bo.account.AccountRequest
import com.aura.data.bo.account.AccountResponse
import com.aura.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MyRepository): ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _accounts = MutableLiveData<List<AccountResponse>>()
    val accounts: LiveData<List<AccountResponse>> = _accounts

    fun getAccounts() {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isLoading = true, showErrorMessage = false, showRetryButton = false) }
            try {
                val response = repository.accounts(AccountRequest(id = "1234"))
                _accounts.value = response.body()
                _uiState.update { currentState -> currentState.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { currentState -> currentState.copy(isLoading = false, showErrorMessage = true, showRetryButton = true) }
            }
        }
    }
}