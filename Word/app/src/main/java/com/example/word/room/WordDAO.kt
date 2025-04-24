package com.example.word.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
@Dao
interface WordDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItem(word: Word)

    @Delete
    suspend fun deleteItem(word: Word)

    @Update
    suspend fun updateItem(word: Word)

    @Query("SELECT * FROM word")
    fun getAllItems(): Flow<List<Word>>

    @Query("SELECT * from word WHERE id = :id")
    suspend fun getItem(id: Long): Word
}