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
import com.example.learnflow.utils.EnvUtils
import io.github.cdimascio.dotenv.dotenv

object Api {
    var currentUser: User? = null

    fun login(context: Context, email: String, password: String) {
        ContextCompat.startActivity(context, Intent(context, HomeActivity::class.java), null)
        Log.d("Components", "${EnvUtils.API_BASE_URL}${Endpoints.loginStudent}")
    }

    fun register(user: User) {
        TODO("Register not implemented yet")
    }

    fun disconnect(context: Context) {
        currentUser = null
        ContextCompat.startActivity(context, Intent(context, MainActivity::class.java), null)
    }

}