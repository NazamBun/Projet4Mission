package com.aura.data.repository

import com.aura.data.bo.account.AccountRequest
import com.aura.data.bo.account.AccountResponse
import com.aura.data.bo.login.LoginRequest
import com.aura.data.bo.login.LoginResponse
import com.aura.data.bo.transfer.TransferRequest
import com.aura.data.bo.transfer.TransferResponse
import retrofit2.Response

interface MyRepository {
    suspend fun login(request: LoginRequest): Response<LoginResponse>
    suspend fun accounts(request: AccountRequest): Response<List<AccountResponse>>
    suspend fun transfer(request: TransferRequest): Response<TransferResponse>
}
