package com.example.bookshelf.network

import kotlinx.serialization.Serializable

@Serializable
data class BookData(
    val id: String,
    val volumeInfo : VolumeInfo
)

@Serializable
data class VolumeInfo(
    val imageLinks: ImageLinks? = null
)

@Serializable
data class ImageLinks(
    val thumbnail: String? = null
)
