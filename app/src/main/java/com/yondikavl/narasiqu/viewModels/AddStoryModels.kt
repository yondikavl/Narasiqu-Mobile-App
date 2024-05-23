package com.yondikavl.narasiqu.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.yondikavl.narasiqu.data.StoryRepository
import com.yondikavl.narasiqu.models.ResponseUploadStory
import com.yondikavl.narasiqu.models.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryModels(private val repo: StoryRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }

    suspend fun postStory(desc: RequestBody, poto: MultipartBody.Part): LiveData<ResponseUploadStory>{
        return repo.postStory(desc, poto).asFlow().asLiveData()
    }
}