package com.example.bookshelf.data

import com.example.bookshelf.network.BookshelfApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface BookshelfAppContainer {
    val bookshelfRepository: BookshelfRepository
}

class DefaultBookshelfAppContainer: BookshelfAppContainer {
    private val baseUrl = "https://www.googleapis.com/books/v1/"
    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val bookshelfApiService: BookshelfApiService by lazy {
        retrofit.create(BookshelfApiService::class.java)
    }

    override val bookshelfRepository: BookshelfRepository by lazy {
        NetworkBookshelfRepository(bookshelfApiService)
    }

}