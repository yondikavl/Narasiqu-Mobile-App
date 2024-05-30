package com.yondikavl.narasiqu.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yondikavl.narasiqu.network.Injection
import com.yondikavl.narasiqu.data.repository.StoryRepository

class ViewModelsFactory(private val repo: StoryRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModels::class.java) -> {
                LoginViewModels(repo) as T
            }
            modelClass.isAssignableFrom(MainViewModels::class.java) -> {
                MainViewModels(repo) as T
            }
            modelClass.isAssignableFrom(DetailStoryViewModels::class.java) -> {
                DetailStoryViewModels(repo) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModels::class.java) -> {
                AddStoryViewModels(repo) as T
            }
            modelClass.isAssignableFrom(MapStoryViewModels::class.java) -> {
                MapStoryViewModels(repo) as T
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