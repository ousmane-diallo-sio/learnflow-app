package com.example.learnflow

import android.app.Activity
import android.content.Context
import android.net.Network
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnflow.model.User
import com.example.learnflow.network.NetworkManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class HomeViewModel : ViewModel() {

    private val TAG = "HomeViewModel"

    fun getMe(context: Activity, callback: (User?) -> Unit) {
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
                res?.let { serverResponse -> serverResponse.data?.let { callback(it) } }
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

}