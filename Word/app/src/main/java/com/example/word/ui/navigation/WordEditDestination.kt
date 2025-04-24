package com.example.word.ui.navigation


object WordEditDestination {
    val route = "word_edit_screen"
    const val wordIdArg = "wordId"
    val routeWithArgs = "$route/{$wordIdArg}"
}