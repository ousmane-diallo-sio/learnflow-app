package com.example.learnflow.network

import com.example.learnflow.model.Jwt

data class ServerResponse<T>(
    val status: Int,
    val jwt: Jwt?,
    val data: T?,
    val error: String?,
)