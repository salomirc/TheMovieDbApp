package com.belsoft.themoviedbapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.belsoft.themoviedbapp.databinding.SearchSelectItemBinding
import com.belsoft.themoviedbapp.models.SearchSelectItemModel

class SearchListAdapter(
    private val onItemClick: (item: SearchSelectItemModel) -> Unit
) : ListAdapter<SearchSelectItemModel, SearchListAdapter.SearchListViewHolder>(SearchListDiffCallback) {

    private var items: List<SearchSelectItemModel> = listOf()

    inner class SearchListViewHolder(
        val binding: SearchSelectItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(items[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val binding = SearchSelectItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.binding.itemModel = currentItem
    }

    override fun submitList(list: List<SearchSelectItemModel>?) {
        list?.let {
            items = it
        }
        super.submitList(list)
    }
}

object SearchListDiffCallback : DiffUtil.ItemCallback<SearchSelectItemModel>() {
    override fun areItemsTheSame(oldItem: SearchSelectItemModel, newItem: SearchSelectItemModel): Boolean {
        return oldItem.id== newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchSelectItemModel, newItem: SearchSelectItemModel): Boolean {
        return oldItem == newItem
    }
}