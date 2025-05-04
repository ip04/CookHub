package com.example.cookhub.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "directions",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE // מחיקה של מתכון תמחק את ההוראות
        )
    ]
)
data class Direction(
    val description:String,
    val recipeId:Int
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
