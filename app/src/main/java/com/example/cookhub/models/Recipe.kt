package com.example.cookhub.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    val title: String,
    val author: String?,
    val difficulty: String?,
    val time: String?,
    val photo: String?
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}