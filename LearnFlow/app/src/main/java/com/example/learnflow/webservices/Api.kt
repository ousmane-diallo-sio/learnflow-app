package com.example.learnflow.webservices

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.learnflow.BuildConfig
import com.example.learnflow.HomeActivity
import com.example.learnflow.MainActivity
import com.example.learnflow.model.User
import com.example.learnflow.model.UserType
import com.example.learnflow.utils.EnvUtils
import com.example.learnflow.webservices.Api.currentUser
import com.example.learnflow.webservices.Api.handleError
import com.example.learnflow.webservices.Api.handleMissingNetwork
import com.example.learnflow.webservices.Api.isNetworkConnected
import com.example.learnflow.webservices.Api.userType
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
import java.net.ConnectException
import java.util.logging.Handler

object Api {
    private val client = OkHttpClient()
    private val gson = Gson()

    var currentUser: User? = null
    var userType: UserType? = null

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun handleMissingNetwork(context: Context) {
        if (!isNetworkConnected(context)) {
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Vous n'êtes pas connecté à internet")
                .setMessage("Veuillez vous connecter à internet pour continuer")
                .setPositiveButton("Me connecter") { _, _ ->
                    context.startActivity(Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS))
                }
                .setCancelable(false)
                .create()

            alertDialog.show()
        }
    }

    fun handleError(context: Context, e: java.lang.Exception, methodName: String) {
        Log.e("Components", "Api::${methodName} -> ${e.message ?: ""}")
        (context as Activity).runOnUiThread {
            when (e) {
                is ConnectException -> Toast.makeText(context, "Erreur lors de la connexion au serveur", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(context: Context, email: String, password: String) {
        if (!isNetworkConnected(context)) {
            handleMissingNetwork(context)
            return
        }

        ContextCompat.startActivity(context, Intent(context, HomeActivity::class.java), null)
        Log.d("Components", Endpoints.loginStudent)
    }

    @Throws (Exception::class)
    fun register(context: Context, user: User, callback: ((Response) -> Unit)?) {
        if (userType == null) throw Exception("User type is required")
        if (!isNetworkConnected(context)) {
            handleMissingNetwork(context)
            return
        }

        currentUser = user
        val formBody = gson.toJson(user).toRequestBody("application/json".toMediaType())
        Log.w("Components", "formBody : $formBody")

        val request = Request.Builder()
            .url(
                if (userType == UserType.STUDENT) Endpoints.registerStudent
                else Endpoints.registerTeacher
            )
            .post(formBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.newCall(request).execute().use { response ->
                    callback?.invoke(response)
                }
            } catch (e: Exception) { handleError(context, e, "register") }
        }
    }

    fun disconnect(context: Context) {
        currentUser = null
        ContextCompat.startActivity(context, Intent(context, MainActivity::class.java), null)
    }

}