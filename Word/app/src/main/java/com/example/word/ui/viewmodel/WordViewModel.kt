package com.example.word.ui.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.word.repository.OfflineWordRepository
import com.example.word.room.Word
import com.example.word.ui.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class WordViewModel (private val repository: OfflineWordRepository) : ViewModel() {
    var mongolian = mutableStateOf("")
    var english = mutableStateOf("")
    //buh list ee avna
    val allWords: Flow<List<Word>> = repository.getAllItems()
    //viewMode oo avna
   private val _viewMode = MutableStateFlow(ViewMode.BOTH)
   val viewMode: StateFlow<ViewMode> = _viewMode

    fun delete(word: Word) {
        viewModelScope.launch {
            repository.delete(word)
        }
    }
    suspend fun getItem(id: Long): Word {
        return repository.getItem(id)
    }
    fun isValid(): Boolean {
        return mongolian.value.isNotBlank() && english.value.isNotBlank()
    }

    suspend fun save() {
        // valid esehe shalgana
        if (isValid()) {
            val word = Word(
                mongolian = mongolian.value,
                english = english.value
            )
            // hadgalna
            repository.insert(word)
        }
    }
    suspend fun update(word: Word?) {
        word?.let {
            repository.update(it)

        }
    }


    //viewMode hadgalah
    fun saveViewMode(viewMode: ViewMode) {
        _viewMode.value = viewMode
        viewModelScope.launch {
            repository.saveViewMode(viewMode)
        }
    }
    private val _wordList = mutableStateOf<List<Word>>(emptyList())
    fun toggleViewMode(currentIndex: Int) {
        val newMode = when (val currentMode = _viewMode.value) {
            ViewMode.BOTH -> {
                val word = _wordList.value.getOrNull(currentIndex)
                if (word != null) {
                    if (english.value.isNotBlank() && mongolian.value.isNotBlank()) {
                        if (viewMode.value == ViewMode.MONGOLIAN_ONLY) ViewMode.ENGLISH_ONLY
                        else ViewMode.MONGOLIAN_ONLY
                    } else {
                        ViewMode.ENGLISH_ONLY
                    }
                } else {
                    currentMode
                }
            }
            ViewMode.ENGLISH_ONLY -> ViewMode.MONGOLIAN_ONLY
            ViewMode.MONGOLIAN_ONLY -> ViewMode.ENGLISH_ONLY
        }
        _viewMode.value = newMode
    }


}