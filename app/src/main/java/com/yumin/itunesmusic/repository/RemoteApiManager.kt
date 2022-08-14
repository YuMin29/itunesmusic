package com.yumin.itunesmusic.repository

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RemoteApiManager {

    private const val BASE_URL = "https://itunes.apple.com/"
    val remoteApiService: RemoteApiService

    init {
        val okHttpClient: OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .build()

        val retrofit: Retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        remoteApiService = retrofit.create(RemoteApiService::class.java)
    }
}