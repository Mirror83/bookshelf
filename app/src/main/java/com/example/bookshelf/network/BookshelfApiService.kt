package com.example.bookshelf.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookshelfApiService {
    @GET("volumes")
    suspend fun getBookIds(
        @Query("q")
        searchCategory: String
    ): BookSearchResult

        @GET("volumes/{volumeId}")
    suspend fun getBookData(@Path("volumeId") volumeId: String) : BookData
}

