package com.example.learnflow.network

import com.example.learnflow.model.Address

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