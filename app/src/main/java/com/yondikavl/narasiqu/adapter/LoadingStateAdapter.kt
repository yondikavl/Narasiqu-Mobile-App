package com.yondikavl.narasiqu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yondikavl.narasiqu.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val bind = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(bind, retry)
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.binding(loadState)
    }

    class LoadingViewHolder(private val bind: ItemLoadingBinding, retry: () -> Unit):
        RecyclerView.ViewHolder(bind.root) {
        init {
            bind.retryButton.setOnClickListener { retry.invoke() }
        }

        fun binding(loadState: LoadState) {
            if (loadState is LoadState.Error){
                bind.errorMsg.text = loadState.error.localizedMessage
            }
            bind.progressBar.isVisible = loadState is LoadState.Loading
            bind.retryButton.isVisible = loadState is LoadState.Error
            bind.errorMsg.isVisible = loadState is LoadState.Error
        }
    }




}