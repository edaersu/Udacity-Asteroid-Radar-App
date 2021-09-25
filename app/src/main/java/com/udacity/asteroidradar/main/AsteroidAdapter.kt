package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ItemAsteroidBinding

class AsteroidAdapter(val click: AsteroidClick) :
    ListAdapter<Asteroid, AsteroidAdapter.ViewHolder>(ViewHolder.ShoeDiffCallback()) {

    //does actual view binding to holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asteroidItem = getItem(position)
        holder.itemView.setOnClickListener { //handle click events
            click.onClick(asteroidItem)
        }
        holder.bind(asteroidItem)
    }

    //create holder for views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /*Holds views to be bound to recycler view*/
    class ViewHolder private constructor(val binding: ItemAsteroidBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Asteroid) {
            binding.asteroid = item
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAsteroidBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        /* Monitor changes to recycler view content */
        class ShoeDiffCallback : DiffUtil.ItemCallback<Asteroid>() {

            override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
                return oldItem == newItem
            }
        }
    }
}

/*OnClickListener class*/
class AsteroidClick(val block: (Asteroid) -> Unit) {
    //called for onClick event on Asteroids
    fun onClick(asteroid: Asteroid) = block(asteroid)
}