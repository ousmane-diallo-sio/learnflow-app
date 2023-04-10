package com.example.learnflow.model

import android.location.Address
import java.time.LocalDate

class User {
    val firstName: String
    val lastName: String
    val birthDay: LocalDate
    val email: String
    val address: Address

    constructor(firstName: String, lastName: String, birthDay: LocalDate, email: String, address: Address) {
        this.firstName = firstName
        this.lastName = lastName
        this.birthDay = birthDay
        this.email = email
        this.address = address
    }
}