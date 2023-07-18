package com.example.bookshelf.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.network.BookData
import com.example.bookshelf.network.BookshelfApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface BookshelfUiState {
    data class Success(
        val uiBookDataList: List<UiBookData>
    ) : BookshelfUiState

    object Error : BookshelfUiState
    object Loading : BookshelfUiState
}

data class UiBookData(
    val id: String,
    val thumbnailUrl: String,
)

class BookshelfViewModel : ViewModel() {
    private var _bookshelfUiState: MutableStateFlow<BookshelfUiState> =
        MutableStateFlow(BookshelfUiState.Loading)
    val bookshelfUiState = _bookshelfUiState.asStateFlow()
    var searchTerm: String by mutableStateOf("")

    fun changeSearchTerm(newValue: String) {
        searchTerm = newValue
    }

    fun getBooks(searchCategory: String = "jazz+history") {
        viewModelScope.launch {
            try {
                _bookshelfUiState.value = BookshelfUiState.Loading
                val bookSearchResult = BookshelfApi.retrofitService.getBookIds(searchCategory)
                val uiBookDataList = mutableListOf<UiBookData>()
                for (book in bookSearchResult.books) {
                    val bookData: BookData = BookshelfApi.retrofitService.getBookData(book.id)
                    if (bookData.volumeInfo.imageLinks?.thumbnail != null) {
                        val httpsThumbnailLink = bookData.volumeInfo.imageLinks
                            .thumbnail.replace("http", "https")
                        uiBookDataList.add(UiBookData(bookData.id, httpsThumbnailLink))
                    }
                }
                _bookshelfUiState.value = BookshelfUiState.Success(uiBookDataList)
            } catch (e: IOException) {
                e.message?.let { Log.e("BookshelfErrorIO", it) }
                _bookshelfUiState.value = BookshelfUiState.Error
            } catch (e: HttpException) {
                e.message?.let { Log.e("BookshelfErrorHttp", it) }
                _bookshelfUiState.value = BookshelfUiState.Error
            }
        }
    }

    init {
        getBooks()
    }
}