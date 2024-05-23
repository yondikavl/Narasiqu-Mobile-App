package com.yondikavl.narasiqu.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.yondikavl.narasiqu.data.StoryRepository
import com.yondikavl.narasiqu.models.ListStoryItem

class MapStoryModels(private val repo: StoryRepository): ViewModel() {
    fun getStory(): LiveData<List<ListStoryItem>> {
        return repo.getStory().asFlow().asLiveData()
    }
}