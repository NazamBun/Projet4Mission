package com.aura.data.service

import com.aura.data.bo.account.AccountRequest
import com.aura.data.bo.account.AccountResponse
import com.aura.data.bo.login.LoginRequest
import com.aura.data.bo.login.LoginResponse
import com.aura.data.bo.transfer.TransferRequest
import com.aura.data.bo.transfer.TransferResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/account")
    suspend fun account(@Body request: AccountRequest): Response<List<AccountResponse>>

    @POST("/transfer")
    suspend fun transfer(@Body request: TransferRequest): Response<TransferResponse>
}