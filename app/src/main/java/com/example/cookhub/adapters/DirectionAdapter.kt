package com.example.cookhub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cookhub.models.Direction
import com.example.cookhub.databinding.DirectionCardLayoutBinding

class DirectionAdapter(var directions: List<Direction>): RecyclerView.Adapter<DirectionAdapter.DirectionViewHolder>() {

    class DirectionViewHolder(private val binding: DirectionCardLayoutBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(direction: Direction, position: Int){
            binding.nameTextView.text = direction.description
            binding.numberTextView.text = "${position + 1}."
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DirectionViewHolder = DirectionViewHolder(
        DirectionCardLayoutBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))

    override fun onBindViewHolder(holder: DirectionAdapter.DirectionViewHolder, position: Int) =
        holder.bind(directions[position], position)

    override fun getItemCount(): Int = directions.size

    fun updateDirections(newDirections: List<Direction>) {
        directions = newDirections
        notifyDataSetChanged()
    }
}