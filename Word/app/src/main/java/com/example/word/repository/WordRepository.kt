package com.example.word.repository

import com.example.word.room.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    suspend fun insert(word: Word)
    suspend fun update(word: Word)
    suspend fun delete(word: Word)
    suspend fun getItem(id: Long): Word
    fun getAllItems(): Flow<List<Word>>
}