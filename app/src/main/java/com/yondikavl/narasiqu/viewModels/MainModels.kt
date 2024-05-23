package com.yondikavl.narasiqu.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yondikavl.narasiqu.data.StoryRepository
import com.yondikavl.narasiqu.models.ListStoryItem
import com.yondikavl.narasiqu.models.UserModel
import kotlinx.coroutines.launch

class MainModels(private val repo: StoryRepository): ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }
    fun getAllStory(): LiveData<PagingData<ListStoryItem>> {
        return repo.getAllStory().cachedIn(viewModelScope)
    }
}