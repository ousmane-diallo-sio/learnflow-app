package com.example.learnflow.ui.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnflow.model.User
import com.example.learnflow.network.NetworkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class SearchTeacherViewModel : ViewModel() {

    val searchFlow = MutableStateFlow<List<User>>(emptyList())

    fun searchTeachers(context: Context, query: String) {
        viewModelScope.launch {
            try {
                val teachers = NetworkManager.getTeachersAsync(context, query)?.await()

                teachers?.data?.let { searchFlow.emit(it) }
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)
                Log.e("MainViewModel", "Failed to register: ${serverResponse?.error}")
            } catch (e: SocketTimeoutException) {
                Log.e("MainViewModel", "Connection timed out: $e")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to register: ${e.message}")
            }
        }
    }

    fun searchStudents(context: Context, query: String) {
        viewModelScope.launch {
            try {
                val students = NetworkManager.getStudentsAsync(context, query)?.await()

                students?.data?.let { searchFlow.emit(it) }
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)
                Log.e("MainViewModel", "Failed to register: ${serverResponse?.error}")
            } catch (e: SocketTimeoutException) {
                Log.e("MainViewModel", "Connection timed out: $e")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to register: ${e.message}")
            }
        }
    }

    fun resetUsers() {
        viewModelScope.launch {
            searchFlow.emit(emptyList())
        }
    }
}