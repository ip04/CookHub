package com.example.cookhub.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        binding.deleteIV.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            val title = TextView(requireContext()).apply {
                text = getString(R.string.dialog_title)
                setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                textSize = 20f
                setPadding(32, 48, 32, 24) // ריווח פנימי
            }
            builder.apply {
                setCustomTitle(title)
                setMessage(R.string.dialog_text)
                setCancelable(false)
                setIcon(R.drawable.baseline_delete_24)
                setPositiveButton(getString(R.string.dialog_yes)){ _, _ ->
                    viewModel.deleteAllRecipes()
                    Toast.makeText(requireContext(),
                        getString(R.string.all_recipes_deleted), Toast.LENGTH_SHORT).show()
                }

                setNegativeButton(getString(R.string.dialog_no)){ dialog, _->
                    dialog.dismiss()
                }
            }
            val dialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                resources.getColor(android.R.color.holo_red_dark, null)
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                resources.getColor(android.R.color.holo_blue_dark, null)
            )
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