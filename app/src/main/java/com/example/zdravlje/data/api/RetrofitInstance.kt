package com.example.zdravlje.data.api

import android.util.Log
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.zdravlje.services.avc.SampleApplication
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.nites.ezdravljeapp.BuildConfig
import java.util.concurrent.TimeUnit


object RetrofitInstance {
    var AUTH_TOKEN = ""

    private var client = OkHttpClient.Builder().apply {
        addInterceptor(AuthInterceptor())
        addInterceptor(
            ChuckerInterceptor.Builder(SampleApplication.context!!)
                .collector(ChuckerCollector(SampleApplication.context!!))
                .maxContentLength(250000L)
                .redactHeaders(emptySet())
                .alwaysReadResponseBody(false)
                .build()
        )
        Log.d("Auth", AUTH_TOKEN)
        readTimeout(1000, TimeUnit.SECONDS)
        callTimeout(1000, TimeUnit.SECONDS)
        writeTimeout(1000, TimeUnit.SECONDS)
        connectTimeout(1000, TimeUnit.SECONDS)
    }.build()

    private val gson = GsonBuilder()
        .serializeNulls()  // Ovde dozvoljavamo da null vrednosti budu uključene u JSON
        .create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Koristimo custom Gson sa serializeNulls
            .build()
    }

    val appApi: AppApi by lazy {
        retrofit.create(AppApi::class.java)
    }

}