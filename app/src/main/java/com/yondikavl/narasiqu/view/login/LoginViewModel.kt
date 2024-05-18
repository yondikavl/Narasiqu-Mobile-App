package com.yondikavl.narasiqu.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yondikavl.narasiqu.data.UserRepository
import com.yondikavl.narasiqu.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}