package com.yondikavl.narasiqu.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.yondikavl.narasiqu.data.repository.StoryRepository
import com.yondikavl.narasiqu.data.remote.response.UploadStoryResponse
import com.yondikavl.narasiqu.data.model.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModels(private val repo: StoryRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }

    suspend fun postStory(desc: RequestBody, photo: MultipartBody.Part): LiveData<UploadStoryResponse>{
        return repo.postStory(desc, photo).asFlow().asLiveData()
    }
}