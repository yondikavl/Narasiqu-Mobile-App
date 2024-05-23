package com.yondikavl.narasiqu.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.yondikavl.narasiqu.data.StoryRepository
import com.yondikavl.narasiqu.models.RequestLogin
import com.yondikavl.narasiqu.models.ResponseLogin
import com.yondikavl.narasiqu.models.UserModel

class LoginViewModels(private val repo: StoryRepository): ViewModel() {

    fun getSession(): LiveData<UserModel>{
        return repo.getSession().asLiveData()
    }
    suspend fun postLogin(reqLogin: RequestLogin): LiveData<ResponseLogin> {
        return repo.postLogin(reqLogin).asFlow().asLiveData()
    }
}