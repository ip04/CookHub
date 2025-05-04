package com.example.cookhub.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.cookhub.R
import com.example.cookhub.RecipeViewModel
import com.example.cookhub.adapters.DirectionAdapter
import com.example.cookhub.adapters.IngredientAdapter
import com.example.cookhub.databinding.DetailRecipeLayoutBinding
import kotlinx.coroutines.launch

class DetailRecipeFragment: Fragment() {

    private var _binding: DetailRecipeLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var directionAdapter: DirectionAdapter

    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailRecipeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ingredientAdapter = IngredientAdapter(emptyList())
        directionAdapter = DirectionAdapter(emptyList())

        binding.recyclerIngredients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientAdapter
        }

        binding.recyclerDirections.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = directionAdapter
        }

        val recipeId = arguments?.getInt("recipeId") ?: 0
        lifecycleScope.launch {
            val recipeWithDetails = viewModel.getRecipeWithDetails(recipeId)
            if (recipeWithDetails != null) {
                binding.recipeTitle.text = recipeWithDetails.recipe.title
                binding.authorName.text = recipeWithDetails.recipe.author
                binding.timeTV.text = recipeWithDetails.recipe.time
                binding.difficultyTV.text = recipeWithDetails.recipe.difficulty
                Glide.with(requireContext())
                    .load(recipeWithDetails.recipe.photo)
                    .circleCrop()
                    .error(R.mipmap.ic_launcher)
                    .into(binding.recipeImage)
                ingredientAdapter = IngredientAdapter(recipeWithDetails.ingredients)
                directionAdapter = DirectionAdapter(recipeWithDetails.directions)
                binding.recyclerIngredients.adapter = ingredientAdapter
                binding.recyclerDirections.adapter = directionAdapter
            } else {
                Toast.makeText(context, "Failed to load recipe details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}