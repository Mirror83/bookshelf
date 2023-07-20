package com.example.bookshelf

import android.app.Application
import com.example.bookshelf.data.BookshelfAppContainer
import com.example.bookshelf.data.DefaultBookshelfAppContainer

class BookshelfApplication : Application() {
    lateinit var container: BookshelfAppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultBookshelfAppContainer()
    }
}