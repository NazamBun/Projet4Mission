package com.aura.data.service

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.14:8080"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(MoshiInstance.moshi))
            .build()
            .create(ApiService::class.java)
    }
}