package com.yondikavl.narasiqu.network

import com.yondikavl.narasiqu.data.local.UserPreference
import com.yondikavl.narasiqu.data.remote.request.LoginRequest
import com.yondikavl.narasiqu.data.remote.request.RegisterRequest
import com.yondikavl.narasiqu.data.remote.response.ResponseDetailStory
import com.yondikavl.narasiqu.data.remote.response.ResponseListStory
import com.yondikavl.narasiqu.data.remote.response.ResponseLogin
import com.yondikavl.narasiqu.data.remote.response.RegisterResponse
import com.yondikavl.narasiqu.data.remote.response.UploadStoryResponse
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

    private val urlBaseApi: String = "https://story-api.dicoding.dev/v1/"

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
            .baseUrl(urlBaseApi)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(urlData::class.java)
    }
}

interface urlData {

    @POST("register")
    fun postRegister(@Body reqRegister: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    suspend fun postLogin(@Body reqLogin: LoginRequest): Response<ResponseLogin>

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
    ): Response<UploadStoryResponse>
}