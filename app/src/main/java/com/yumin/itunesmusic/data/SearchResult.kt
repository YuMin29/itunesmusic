package com.yumin.itunesmusic.data


import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val results: List<Result>
)