package com.rasyidin.storyapp.presentation.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rasyidin.storyapp.data.model.Setting
import com.rasyidin.storyapp.databinding.ItemSettingBinding

class SettingAdapter : ListAdapter<Setting, SettingAdapter.ItemViewHolder>(diffCallback) {

    var onItemClick: ((Setting) -> Unit)? = null

    class ItemViewHolder(val binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(setting: Setting) {
            val context = itemView.context
            binding.apply {
                with(setting) {
                    Glide.with(context)
                        .load(ContextCompat.getDrawable(
                            context,
                            image
                        ))
                        .into(imgLanguage)

                    labelLanguage.text = context.getString(title)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingAdapter.ItemViewHolder {
        val binding = ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val setting = getItem(position)
        holder.bind(setting)
        holder.binding.apply {
            root.setOnClickListener {
                onItemClick?.invoke(setting)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Setting>() {
            override fun areItemsTheSame(oldItem: Setting, newItem: Setting): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Setting, newItem: Setting): Boolean {
                return oldItem == newItem
            }
        }
    }
}