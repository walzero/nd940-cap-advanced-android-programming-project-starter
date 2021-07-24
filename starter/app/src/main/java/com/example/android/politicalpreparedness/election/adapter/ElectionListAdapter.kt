package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ListItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) :
    ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    //TODO: Add companion object to inflate ViewHolder (from)
}

//TODO: Create ElectionViewHolder
class ElectionViewHolder(
    val binding: ListItemElectionBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        election: Election,
        clickListener: ElectionListener
    ) {

    }

    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val binding = ListItemElectionBinding
                .inflate(LayoutInflater.from(parent.context))

            return ElectionViewHolder(binding)
        }
    }
}

//TODO: Create ElectionDiffCallback
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }

}

//TODO: Create ElectionListener
fun interface ElectionListener {
    fun onClick(election: Election)
}