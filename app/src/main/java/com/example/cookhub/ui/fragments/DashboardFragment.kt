package com.example.cookhub.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookhub.R
import com.example.cookhub.RecipeViewModel
import com.example.cookhub.adapters.RecipeAdapter
import com.example.cookhub.databinding.DashboardLayoutBinding

class DashboardFragment: Fragment() {
    private var _binding: DashboardLayoutBinding? = null
    private val binding get() =  _binding!!

    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DashboardLayoutBinding.inflate(inflater, container, false)
        binding.fabAddRecipe.setOnClickListener{
            findNavController().navigate(R.id.action_dashboardFragment_to_addRecipeFragment2)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.recipes?.observe(viewLifecycleOwner){ recipes ->
            val adapter = RecipeAdapter(recipes, object : RecipeAdapter.RecipeListener{
                override fun onRecipeClicked(index: Int) {
                    val recipe = recipes[index]
                    viewModel.setRecipe(recipe)
                    findNavController().navigate(
                        R.id.action_dashboardFragment_to_detailRecipeFragment, bundleOf("recipeId" to recipe.id)
                    )
                }

                override fun onRecipeLongClicked(index: Int) {
                    Toast.makeText(requireContext(), recipes[index].title, Toast.LENGTH_SHORT).show()

                }
            })
            binding.recipeCardsRecyclerView.adapter = adapter
            binding.recipeCardsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
        
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val recipeToDelete = (binding.recipeCardsRecyclerView.adapter as RecipeAdapter).recipeAt(position)
                viewModel.deleteRecipeAsync(recipeToDelete)
            }
        }).attachToRecyclerView(binding.recipeCardsRecyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}