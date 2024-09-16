package com.aura

import com.aura.data.bo.login.LoginRequest
import com.aura.data.bo.login.LoginResponse
import com.aura.data.service.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class ApiServiceTest {

    @Mock
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `login with valid credentials returns granted true`() = runTest  {
        val request = LoginRequest("validId", "validPassword")
        val response = Response.success(LoginResponse(true))
        `when`(apiService.login(request)).thenReturn(response)

        val result = apiService.login(request)

        assertEquals(true, result.body()?.granted)
    }

    @Test
    fun `login with invalid credentials returns granted false`() = runTest  {
        val request = LoginRequest("invalidId", "invalidPassword")
        val response = Response.success(LoginResponse(false))
        `when`(apiService.login(request)).thenReturn(response)

        val result = apiService.login(request)

        assertEquals(false, result.body()?.granted)
    }

    @Test
    fun `login with valid credentials returns 200 OK`() = runTest  {
        val request = LoginRequest("validId", "validPassword")
        val response = Response.success(LoginResponse(true))
        `when`(apiService.login(request)).thenReturn(response)

        val result = apiService.login(request)

        assertEquals(200, result.code())
    }

    @Test
    fun `login with invalid credentials returns 401 Unauthorized`() = runTest  {
        val request = LoginRequest("invalidId", "invalidPassword")
        val errorResponse = "Unauthorized".toResponseBody()
        val response = Response.error<LoginResponse>(401, errorResponse)
        `when`(apiService.login(request)).thenReturn(response)

        val result = apiService.login(request)

        assertEquals(401, result.code())
    }

    @Test
    fun `login with empty credentials returns 400 Bad Request`() = runTest  {
        val request = LoginRequest("", "")
        val errorResponse = "Bad Request".toResponseBody()
        val response = Response.error<LoginResponse>(400, errorResponse)
        `when`(apiService.login(request)).thenReturn(response)

        val result = apiService.login(request)

        assertEquals(400, result.code())
    }
}