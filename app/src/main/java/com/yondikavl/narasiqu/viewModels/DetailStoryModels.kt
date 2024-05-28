package com.yondikavl.narasiqu.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yondikavl.narasiqu.data.repository.StoryRepository
import com.yondikavl.narasiqu.data.remote.response.Story

class DetailStoryModels(private val repo: StoryRepository): ViewModel() {

    suspend fun getDetailStory(id: String): LiveData<Story> {
        return repo.getDetailStory(id).asLiveData()
    }
}