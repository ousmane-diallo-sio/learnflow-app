package com.example.learnflow

import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.net.Network
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnflow.model.User
import com.example.learnflow.network.NetworkManager
import com.example.learnflow.utils.EnvUtils
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class HomeViewModel : ViewModel() {

    private val TAG = "HomeViewModel"
    val userFlow = MutableStateFlow<User?>(null)
    val schoolLevels = listOf(
        "CP",
        "CE1",
        "CE2",
        "CM1",
        "CM2",
        "6ème",
        "5ème",
        "4ème",
        "3ème",
        "2nde",
        "1ère",
        "Terminale",
        "Bac +1",
        "Bac +2",
        "Bac +3",
        "Bac +4",
        "Bac +5"
    )
    val schoolSubjectsFlow = MutableStateFlow(
        listOf(
            "Maths",
            "Anglais",
            "Français",
            "Physique-chimie",
            "Histoire",
            "Géographie",
            "Art",
            "Informatique",
            "Economie",
            "Philosophie",
            "Biologie"
        )
    )
    lateinit var placesClient: PlacesClient

    fun initPlacesClient(context: Context) {
        Log.d(TAG, "initPlacesClient: ${EnvUtils.MAPS_API_KEY}")
        Places.initialize(context, EnvUtils.MAPS_API_KEY)
        placesClient = Places.createClient(context)
    }

    fun getMe(context: Activity, callback: (User?) -> Unit = {}) {
        fun handleRequestFailure() {
            Snackbar.make(
                context.findViewById(android.R.id.content),
                "Erreur lors de la récupération de vos informations",
                Snackbar.LENGTH_LONG
            )
                .setAction(context.getString(R.string.retry)) { getMe(context, callback) }
                .show()
        }

        viewModelScope.launch {
            try {
                val res = NetworkManager.getMeAsync(context)?.await()
                res?.let { serverResponse ->
                    serverResponse.data?.let {
                        userFlow.emit(it)
                        callback(it)
                    }
                }
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)
                Log.e(TAG, "getMe: ${serverResponse?.error}")
                handleRequestFailure()
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "getMe: ${e.message}")
                handleRequestFailure()
            } catch (e: Exception) {
                Log.e(TAG, "getMe: ${e.message}")
                handleRequestFailure()
            }
        }
    }

    fun updateUser(context: Activity, user: User) {
        val oldUser = userFlow.value
        if (user == oldUser) return

        val onSuccess: suspend (User) -> Unit = { data: User ->
            val snackbar = Snackbar.make(
                context.findViewById(android.R.id.content),
                "Vos informations ont bien été mises à jour",
                Snackbar.LENGTH_SHORT
            )
            snackbar.setAction("OK") { snackbar.dismiss() }
            snackbar.show()
            userFlow.emit(data)
        }

        val onFailure: suspend (String?) -> Unit = { errorMsg: String? ->
            Snackbar.make(
                context.findViewById(android.R.id.content),
                errorMsg ?: "Erreur lors de la mise à jour de vos informations",
                Snackbar.LENGTH_LONG
            )
                .show()
            Log.d(TAG, "updateUser: ${oldUser?.phoneNumber}")
            userFlow.emit(oldUser)
        }

        viewModelScope.launch {
            try {
                userFlow.emit(user)
                val res = NetworkManager.updateUserAsync(context, user)?.await()

                res?.let { serverResponse ->
                    serverResponse.data?.let { data ->
                        userFlow.emit(data)
                        onSuccess(data)
                        return@launch
                    }
                    onFailure(null)
                }
                Log.e("HomeViewModel", "API response data is null !!")
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)
                Log.e("HomeViewModel", "updateUser: ${serverResponse?.error}")
                onFailure(serverResponse?.error)
            } catch (e: SocketTimeoutException) {
                Log.e("HomeViewModel", "Connection timed out: $e")
                onFailure("La connexion au serveur a échouée")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Update user: ${e.message}")
                onFailure(null)
            }
        }
    }

    fun logout(context: Activity, callback: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkManager.logoutAsync(context)?.await()
                res?.let {
                    callback()
                }
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)
                Log.e(TAG, "logout: ${serverResponse?.error}")
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "logout: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "logout: ${e.message}")
            }
        }
    }
}