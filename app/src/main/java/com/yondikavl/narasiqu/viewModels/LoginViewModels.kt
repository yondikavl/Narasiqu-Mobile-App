package com.yondikavl.narasiqu.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.yondikavl.narasiqu.data.repository.StoryRepository
import com.yondikavl.narasiqu.data.remote.request.LoginRequest
import com.yondikavl.narasiqu.data.remote.response.ResponseLogin
import com.yondikavl.narasiqu.data.model.UserModel

class LoginViewModels(private val repo: StoryRepository): ViewModel() {

    fun getSession(): LiveData<UserModel>{
        return repo.getSession().asLiveData()
    }
    suspend fun postLogin(requestLogin: LoginRequest): LiveData<ResponseLogin> {
        return repo.postLogin(requestLogin).asFlow().asLiveData()
    }
}