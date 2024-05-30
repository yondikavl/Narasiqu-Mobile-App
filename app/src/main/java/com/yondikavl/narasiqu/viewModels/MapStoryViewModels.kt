package com.yondikavl.narasiqu.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.yondikavl.narasiqu.data.repository.StoryRepository
import com.yondikavl.narasiqu.data.remote.response.ListStoryItem

class MapStoryViewModels(private val repo: StoryRepository): ViewModel() {
    fun getStory(): LiveData<List<ListStoryItem>> {
        return repo.getStory().asFlow().asLiveData()
    }
}