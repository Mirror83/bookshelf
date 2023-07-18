package com.example.bookshelf.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookshelf.R

@Composable
fun BookshelfApp(
    modifier: Modifier = Modifier,
    bookshelfViewModel: BookshelfViewModel = viewModel(),
) {
    val bookshelfUiState by
    bookshelfViewModel.bookshelfUiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(4.dp)
    ) {
        BookshelfSearchBar(
            searchTerm = bookshelfViewModel.searchTerm,
            changeSearchTerm = bookshelfViewModel::changeSearchTerm,
            searchByCategory = bookshelfViewModel::getBooks
        )
        when (bookshelfUiState) {
            is BookshelfUiState.Success -> BookshelfScreen(
                (bookshelfUiState as BookshelfUiState.Success).uiBookDataList
            )

            is BookshelfUiState.Loading -> LoadingScreen()
            is BookshelfUiState.Error -> ErrorScreen(
                bookshelfViewModel::getBooks,
                searchTerm = bookshelfViewModel.searchTerm
            )
        }
    }

}

@Composable
fun BookshelfScreen(
    uiBookDataList: List<UiBookData>,
    modifier: Modifier = Modifier
) {
    if (uiBookDataList.isEmpty()) {
        Text(stringResource(R.string.no_results_found))
    } else {
        LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = modifier) {
            items(uiBookDataList, key = { it.id }) {
                BookThumbnail(thumbnailUrl = it.thumbnailUrl)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfSearchBar(
    searchTerm: String,
    changeSearchTerm: (String) -> Unit,
    searchByCategory: (searchCategory: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = searchTerm,
            onValueChange = { changeSearchTerm(it) },
            label = { Text("Category") },
            placeholder = { Text(stringResource(id = R.string.default_search_category)) },
            modifier = Modifier
                .padding(bottom = 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    searchByCategory(searchTerm)
                    focusManager.clearFocus()
                }
            )
        )
    }
}


@Composable
fun BookThumbnail(thumbnailUrl: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(thumbnailUrl)
            .placeholder(R.drawable.baseline_sync_24)
            .error(R.drawable.baseline_broken_image_24)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(0.5f)
            .padding(4.dp)
            .fillMaxWidth(),

        )
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(stringResource(R.string.loading))
    }
}

@Composable
fun ErrorScreen(
    searchByCategory: (searchCategory: String) -> Unit,
    searchTerm: String,
    modifier: Modifier = Modifier
) {
    val defaultSearchCategory = stringResource(id = R.string.default_search_category)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(stringResource(R.string.error), modifier = Modifier.padding(8.dp))
        Button(onClick = {
            if (searchTerm.isEmpty()) {
                searchByCategory(defaultSearchCategory)
            } else {
                searchByCategory(searchTerm)
            }
        }) {
            Text(stringResource(R.string.retry))
        }
    }
}