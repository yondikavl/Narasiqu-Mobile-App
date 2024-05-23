package com.yondikavl.narasiqu.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yondikavl.narasiqu.data.Injection
import com.yondikavl.narasiqu.data.StoryRepository

class ViewModelsFactory(private val repo: StoryRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModels::class.java) -> {
                LoginViewModels(repo) as T
            }
            modelClass.isAssignableFrom(MainModels::class.java) -> {
                MainModels(repo) as T
            }
            modelClass.isAssignableFrom(DetailStoryModels::class.java) -> {
                DetailStoryModels(repo) as T
            }
            modelClass.isAssignableFrom(AddStoryModels::class.java) -> {
                AddStoryModels(repo) as T
            }
            modelClass.isAssignableFrom(MapStoryModels::class.java) -> {
                MapStoryModels(repo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelsFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelsFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelsFactory::class.java) {
                    INSTANCE = ViewModelsFactory(Injection.provideRepo(context))
                }
            }
            return INSTANCE as ViewModelsFactory
        }
    }
}