package com.example.cookhub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cookhub.models.Ingredient
import com.example.cookhub.databinding.IngridiantCardLayoutBinding

class IngredientAdapter (val ingredients: List<Ingredient>) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>(){

    class IngredientViewHolder(private val binding: IngridiantCardLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(ingredient: Ingredient){
            binding.name.text = ingredient.name
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

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) =
        holder.bind(ingredients[position])

    override fun getItemCount(): Int = ingredients.size

}