package com.example.word.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var mongolian: String,
    var english: String
)
