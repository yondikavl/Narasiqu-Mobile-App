package com.yondikavl.narasiqu.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.yondikavl.narasiqu.api.urlData
import com.yondikavl.narasiqu.models.ListStoryItem
import com.yondikavl.narasiqu.models.RequestLogin
import com.yondikavl.narasiqu.models.ResponseDetailStory
import com.yondikavl.narasiqu.models.ResponseListStory
import com.yondikavl.narasiqu.models.ResponseLogin
import com.yondikavl.narasiqu.models.ResponseUploadStory
import com.yondikavl.narasiqu.models.Story
import com.yondikavl.narasiqu.models.UserModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor (
    private val userPreference: UserPreference,
    private val apiService: urlData
) {

    private suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun postLogin(reqLogin: RequestLogin): LiveData<ResponseLogin> {
        val data = MutableLiveData<ResponseLogin>()
        try {
            val response = apiService.postLogin(reqLogin)
            if (response.isSuccessful){
                data.postValue(response.body())
                response.body().let {
                    val token = it?.loginResult?.token
                    val name = it?.loginResult?.name
                    this.saveSession(UserModel(name!!, token!!))
                }
            }
        } catch (e: Exception){
            e.message
        }
        return data
    }

    suspend fun getDetailStory(id: String): Flow<Story> = callbackFlow {
        val call = apiService.getDetailStory(id)
        call.enqueue(object : Callback<ResponseDetailStory> {
            override fun onResponse(
                call: Call<ResponseDetailStory>,
                response: Response<ResponseDetailStory>
            ) {
                if (response.isSuccessful) {
                    val story = response.body()?.story
                    if (story != null){
                        trySend(story)
                    } else {
                        close(Exception("Data Tidak Ada..."))
                    }
                } else {
                    close(Exception("Unsuccessful response: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ResponseDetailStory>, t: Throwable) {
                close(t)
            }
        })

        awaitClose {
            call.cancel()
        }
    }

    fun getStory(): LiveData<List<ListStoryItem>> {
        val data = MutableLiveData<List<ListStoryItem>>()
        val call = apiService.getStory()

        call.enqueue(object : Callback<ResponseListStory> {
            override fun onResponse(
                call: Call<ResponseListStory>,
                response: Response<ResponseListStory>
            ) {
                if (response.isSuccessful){
                    data.value = response.body()!!.listStory ?: emptyList()
                }
            }

            override fun onFailure(call: Call<ResponseListStory>, t: Throwable) {
                t.message
            }

        })
        return data
    }

    suspend fun postStory(desc: RequestBody, poto: MultipartBody.Part): LiveData<ResponseUploadStory> {
        val data = MutableLiveData<ResponseUploadStory>()

        try {
            val response = apiService.postStory(desc, poto)
            if (response.isSuccessful){
                data.postValue(response.body())
            }
        } catch (e: Exception){
            e.message
        }
        return data
    }

    fun getAllStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PagingStory(apiService) },
            initialKey = 1
        ).liveData
    }


    companion object {
        private const val PAGE_SIZE = 10

        @Volatile
        var instance: StoryRepository? = null
        fun getInstance(
            userPref: UserPreference, apiService: urlData
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(userPref, apiService)
            }.also { instance = it }
    }
}