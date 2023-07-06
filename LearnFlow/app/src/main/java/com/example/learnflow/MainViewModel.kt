package com.example.learnflow

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnflow.model.User
import com.example.learnflow.network.NetworkManager
import com.example.learnflow.network.StudentRegisterRequest
import com.example.learnflow.network.UserLoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val userFlow = MutableStateFlow<User?>(null)
    private val studentRegisterRequestFlow = MutableStateFlow<StudentRegisterRequest?>(null)
    // profilePictureUrl = "https://d38b044pevnwc9.cloudfront.net/cutout-nuxt/enhancer/2.jpg"

    fun onStart(mainActivity: MainActivity) {
        NetworkManager.observeNetworkConnectivity(mainActivity)
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userFlow.emit(user)
        }
    }

    fun updateStudentRegisterRequest(studentRegisterRequest: StudentRegisterRequest) {
        viewModelScope.launch {
            studentRegisterRequestFlow.emit(studentRegisterRequest)
        }
    }

    fun login(context: Context, userLoginRequest: UserLoginRequest, callback: (error: String?) -> Unit) {
        viewModelScope.launch {
            try {
                NetworkManager.loginAsync(context, userLoginRequest)?.await().let {
                    if (it == null) return@launch

                    if (it.error != null) {
                        Log.e("MainViewModel", "Failed to login: ${it.error}")
                        callback(it.error)
                        return@launch
                    }
                    if (it.status == 200 && it.data != null) {
                        val jwtToken = it.jwt
                        if (jwtToken != null) {
                            saveJwtToken(context.getSharedPreferences("jwtToken", Context.MODE_PRIVATE), jwtToken)
                        }
                        updateUser(it.data)
                        callback(null)
                    } else {
                        Log.e("MainViewModel", "Failed to login: status ${it.status}")
                        callback("Erreur lors de la connexion")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to login: $e")
                callback("Erreur lors de la connexion")
            }
        }
    }

    fun registerStudent(context: Context, callback: (data: User?, error: String?) -> Unit) {
        viewModelScope.launch {
            try {
                NetworkManager.registerStudentAsync(context, studentRegisterRequestFlow.value!!)?.await().let {
                    if (it == null) return@launch

                    if (it.error != null) {
                        Log.e("MainViewModel", "Failed to register student: ${it.error}")
                        callback(null, it.error)
                        return@launch
                    }
                    if (it.status == 201 && it.data != null) {
                        val jwtToken = it.jwt
                        if (jwtToken != null) {
                            saveJwtToken(context.getSharedPreferences("jwtToken", Context.MODE_PRIVATE), jwtToken)
                        }
                        updateUser(it.data)
                        callback(it.data, null)
                    } else {
                        Log.e("MainViewModel", "Failed to register student: status ${it.status}")
                        callback(null, "Erreur lors de l'enregistrement (${it.status})")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to register student: $e")
                callback(null, "Erreur lors de l'enregistrement")
            }
        }
    }

    private fun saveJwtToken(sharedPreferences: SharedPreferences, token: String) {
        sharedPreferences.edit().putString("jwtToken", token).apply()
    }

    fun deleteJwtToken(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit().remove("jwtToken").apply()
    }

    fun getJwtToken(sharedPreferences: SharedPreferences): String? {
        return sharedPreferences.getString("jwtToken", null)
    }

    /*
    * val productFlow = MutableStateFlow<Product?>(null)

    fun getProduct(barcode: String) {
        viewModelScope.launch {
            try {
                val product = NetworkManager.getProductAsync(barcode).await()
                Log.d("ProductDetailsViewModel", "getProduct: $product")
                productFlow.emit(product.toProduct())
            } catch (e: Exception) {
                Log.e("ProductDetailsViewModel", "getProduct: $e")
            }
        }
    }
    * */

}