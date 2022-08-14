package com.yumin.itunesmusic.repository

import com.yumin.itunesmusic.data.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteApiService {
    @GET("search")
    fun getSearchResult(
        @Query("term") keyword: String,
        @Query("media") type: String,
        @Query("country") country: String
    ): Call<SearchResult>
}