package com.example.cookhub.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.cookhub.models.Direction
import com.example.cookhub.models.Ingredient
import com.example.cookhub.models.Recipe
import com.example.cookhub.models.RecipeWithDetails


@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeWithDetails(id: Int): RecipeWithDetails

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert
    suspend fun insertIngredients(ingredients: List<Ingredient>)

    @Insert
    suspend fun insertDirections(directions: List<Direction>)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Update
    suspend fun updateIngredients(ingredients: List<Ingredient>)

    @Update
    suspend fun updateDirections(directions: List<Direction>)
}

