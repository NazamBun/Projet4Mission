package com.aura.data.repository

import com.aura.data.login.LoginRequest
import com.aura.data.login.LoginResponse
import retrofit2.Response

interface LoginRepository {
    suspend fun login(request: LoginRequest): Response<LoginResponse>
}