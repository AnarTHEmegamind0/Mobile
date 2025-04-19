package com.example.word.ui.viewmodel

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.word.room.Word
import kotlinx.coroutines.launch
import com.example.word.R

@OptIn(ExperimentalMaterial3Api::class)@Composable
fun WordEditScreen(
    navController: NavController,
    viewModel: WordViewModel,
    wordId: Long,

    ) {
    val coroutineScope = rememberCoroutineScope()
    var wordList by remember { mutableStateOf(null as Word?) }
    LaunchedEffect(wordId) {
        wordList = viewModel.getItem(wordId)
        wordList?.let {
            viewModel.mongolian.value = it.mongolian
            viewModel.english.value = it.english
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.edit_word_title)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp()  }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        wordList?.let { word ->
            WordEntryBody(
                viewModel = viewModel,
                onSaveClick = {
                    coroutineScope.launch {
                        word.mongolian = viewModel.mongolian.value
                        word.english = viewModel.english.value
                        if (viewModel.isValid()) {
                            viewModel.update(word)
                            navController.popBackStack()
                        }
                    }
                },
                onCancelClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}


