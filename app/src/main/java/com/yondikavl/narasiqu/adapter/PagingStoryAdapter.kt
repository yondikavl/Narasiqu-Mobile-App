package com.yondikavl.narasiqu.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yondikavl.narasiqu.ui.DetailStoryActivity
import com.yondikavl.narasiqu.databinding.ItemStoryBinding
import com.yondikavl.narasiqu.data.remote.response.ListStoryItem
import com.squareup.picasso.Picasso

class PagingStoryAdapter : PagingDataAdapter<ListStoryItem, PagingStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        story?.let {
            holder.binding.apply {
                titleStory.text = it.name
                descStory.text = it.description
                Picasso.get().load(it.photoUrl).fit().into(storyPoto)

                root.setOnClickListener { view ->
                    val intent = Intent(view.context, DetailStoryActivity::class.java).apply {
                        putExtra("id", it.id)
                    }
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(view.context as Activity)
                    view.context.startActivity(intent, options.toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
