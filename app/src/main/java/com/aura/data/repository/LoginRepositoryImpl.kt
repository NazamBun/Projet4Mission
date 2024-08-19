package com.aura.data.repository


import com.aura.data.login.LoginRequest
import com.aura.data.login.LoginResponse
import com.aura.data.service.ApiService
import retrofit2.Response

class LoginRepositoryImpl(private val apiService: ApiService) : LoginRepository {
    override suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }
}