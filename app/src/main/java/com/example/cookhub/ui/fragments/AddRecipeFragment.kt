package com.example.cookhub.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookhub.models.Direction
import com.example.cookhub.models.Ingredient
import com.example.cookhub.models.Recipe
import com.example.cookhub.R
import com.example.cookhub.RecipeViewModel
import com.example.cookhub.adapters.DirectionAdapter
import com.example.cookhub.adapters.IngredientAdapter
import com.example.cookhub.databinding.AddRecipeLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddRecipeFragment: Fragment() {
    private var _binding: AddRecipeLayoutBinding? = null
    private val binding get() = _binding!!
    private var recipeId: Int? = null

    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var directionAdapter: DirectionAdapter

    private val ingredientsList = mutableListOf<Ingredient>()
    private val directionsList = mutableListOf<Direction>()
    private var existingIngredients: List<Ingredient> = emptyList()
    private var existingDirections: List<Direction> = emptyList()

    private val viewModel: RecipeViewModel by activityViewModels()

    val difficulties: List<String> = listOf("🟢 Easy","🟡 Medium", "🔴 Hard")

    private var imageUri: Uri? = null

    val pickImageLauncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.recipeImage.setImageURI(it)
            requireActivity().contentResolver.takePersistableUriPermission(it!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddRecipeLayoutBinding.inflate(inflater, container, false)
        recipeId = arguments?.getInt("recipeId")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner(binding.spinnerDifficulty, difficulties)

        //Define Adapters for Ingredients and Directions
        ingredientAdapter = IngredientAdapter(ingredientsList)
        binding.recyclerIngredients.adapter = ingredientAdapter
        binding.recyclerIngredients.layoutManager = LinearLayoutManager(requireContext())

        directionAdapter = DirectionAdapter(directionsList)
        binding.recyclerDirections.adapter = directionAdapter
        binding.recyclerDirections.layoutManager = LinearLayoutManager(requireContext())

        //In edit form check if data already exist and reload
        recipeId?.let { id ->
            CoroutineScope(Dispatchers.Main).launch {
                val recipeWithDetails = withContext(Dispatchers.IO) {
                    viewModel.getRecipeWithDetails(id)
                }
                recipeWithDetails?.let { details ->
                    binding.recipeTitle.setText(details.recipe.title)
                    binding.authorName.setText(details.recipe.author)

                    val difficultyIndex = difficulties.indexOf(details.recipe.difficulty)
                    if (difficultyIndex >= 0) {
                        binding.spinnerDifficulty.setSelection(difficultyIndex)
                    }

                    //reload time and remove emojis and "mins"
                    val timeStr = details.recipe.time?.replace("🕒 ", "")?.replace(" mins", "")
                    binding.prepTime.setText(timeStr)

                    //reload photo if exist
                    if (details.recipe.photo?.isNotEmpty() == true) {
                        imageUri = Uri.parse(details.recipe.photo)
                        binding.recipeImage.setImageURI(imageUri)
                    }

                    //reload Ingredients and Directions
                    existingIngredients = details.ingredients
                    existingDirections = details.directions
                    ingredientsList.clear()
                    ingredientsList.addAll(details.ingredients)
                    directionsList.clear()
                    directionsList.addAll(details.directions)
                    ingredientAdapter.notifyDataSetChanged()
                    directionAdapter.notifyDataSetChanged()

                    binding.finishBtn.text = getString(R.string.update_recipe)
                }
            }
        }


        binding.finishBtn.setOnClickListener {
            val title = binding.recipeTitle.text.toString()
            val author = binding.authorName.text.toString()
            val difficulty = binding.spinnerDifficulty.selectedItem.toString()
            val time = binding.prepTime.text.toString()
            val timeStr = "🕒 ${binding.prepTime.text.toString()} mins"
            val imageUriStr = imageUri.toString()

            val condition = title.isNotEmpty() &&
                    author.isNotEmpty() &&
                    difficulty.isNotEmpty() &&
                    time.isNotEmpty() &&
                    ingredientsList.isNotEmpty() &&
                    directionsList.isNotEmpty()

            if (condition){
                val recipe = Recipe(
                    title,
                    author,
                    difficulty,
                    timeStr,
                    imageUriStr).apply {
                    if (recipeId != null) id = recipeId!! // הגדרת ה-id עבור עדכון
                }

                if (recipeId == null) {
                    //saving new recipe
                    viewModel.saveFullRecipe(recipe, ingredientsList, directionsList)
                } else {
                    //updating recipe
                    ingredientsList.forEach { it.recipeId = recipeId!! }
                    directionsList.forEach { it.recipeId = recipeId!! }
                    viewModel.updateFullRecipe(recipe, ingredientsList, directionsList)
                }
                findNavController().navigate(R.id.action_addRecipeFragment2_to_dashboardFragment)
            }else{
                Toast.makeText(requireContext(),
                    getString(R.string.please_fill_all_the_fields),
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.addIngredient.setOnClickListener {
            val ingredientName = binding.ingredientsEdittext.text.toString()
            if (ingredientName.isNotEmpty()) {
                val newIngredient = Ingredient(
                    name = ingredientName,
                    recipeId = recipeId ?: 0)
                ingredientsList.add(newIngredient)
                ingredientAdapter.notifyItemInserted(ingredientsList.size - 1)
                binding.ingredientsEdittext.text.clear()
            }
        }

        binding.addDirection.setOnClickListener {
            val directionName = binding.directionEdittext.text.toString()
            if (directionName.isNotEmpty()){
                val newDirection = Direction(
                    description = directionName,
                    recipeId = recipeId ?: 0)
                directionsList.add(newDirection)
                directionAdapter.notifyItemInserted(directionsList.size-1)
                binding.directionEdittext.text.clear()
            }
        }

        binding.pickImageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        // Define Swipe-to-Delete for Ingredients and Direction
        attachSwipeToDelete(binding.recyclerDirections, directionsList) { pos ->
            directionsList.removeAt(pos)
        }

        attachSwipeToDelete(binding.recyclerIngredients, ingredientsList) { pos ->
            ingredientsList.removeAt(pos)
        }

    }

    private fun attachSwipeToDelete(
        recyclerView: RecyclerView,
        dataList: MutableList<*>,
        onItemRemoved: (Int) -> Unit
    ) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_SWIPE,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                onItemRemoved(pos)
                recyclerView.adapter?.notifyItemRemoved(pos)
                recyclerView.adapter?.notifyItemRangeChanged(pos, dataList.size - pos)
            }

        }).attachToRecyclerView(recyclerView)
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}