package com.example.bookshelf.data

import com.example.bookshelf.network.BookData
import com.example.bookshelf.network.BookshelfApiService
import com.example.bookshelf.ui.UiBookData

interface BookshelfRepository {
    suspend fun getUiBookData(
        searchCategory: String,
    ): List<UiBookData>
}

class NetworkBookshelfRepository(
    private val bookshelfApiService: BookshelfApiService
) : BookshelfRepository {
    override suspend fun getUiBookData(
        searchCategory: String,
    ): List<UiBookData> {
        val bookSearchResult = bookshelfApiService.getBookIds(searchCategory)
        val uiBookDataList = mutableListOf<UiBookData>()
        for (book in bookSearchResult.books) {
            val bookData: BookData = bookshelfApiService.getBookData(book.id)
            if (bookData.volumeInfo.imageLinks?.thumbnail != null) {
                val httpsThumbnailLink = bookData.volumeInfo.imageLinks
                    .thumbnail.replace("http", "https")
                uiBookDataList.add(UiBookData(bookData.id, httpsThumbnailLink))
            }
        }
        return uiBookDataList.toList()
    }

}