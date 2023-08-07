package com.example.learnflow.network

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.provider.Settings
import android.widget.Toast
import com.example.learnflow.model.LocalDateTypeAdapter
import com.example.learnflow.model.SchoolSubject
import com.example.learnflow.model.User
import com.example.learnflow.model.UserType
import com.example.learnflow.utils.EnvUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object NetworkManager {
    var userType: UserType? = null

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTypeAdapter())
        .create()


    private val api = Retrofit.Builder()
        .baseUrl(EnvUtils.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
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
            override fun onAvailable(network: Network) {}

            override fun onLost(network: Network) {
                handleMissingNetwork(context)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun parseHttpException(httpException: HttpException): ServerResponse<*>? {
        val responseBody = httpException.response()?.errorBody()?.string()
        val serverResponse: ServerResponse<*>? = responseBody?.let {
            val gson = Gson()
            gson.fromJson(it, ServerResponse::class.java)
        }
        return serverResponse
    }

    fun formatDateFRToISOString(dateString: String): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDate = LocalDate.parse(dateString, dateFormatter)

        return localDate.atStartOfDay().toString()
    }

    fun loginAsync(context: Context, requestBody: UserLoginDTO): Deferred<ServerResponse<User>>? {
        if (handleMissingNetwork(context)) return null
        return api.loginAsync(requestBody)
    }

    fun registerStudentAsync(context: Context, requestBody: StudentSignupDTO): Deferred<ServerResponse<User>>? {
        if (handleMissingNetwork(context)) return null
        return api.registerStudentAsync(requestBody)
    }

    fun registerTeacherAsync(context: Context, requestBody: TeacherSignupDTO): Deferred<ServerResponse<User>>? {
        if (handleMissingNetwork(context)) return null
        return api.registerTeacherAsync(requestBody)
    }

    fun getTeachersAsync(context: Context, query: String): Deferred<ServerResponse<List<User>>>? {
        if (handleMissingNetwork(context)) return null
        return api.getTeachersAsync(query)
    }

    fun getStudentsAsync(context: Context, query: String): Deferred<ServerResponse<List<User>>>? {
        if (handleMissingNetwork(context)) return null
        return api.getStudentsAsync(query)
    }

    fun getSchoolSubjectsAsync(context: Context): Deferred<ServerResponse<List<SchoolSubject>>>? {
        if (handleMissingNetwork(context)) return null
        return api.getSchoolSubjectsAsync()
    }
}