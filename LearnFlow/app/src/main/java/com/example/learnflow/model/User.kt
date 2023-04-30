package com.example.learnflow.model

import java.time.LocalDate

open class User {
    val firstName: String
    val lastName: String
    val birthDay: LocalDate
    val email: String
    val address: Address
    val phoneNumber: String

    constructor(firstName: String, lastName: String, birthDay: LocalDate, email: String, address: Address, phoneNumber: String) {
        this.firstName = firstName
        this.lastName = lastName
        this.birthDay = birthDay
        this.email = email
        this.address = address
        this.phoneNumber = phoneNumber
    }
}