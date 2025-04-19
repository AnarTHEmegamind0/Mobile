package com.example.word.ui.viewmodel

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import com.example.word.R
import com.example.word.room.Word
import com.example.word.ui.ViewMode
import com.example.word.work.scheduleNotificationWorker

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navigateToWordEntry: () -> Unit,
    navigateToWordUpdate: (Long) -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: WordViewModel
) {
    val cxt = LocalContext.current
    //list
    val wordList by viewModel.allWords.collectAsState(initial = emptyList())
    //viewMode
    val viewMode by viewModel.viewMode.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                actions = {
                    IconButton(
                        onClick = {navigateToSettings()}
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },

    ){ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            if (wordList.isEmpty()) {
                WorkManager.getInstance(cxt).cancelAllWorkByTag("reminder")
                NoWordScreen(
                    navigateToWordEntry = navigateToWordEntry
                )
            } else {
                scheduleNotificationWorker(cxt)
                // Display word list based on view mode
                when (viewMode) {
                    ViewMode.BOTH -> {
                        HomeScreen(
                            wordList = wordList,
                            onEdit = navigateToWordUpdate,
                            onAdd = navigateToWordEntry,
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    ViewMode.ENGLISH_ONLY -> {
                        HomeScreen(
                            wordList = wordList.map { it.copy(mongolian = "") }, // Show only English words
                            onEdit = navigateToWordUpdate,
                            onAdd = navigateToWordEntry,
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    ViewMode.MONGOLIAN_ONLY -> {
                        HomeScreen(
                            wordList = wordList.map { it.copy(english = "") }, // Show only Mongolian words
                            onEdit = navigateToWordUpdate,
                            onAdd = navigateToWordEntry,
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    wordList: List<Word>,
    onEdit: (Long) -> Unit,
    onAdd: () -> Unit,
    viewModel: WordViewModel,
    modifier: Modifier = Modifier
) {
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    val currentWord = wordList.getOrNull(currentIndex)

    Surface(
        modifier = modifier.padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
            currentWord?.let { word ->
                // Mongolian word text view
                Text(
                    text = word.mongolian,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clickable {
                            // Toggle between view modes
                            viewModel.toggleViewMode(currentIndex)
                        },
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    text = word.english,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable {
                            // Toggle between view modes
                            viewModel.toggleViewMode(currentIndex)
                        },
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                // Row for buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Add button
                    Button(
                        onClick = onAdd,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Add")
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    // Update button
                    Button(
                        onClick = { onEdit(word.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Update")
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    // Delete button
                    Button(
                        onClick = { deleteConfirmationRequired = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Delete")
                    }
                    if (deleteConfirmationRequired) {
                        DeleteConfirmationDialog(
                            onDeleteConfirm = {
                                deleteConfirmationRequired = false
                                viewModel.delete(word)
                                val updatedIndex = if (currentIndex == wordList.size - 1) {
                                    0 // If current index is at the end, reset to the beginning
                                } else {
                                    currentIndex // Otherwise, keep the current index
                                }
                                currentIndex = updatedIndex // Update the current index
                            },
                            onDeleteCancel = { deleteConfirmationRequired = false },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Row for previous and next buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Previous button
                    Button(
                        onClick = {
                            currentIndex = (currentIndex - 1 + wordList.size) % wordList.size
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Previous")
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))
                    // Next button
                    Button(
                        onClick = {
                            currentIndex = (currentIndex + 1) % wordList.size
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Next")
                    }
                }
            }
        }
    }
}
@Composable
fun NoWordScreen(
    navigateToWordEntry: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.no_word),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = navigateToWordEntry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Update button
            Button(
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                enabled = false
            ) {
                Text(text = "Update")
            }
            // Delete button
            Button(
                onClick = {  },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, end = 4.dp),
                enabled = false
            ) {
                Text(text = "Delete")
            }
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Previous button
            Button(
                onClick = {  },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, end = 4.dp),
                enabled = false
            ) {
                Text(text = "Previous")
            }
            // Next button
            Button(
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                enabled = false
            ) {
                Text(text = "Next")
            }
        }
    }
}
@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = {  },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        })
}
