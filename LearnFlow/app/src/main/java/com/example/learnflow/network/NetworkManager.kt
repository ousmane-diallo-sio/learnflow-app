package com.example.learnflow.network

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.provider.Settings
import android.util.Log
import android.widget.Toast
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

    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun handleMissingNetwork(context: Context): Boolean {
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
            return true
        }
        return false
    }

    fun observeNetworkConnectivity(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Toast.makeText(context, "Connecté à internet", Toast.LENGTH_SHORT).show()
            }

            override fun onLost(network: Network) {
                handleMissingNetwork(context)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun loginAsync(context: Context, requestBody: UserLoginRequest): Deferred<ServerResponse<User>>? {
        if (handleMissingNetwork(context)) return null

        Log.d("NetworkManager", "loginAsync: $requestBody")
        return api.loginAsync(requestBody)
    }

    fun registerStudentAsync(context: Context, requestBody: StudentRegisterRequest): Deferred<ServerResponse<User>>? {
        if (handleMissingNetwork(context)) return null

        Log.d("NetworkManager", "registerStudentAsync: $requestBody")
        return api.registerStudentAsync(requestBody)
    }
}