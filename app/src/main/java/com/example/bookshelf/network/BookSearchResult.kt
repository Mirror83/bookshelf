package com.example.bookshelf.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookSearchResult(
    @SerialName("items")
    val books: List<Book>
)

@Serializable
data class Book(
    val id: String,
)

