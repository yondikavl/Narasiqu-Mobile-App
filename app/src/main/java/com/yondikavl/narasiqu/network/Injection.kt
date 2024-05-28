package com.yondikavl.narasiqu.network

import android.content.Context
import com.yondikavl.narasiqu.data.local.UserPreference
import com.yondikavl.narasiqu.data.local.dataStore
import com.yondikavl.narasiqu.data.repository.StoryRepository

object Injection {
    fun provideRepo(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = BaseApi().getApiService(pref)
        return StoryRepository.getInstance(pref, apiService)
    }
}