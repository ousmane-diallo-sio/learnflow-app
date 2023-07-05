package com.example.learnflow.network

import android.util.Log
import com.example.learnflow.model.Student
import com.example.learnflow.model.User
import com.example.learnflow.utils.EnvUtils
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private val api = Retrofit.Builder()
        .baseUrl(EnvUtils.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(NetworkI::class.java)

    fun registerStudentAsync(requestBody: StudentRegistrationRequest): Deferred<ServerResponse<User>> {
        Log.d("NetworkManager", "registerStudentAsync: $requestBody")
        return api.registerStudentAsync(requestBody)
    }
}