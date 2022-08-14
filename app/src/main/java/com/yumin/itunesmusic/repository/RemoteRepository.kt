package com.yumin.itunesmusic.repository

import android.util.Log
import com.yumin.itunesmusic.data.SearchResult
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteRepository {
    private var remoteApiService: RemoteApiService = RemoteApiManager.remoteApiService

    suspend fun getSearchResult(keyword: String, type: String, country: String): SearchResult {
        return suspendCancellableCoroutine {
            remoteApiService.getSearchResult(keyword, type, country)
                .enqueue(object : Callback<SearchResult> {
                    override fun onResponse(
                        call: Call<SearchResult>,
                        response: Response<SearchResult>
                    ) {
                        it.resumeWith(Result.success(response.body()) as Result<SearchResult>)
                    }

                    override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                        Log.e("[RemoteRepository]", "[getSearchResult] onFailure")
                        it.resumeWith(Result.failure(t))
                    }
                })
        }
    }
}