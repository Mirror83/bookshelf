package com.example.bookshelf.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.BookshelfApplication
import com.example.bookshelf.data.BookshelfRepository
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

class BookshelfViewModel(
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
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
                val uiBookDataList: List<UiBookData> =
                    bookshelfRepository.getUiBookData(searchCategory)
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

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as BookshelfApplication
                val container = application.container
                val bookshelfRepository = container.bookshelfRepository
                BookshelfViewModel(bookshelfRepository)
            }
        }
    }
}