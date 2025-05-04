package com.example.cookhub.models

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithDetails (
    @Embedded val recipe: Recipe,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<Ingredient>,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val directions: List<Direction>
)

