package com.example.bookshelf.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
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


private const val BASE_URL = "https://www.googleapis.com/books/v1/"

val json = Json { ignoreUnknownKeys = true }
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

object BookshelfApi {
    val retrofitService: BookshelfApiService by lazy {
        retrofit.create(BookshelfApiService::class.java)
    }
}

