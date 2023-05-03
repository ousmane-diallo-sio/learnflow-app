package com.example.learnflow.model

import java.time.LocalDate

open class User {
    var firstName: String? = null
    var lastName: String? = null
    var birthDay: LocalDate? = null
    var email: String? = null
    var address: Address? = null
    var phoneNumber: String? = null

    constructor() {}

    constructor(firstName: String, lastName: String, birthDay: LocalDate, email: String, address: Address, phoneNumber: String) {
        this.firstName = firstName
        this.lastName = lastName
        this.birthDay = birthDay
        this.email = email
        this.address = address
        this.phoneNumber = phoneNumber
    }
}