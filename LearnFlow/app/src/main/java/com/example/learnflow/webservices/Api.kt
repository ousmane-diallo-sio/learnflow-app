package com.example.learnflow.webservices

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.learnflow.BuildConfig
import com.example.learnflow.HomeActivity
import com.example.learnflow.MainActivity
import com.example.learnflow.model.User
import com.example.learnflow.model.UserType
import com.example.learnflow.utils.EnvUtils
import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.logging.Handler

object Api {
    private val client = OkHttpClient()
    private val gson = Gson()

    var currentUser: User? = null
    var userType: UserType? = null

    fun login(context: Context, email: String, password: String) {
        ContextCompat.startActivity(context, Intent(context, HomeActivity::class.java), null)
        Log.d("Components", "${EnvUtils.API_BASE_URL}${Endpoints.loginStudent}")
    }

    @Throws (Exception::class)
    fun register(user: User, callback: ((Response) -> Unit)?) {
        if (userType == null) {
            throw Exception("User type is required")
        }

        currentUser = user
        val formBody = gson.toJson(user).toRequestBody("application/json".toMediaType())
        Log.w("Components", "formBody : $formBody")

        val request = Request.Builder()
            .url("${EnvUtils.API_BASE_URL}/register/${userType}")
            .post(formBody)
            .build()

        try {
            CoroutineScope(Dispatchers.IO).launch {
                client.newCall(request).execute().use { response ->
                    callback?.invoke(response)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun disconnect(context: Context) {
        currentUser = null
        ContextCompat.startActivity(context, Intent(context, MainActivity::class.java), null)
    }

}