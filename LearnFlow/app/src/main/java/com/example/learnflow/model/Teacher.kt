package com.example.learnflow.model

import java.io.File
import java.time.LocalDate

class Teacher(
    firstName: String,
    lastName: String,
    birthDay: LocalDate,
    email: String,
    address: Address,
    phoneNumber: String,
    degrees: ArrayList<Degree>,
    identityCard: File,
    isValidated: Boolean
) : User(firstName, lastName, birthDay, email, address, phoneNumber)
