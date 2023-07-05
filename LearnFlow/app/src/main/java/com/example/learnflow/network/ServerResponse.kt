package com.example.learnflow.network

import com.google.gson.annotations.SerializedName

data class ServerResponse<T>(
    @SerializedName("status")
    val status: Int,
    @SerializedName("jwt")
    val jwt: String?,
    @SerializedName("data")
    val data: T?,
    @SerializedName("error")
    val error: String?,
)