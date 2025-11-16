package com.example.pokedex.data.network

import android.content.Context
import com.example.pokedex.data.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    private var apiService: ApiService? = null

    fun getApiService(context: Context): ApiService {
        if (apiService == null) {
            val sessionManager = SessionManager(context)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                    sessionManager.fetchAuthToken()?.let {
                        requestBuilder.addHeader("Authorization", "Bearer $it")
                    }
                    chain.proceed(requestBuilder.build())
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService!!
    }
}