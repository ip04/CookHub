package com.example.cookhub.data.repository

import android.app.Application
import com.example.cookhub.data.local_db.RecipeDao
import com.example.cookhub.data.local_db.RecipeDataBase
import com.example.cookhub.models.Direction
import com.example.cookhub.models.Ingredient
import com.example.cookhub.models.Recipe

class RecipeRepository(application: Application) {

    private var recipeDao: RecipeDao?

    init {
        val db = RecipeDataBase.getDatabase(application.applicationContext)
        recipeDao = db?.recipeDao()
    }

    fun getAllRecipes() = recipeDao?.getAllRecipes()

    suspend fun getRecipeWithDetails(id: Int) = recipeDao?.getRecipeWithDetails(id)

    suspend fun insertRecipe(recipe: Recipe) = recipeDao?.insertRecipe(recipe)

    suspend fun insertIngredients(ingredients: List<Ingredient>){
        recipeDao?.insertIngredients(ingredients)
    }

    suspend fun insertDirections(directions: List<Direction>){
        recipeDao?.insertDirections(directions)
    }

    suspend fun deleteRecipe(recipe: Recipe){
        recipeDao?.deleteRecipe(recipe)
    }

    suspend fun deleteAllRecipes(){
        recipeDao?.deleteAllRecipes()
    }

    suspend fun addFullRecipe(recipe: Recipe, ingredients: List<Ingredient>, directions: List<Direction>) {
        val recipeId = recipeDao?.insertRecipe(recipe)?.toInt()
            ?: throw IllegalStateException("Recipe insertion failed")

        val updatedIngredients = ingredients.map { it.copy(recipeId = recipeId) }
        val updatedDirections = directions.map { it.copy(recipeId = recipeId) }

        recipeDao?.insertIngredients(updatedIngredients)
        recipeDao?.insertDirections(updatedDirections)
    }

    suspend fun updateFullRecipe(recipe: Recipe, ingredients: List<Ingredient>, directions: List<Direction>) {
        recipeDao?.updateRecipe(recipe)
        val existingDetails = recipeDao?.getRecipeWithDetails(recipe.id)
        if (existingDetails != null) {
            val existingIngredients = existingDetails.ingredients
            val existingDirections = existingDetails.directions
            // מחיקת רשומות שהוסרו
            val ingredientsToDelete = existingIngredients.filter { existing ->
                ingredients.none { it.id == existing.id }
            }
            val directionsToDelete = existingDirections.filter { existing ->
                directions.none { it.id == existing.id }
            }
            recipeDao?.deleteIngredients(ingredientsToDelete)
            recipeDao?.deleteDirections(directionsToDelete)
        }
        // עדכון והוספת רשומות
        ingredients.forEach { it.recipeId = recipe.id }
        directions.forEach { it.recipeId = recipe.id }
        recipeDao?.updateIngredients(ingredients.filter { it.id != 0 }) // עדכון מצרכים קיימים
        recipeDao?.insertIngredients(ingredients.filter { it.id == 0 }) // הוספת מצרכים חדשים
        recipeDao?.updateDirections(directions.filter { it.id != 0 }) // עדכון שלבים קיימים
        recipeDao?.insertDirections(directions.filter { it.id == 0 }) // הוספת שלבים חדשים
    }
}
