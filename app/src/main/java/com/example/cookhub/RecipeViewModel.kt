package com.example.cookhub

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cookhub.data.repository.RecipeRepository
import com.example.cookhub.models.Direction
import com.example.cookhub.models.Ingredient
import com.example.cookhub.models.Recipe
import com.example.cookhub.models.RecipeWithDetails
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application): AndroidViewModel(application) {

    private val repository = RecipeRepository(application)

    val recipes: LiveData<List<Recipe>>? = repository.getAllRecipes()

    private val _chosenRecipe = MutableLiveData<Recipe>()
    val chosenRecipe: LiveData<Recipe> get() = _chosenRecipe

    fun setRecipe(recipe: Recipe) {
        _chosenRecipe.value = recipe
    }

    suspend fun getRecipeWithDetails(id: Int) = repository.getRecipeWithDetails(id)

    suspend fun insertRecipe(recipe: Recipe) = repository.insertRecipe(recipe)

    suspend fun insertIngredients(ingredients: List<Ingredient>){
        repository.insertIngredients(ingredients)
    }

    suspend fun insertDirections(directions: List<Direction>){
        repository.insertDirections(directions)
    }

    suspend fun deleteRecipe(recipe: Recipe){
        repository.deleteRecipe(recipe)
    }

    fun deleteRecipeAsync(recipe: Recipe) {
        viewModelScope.launch {
            deleteRecipe(recipe)
        }
    }

    fun saveFullRecipe(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        directions: List<Direction>
    ) {
        viewModelScope.launch {
            repository.addFullRecipe(recipe, ingredients, directions)
        }
    }

}

/*
    fun addFullRecipe(recipe: Recipe, ingredients: List<Ingredient>, directions: List<Direction>) {
        repository.addFullRecipe(recipe, ingredients, directions)
    }

    fun addIngredients(ingredients: List<Ingredient>) {
        repository.addIngredients(ingredients)
    }

    fun addDirections(directions: List<Direction>) {
        repository.addDirections(directions)
    }

    fun addRecipe(recipe: Recipe){
        repository.addRecipe(recipe)
    }

    fun deleteRecipe(recipe: Recipe){
        repository.deleteRecipe(recipe)
    }
 */