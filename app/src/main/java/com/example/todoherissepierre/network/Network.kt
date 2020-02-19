package com.example.todoherissepierre.network

import android.os.Parcelable
import com.example.todoherissepierre.TasksWebService
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import kotlinx.android.parcel.Parcelize
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.io.Serializable

object Api {
    private const val BASE_URL = "https://android-tasks-api.herokuapp.com/api/"
    private const val TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoyMzEsImV4cCI6MTYxMzY0NTgyMH0.dHLxY-lFQy91_Or8fIacqVYejztV1y8FYX77bqkAoOA"
    private val moshi = Moshi.Builder().build()

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $TOKEN")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val userService: UserService by lazy { retrofit.create(UserService::class.java) }
    val tasksWebService: TasksWebService by lazy { retrofit.create(TasksWebService::class.java) }
}

interface UserService {
    @GET("users/info")
    suspend fun getInfo(): Response<UserInfo>
}

data class UserInfo(
    @field:Json(name = "email")
    val email: String,
    @field:Json(name = "firstname")
    val firstName: String,
    @field:Json(name = "lastname")
    val lastName: String
)

@Parcelize
data class TaskInfos(
    @field:Json(name = "id")
    var id: String,
    @field:Json(name = "title")
    var title: String,
    @field:Json(name = "description")
    var description: String
): Serializable, Parcelable