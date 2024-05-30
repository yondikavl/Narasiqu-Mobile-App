package com.yondikavl.narasiqu.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yondikavl.narasiqu.data.repository.StoryRepository
import com.yondikavl.narasiqu.data.remote.response.ListStoryItem
import com.yondikavl.narasiqu.data.model.UserModel
import kotlinx.coroutines.launch

class MainViewModels(private val repo: StoryRepository): ViewModel() {

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