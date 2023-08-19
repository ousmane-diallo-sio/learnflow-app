package com.example.learnflow

import androidx.lifecycle.ViewModel
import com.example.learnflow.model.User
import kotlinx.coroutines.flow.MutableStateFlow

class UserViewModel: ViewModel() {
    val userFlow: MutableStateFlow<User?> = MutableStateFlow(null)
}