package com.example.learnflow

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnflow.model.Jwt
import com.example.learnflow.model.User
import com.example.learnflow.network.NetworkManager
import com.example.learnflow.network.StudentSignupDTO
import com.example.learnflow.network.UserLoginDTO
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel : ViewModel() {

    private val userFlow = MutableStateFlow<User?>(null)
    private val studentSignupDTOFlow = MutableStateFlow<StudentSignupDTO?>(null)
    // profilePictureUrl = "https://d38b044pevnwc9.cloudfront.net/cutout-nuxt/enhancer/2.jpg"

    fun onStart(mainActivity: MainActivity) {
        NetworkManager.observeNetworkConnectivity(mainActivity)
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userFlow.emit(user)
        }
    }

    fun updateStudentRegisterRequest(studentRegisterDTO: StudentSignupDTO) {
        viewModelScope.launch {
            studentSignupDTOFlow.emit(studentRegisterDTO)
        }
    }

    fun login(
        context: Context,
        userLoginRequest: UserLoginDTO,
        callback: (error: String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val res = NetworkManager.loginAsync(context, userLoginRequest)?.await()

                res?.let { serverResponse ->
                    serverResponse.data?.let { data ->
                        updateUser(data)
                        serverResponse.jwt?.let { jwt ->
                            saveJwtToken(
                                context.getSharedPreferences("jwtToken", Context.MODE_PRIVATE),
                                jwt
                            )
                        }
                        callback(null)
                        return@launch
                    }
                }
                Log.e("MainViewModel", "API response data is null !!")
                callback("Erreur lors de la connexion")
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)

                Log.e("MainViewModel", "Failed to login: ${serverResponse?.error}")
                callback(serverResponse?.error ?: context.getString(R.string.an_error_occured))
            }
        }
    }

    fun registerStudent(context: Context, callback: (data: User?, error: String?) -> Unit) {
        val defaultErrorMsg = context.getString(R.string.an_error_occured)

        viewModelScope.launch {
            try {
                val res = NetworkManager.registerStudentAsync(context, studentSignupDTOFlow.value!!)
                    ?.await()

                res?.let { serverResponse ->
                    serverResponse.data?.let { data ->
                        updateUser(data)
                        serverResponse.jwt?.let { jwt ->
                            saveJwtToken(
                                context.getSharedPreferences("jwtToken", Context.MODE_PRIVATE),
                                jwt
                            )
                        }
                        callback(data, null)
                        return@launch
                    }
                    Log.e("MainViewModel", "API response data is null !!")
                    callback(null, defaultErrorMsg)
                }
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)

                Log.e("MainViewModel", "Failed to login: ${serverResponse?.error}")
                callback(null, serverResponse?.error ?: context.getString(R.string.an_error_occured))
            }
        }
    }

    private fun saveJwtToken(sharedPreferences: SharedPreferences, token: Jwt) {
        sharedPreferences.edit().putString("jwtToken", Gson().toJson(token)).apply()
    }

    fun deleteJwtToken(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit().remove("jwtToken").apply()
    }

    fun getJwtToken(sharedPreferences: SharedPreferences): Jwt? {
        return Gson().fromJson(
            sharedPreferences.getString("jwtToken", null),
            Jwt::class.java
        )
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