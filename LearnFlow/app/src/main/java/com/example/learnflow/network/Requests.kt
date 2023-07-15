package com.example.learnflow.network

import android.os.Parcelable
import com.example.learnflow.model.Address
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StudentRegisterRequest(
    val firstName: String,
    val lastName: String,
    val birthdate: String,
    val email: String,
    val address: Address,
    val phoneNumber: String,
    val profilePictureUrl: String,
    val password: String
)

@Parcelize
data class UserLoginRequest(
    val email: String?,
    val password: String?,
    val jwtToken: String?
) : Parcelable