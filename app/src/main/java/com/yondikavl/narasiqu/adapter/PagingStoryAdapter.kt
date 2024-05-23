package com.yondikavl.narasiqu.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yondikavl.narasiqu.DetailStoryActivity
import com.yondikavl.narasiqu.databinding.ListLayoutBinding
import com.yondikavl.narasiqu.models.ListStoryItem
import com.squareup.picasso.Picasso

class PagingStoryAdapter : PagingDataAdapter<ListStoryItem, PagingStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(val bind: ListLayoutBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)!!
        holder.bind.apply {
            judulStory.text = story.name
            judulStory.text = story.name
            textStory.text = story.description
            Picasso.get().load(story.photoUrl).fit().into(storyPoto)
        }

        holder.itemView.setOnClickListener {
            val i = Intent(it.context, DetailStoryActivity::class.java)
            i.putExtra("id", story.id)
            it.context.startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(it.context as Activity).toBundle())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bind = ListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(bind)
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