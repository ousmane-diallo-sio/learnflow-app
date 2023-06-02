package com.example.learnflow.model

import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.RequestBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter

open class User {
    companion object {
        fun fromJson(json: String): User {
            val gson = Gson()
             return gson.fromJson(json, User::class.java)
        }
    }

    var firstName: String? = null
    var lastName: String? = null
    var birthdate: String? = null
    var email: String? = null
    var address: Address? = null
    var phoneNumber: String? = null
    var profilePictureUrl: String? = null
    var password: String? = null

    constructor(firstName: String, lastName: String, birthdate: String, email: String, address: Address, phoneNumber: String, profilePictureUrl: String, password: String?) {
        this.firstName = firstName
        this.lastName = lastName
        this.birthdate = birthdate
        this.email = email
        this.address = address
        this.phoneNumber = phoneNumber
        this.profilePictureUrl = profilePictureUrl
        this.password = password
    }

}