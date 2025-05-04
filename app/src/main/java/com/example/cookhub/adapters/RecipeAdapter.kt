package com.example.cookhub.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cookhub.models.Recipe
import com.example.cookhub.databinding.RecipeCardLayoutBinding

class RecipeAdapter(val recipes: List<Recipe>, val callBack: RecipeListener): RecyclerView.Adapter<RecipeAdapter.ItemViewHolder>() {

    interface RecipeListener{
        fun onRecipeClicked(index: Int)
        fun onRecipeLongClicked(index: Int)
    }


    inner class ItemViewHolder(private val binding: RecipeCardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, OnLongClickListener{

            init {
                binding.root.setOnClickListener(this)
                binding.root.setOnLongClickListener(this)
            }

        override fun onClick(v: View?) {
            callBack.onRecipeClicked(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            callBack.onRecipeLongClicked(adapterPosition)
            return false
        }

        fun bind(recipe: Recipe){
            binding.itemTitle.text = recipe.title
            binding.itemAuthor.text = recipe.author
            binding.itemTime.text = recipe.time
            binding.itemDifficulty.text = recipe.difficulty
            Glide.with(binding.root).load(recipe.photo).circleCrop().into(binding.itemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(RecipeCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))


    fun recipeAt(position: Int) = recipes[position]

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind(recipes[position])

    override fun getItemCount(): Int = recipes.size
}