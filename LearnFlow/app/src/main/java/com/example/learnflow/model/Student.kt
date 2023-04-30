package com.example.learnflow.model

import java.time.LocalDate

class Student(
    firstName: String,
    lastName: String,
    birthDay: LocalDate,
    email: String,
    address: Address,
    phoneNumber: String,
) : User(firstName, lastName, birthDay, email, address, phoneNumber)
