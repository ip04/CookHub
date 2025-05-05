package com.example.cookhub.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cookhub.models.Ingredient
import com.example.cookhub.databinding.IngridiantCardLayoutBinding

class IngredientAdapter (var ingredients: List<Ingredient>) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>(){

    class IngredientViewHolder(private val binding: IngridiantCardLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(ingredient: Ingredient){
            binding.name.text = ingredient.name
            Log.d("IngredientAdapter", "Completed binding for ${ingredient.name} at position $adapterPosition")
            binding.root.post {
                val heightPx = binding.root.height
                val heightDp = heightPx / binding.root.resources.displayMetrics.density
                Log.d("IngredientAdapter", "Item at position $adapterPosition height: ${heightPx}px (${heightDp}dp)")
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientViewHolder = IngredientViewHolder(
        IngridiantCardLayoutBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        Log.d("IngredientAdapter", "Binding ingredient at position $position: ${ingredients[position].name}")
        holder.bind(ingredients[position])
    }

    override fun getItemCount(): Int {
        Log.d("IngredientAdapter", "getItemCount: ${ingredients.size}")
        return ingredients.size
    }

    fun updateIngredients(newIngredients: List<Ingredient>) {
        Log.d("IngredientAdapter", "Updating ingredients: ${newIngredients.map { it.name }}")
        ingredients = newIngredients
        notifyDataSetChanged()
    }
}