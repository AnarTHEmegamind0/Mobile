package com.example.word


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.word.repository.OfflineWordRepository
import com.example.word.room.WordDB
import com.example.word.ui.navigation.WordEditDestination
import com.example.word.ui.theme.WordTheme
import com.example.word.ui.viewmodel.MainScreen
import com.example.word.ui.viewmodel.SettingsScreen
import com.example.word.ui.viewmodel.WordEditScreen
import com.example.word.ui.viewmodel.WordEntryScreen
import com.example.word.ui.viewmodel.WordViewModel
import com.example.word.work.scheduleNotificationWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyAppContent()

                }
            }
        }
    }


}

@Composable
fun MyAppContent() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = WordDB.getDatabase(context = context)
    Log.d("MainActivity", "Database initialized: $db")
    val repository = OfflineWordRepository(db.WordDAO(), context)
    val myviewmodel = WordViewModel(repository)

    NavHost(navController = navController, startDestination = "home_screen") {
        composable("home_screen") {
            MainScreen(
                navigateToWordEntry = { navController.navigate("word_entry_screen") },
                navigateToWordUpdate = {
                    navController.navigate("${ WordEditDestination.route}/${it}")
                },
                navigateToSettings = { navController.navigate("settings_screen") },
                viewModel = myviewmodel)
        }
        composable("word_entry_screen") {
            WordEntryScreen(navController = navController, viewModel = myviewmodel)
        }
        composable("settings_screen") { // Added
            SettingsScreen(navController= navController, viewModel = myviewmodel)
        }
        composable(
            route = WordEditDestination.routeWithArgs,
            arguments = listOf(navArgument(WordEditDestination.wordIdArg) {
                type = NavType.LongType
            })
        ) { navBackStackEntry ->
            val wordId = navBackStackEntry.arguments?.getLong(WordEditDestination.wordIdArg)
            if (wordId != null) {
                WordEditScreen(
                    navController = navController,
                    viewModel = myviewmodel,
                    wordId = wordId
                )
            } else {
                Log.e("Navigation", "Item ID is null")
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyAppContent()
}

