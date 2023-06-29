package com.example.learnflow

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnflow.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    val userFlow = MutableStateFlow<User?>(null)
    // profilePictureUrl = "https://d38b044pevnwc9.cloudfront.net/cutout-nuxt/enhancer/2.jpg"

    fun updateUser(user: User) {
        viewModelScope.launch {
            userFlow.emit(user)
        }
    }

    fun register(context: Context, user: User) {
        viewModelScope.launch {
            /* try {
                val response = NetworkManager.registerAsync(user).await()
                if (response.isSuccessful) {
                    val jwtToken = response.body()?.jwtToken
                    if (jwtToken != null) {
                        saveJwtToken(context.getSharedPreferences("jwtToken", Context.MODE_PRIVATE), jwtToken)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }*/
        }
    }

    fun saveJwtToken(sharedPreferences: SharedPreferences, token: String) {
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