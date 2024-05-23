package com.yondikavl.narasiqu.data

import android.content.Context
import com.yondikavl.narasiqu.api.BaseApi

object Injection {
    fun provideRepo(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = BaseApi().getApiService(pref)
        return StoryRepository.getInstance(pref, apiService)
    }
}