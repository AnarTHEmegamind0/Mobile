package com.example.word.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.word.room.Word
import com.example.word.room.WordDAO
import com.example.word.ui.ViewMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
private val VIEW_MODE_KEY = stringPreferencesKey("view_mode")

class OfflineWordRepository (private val wordDAO: WordDAO, private val context: Context) : WordRepository {
    override suspend fun insert(word: Word) {
        wordDAO.addItem(word)
    }

    override suspend fun update(word: Word) {
        wordDAO.updateItem(word)
    }

    override suspend fun delete(word: Word) {
        wordDAO.deleteItem(word)
    }
    override suspend fun getItem(id: Long): Word {
        return withContext(Dispatchers.IO) {
            wordDAO.getItem(id)
        }
    }
    override fun getAllItems(): Flow<List<Word>> {
        return wordDAO.getAllItems()
    }

    private val dataStore = context.dataStore
    suspend fun saveViewMode(viewMode: ViewMode) {
        dataStore.edit { preferences ->
            preferences[VIEW_MODE_KEY] = viewMode.name
        }
    }
}