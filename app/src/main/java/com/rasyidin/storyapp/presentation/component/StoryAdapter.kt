package com.rasyidin.storyapp.presentation.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rasyidin.storyapp.data.model.Story
import com.rasyidin.storyapp.databinding.ItemStoryBinding

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(diffCallback) {

    var onItemClick: ((Story, View, View, View, View) -> Unit)? = null

    class StoryViewHolder(val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.apply {
                with(story) {
                    tvName.text = name
                    tvDescription.text = description
                    tvComment.text = (0..2000).random().toString().toShortNumber()
                    tvLike.text = (0..2000).random().toString().toShortNumber()
                    tvDate.text = createdAt?.withDateFormat()
                    tvLocation.text = "${lat ?: 0},${lng ?: 0}"
                    Glide.with(root)
                        .load(photoUrl)
                        .into(imgStory)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let {
            holder.bind(story)
            holder.binding.apply {
                root.setOnClickListener {
                    onItemClick?.invoke(story, imgStory, tvName, tvDescription, tvDate)
                }
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}