package com.aura.data.repository


import com.aura.data.bo.account.AccountRequest
import com.aura.data.bo.account.AccountResponse
import com.aura.data.bo.login.LoginRequest
import com.aura.data.bo.login.LoginResponse
import com.aura.data.bo.transfer.TransferRequest
import com.aura.data.bo.transfer.TransferResponse
import com.aura.data.service.ApiService
import retrofit2.Response

class MyRepositoryImpl(private val apiService: ApiService) : MyRepository {
    override suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    override suspend fun account(request: AccountRequest): Response<List<AccountResponse>> {
        return apiService.account(request)
    }

    override suspend fun transfer(request: TransferRequest): Response<TransferResponse> {
        return apiService.transfer(request)
    }

}