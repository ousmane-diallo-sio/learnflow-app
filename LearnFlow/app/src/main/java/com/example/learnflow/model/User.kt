package com.example.learnflow.model

import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.RequestBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter

open class User(
    var firstName: String? = null,
    var lastName: String? = null,
    var birthdate: String? = null,
    var email: String? = null,
    var address: Address? = null,
    var phoneNumber: String? = null,
    var profilePictureUrl: String? = null,
    var schoolLevel: String? = null,
    var password: String? = null
) {
    companion object {
        fun fromJson(json: String): User {
            val gson = Gson()
             return gson.fromJson(json, User::class.java)
        }
    }

}