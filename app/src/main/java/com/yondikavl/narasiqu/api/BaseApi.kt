package com.yondikavl.narasiqu.api

import com.yondikavl.narasiqu.data.UserPreference
import com.yondikavl.narasiqu.models.RequestLogin
import com.yondikavl.narasiqu.models.RequestRegister
import com.yondikavl.narasiqu.models.ResponseDetailStory
import com.yondikavl.narasiqu.models.ResponseListStory
import com.yondikavl.narasiqu.models.ResponseLogin
import com.yondikavl.narasiqu.models.ResponseRegister
import com.yondikavl.narasiqu.models.ResponseUploadStory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

class BaseApi {

    private val urlBase: String = "https://story-api.dicoding.dev/v1/"

    fun getApiService(pref: UserPreference): urlData {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = Interceptor {
            val req = it.request()
            val user = runBlocking {
                pref.getSession().first()
            }
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer ${user.token}")
                .build()
            it.proceed(requestHeaders)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(urlData::class.java)
    }
}

interface urlData {

    @POST("register")
    fun postRegister(@Body reqRegister: RequestRegister): Call<ResponseRegister>

    @POST("login")
    suspend fun postLogin(@Body reqLogin: RequestLogin): Response<ResponseLogin>

    @GET("stories")
    suspend fun getAllStory(
        @Query("page") page: Int,
        @Query("location") location: Int = 0,
        @Query("size") size: Int = 10
    ): ResponseListStory

    @GET("stories")
    fun getStory(
        @Query("location") location: Int = 1,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Call<ResponseListStory>

    @GET("stories/{id}")
    fun getDetailStory(
        @Path("id") id: String
    ): Call<ResponseDetailStory>

    @POST("stories")
    @Multipart
    suspend fun postStory(
        @Part("description") desc: RequestBody,
        @Part poto: MultipartBody.Part
    ): Response<ResponseUploadStory>
}