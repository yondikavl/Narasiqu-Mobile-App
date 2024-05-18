package com.yondikavl.narasiqu.di

import android.content.Context
import com.yondikavl.narasiqu.data.UserRepository
import com.yondikavl.narasiqu.data.pref.UserPreference
import com.yondikavl.narasiqu.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}