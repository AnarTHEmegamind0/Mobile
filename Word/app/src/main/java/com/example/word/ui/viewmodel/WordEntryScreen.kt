package com.example.word.ui.viewmodel



import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.word.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WordEntryScreen(
    navController: NavHostController,
    viewModel: WordViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.word_entry)) },
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
    ) {innerPadding ->
        WordEntryBody(
            viewModel = viewModel,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.save()
                    navController.popBackStack()
                }
            },
            onCancelClick = {
                navController.popBackStack()
            },
            modifier = Modifier.padding(innerPadding)

        )
    }
}
@Composable
fun WordEntryBody(
    viewModel: WordViewModel,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp)
    ) {
        InputForm(viewModel)
        Spacer(modifier = Modifier.padding(vertical = 16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onSaveClick,
                enabled = viewModel.isValid(),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.save_action))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onCancelClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.cancel_action))
            }
        }
    }
}


@Composable
fun InputForm(
    viewModel: WordViewModel
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = viewModel.mongolian.value,
            onValueChange = { viewModel.mongolian.value = it },
            label = { Text(text = "Mongolian Word") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(vertical = 16.dp))
        OutlinedTextField(
            value = viewModel.english.value,
            onValueChange = { viewModel.english.value = it },
            label = { Text(text = "English Word") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
