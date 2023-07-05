package com.example.learnflow.model

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import okhttp3.FormBody
import okhttp3.RequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Parcelize
open class User(
    open var firstName: String,
    open var lastName: String,
    open var birthdate: LocalDate,
    open var email: String,
    open var address: Address,
    open var phoneNumber: String,
    open var profilePictureUrl: String,
    var student: Student? = null,
    var teacher: Teacher? = null
) : Parcelable

data class Student(
    override var firstName: String,
    override var lastName: String,
    override var birthdate: LocalDate,
    override var email: String,
    override var address: Address,
    override var phoneNumber: String,
    override var profilePictureUrl: String
) : User(
    firstName,
    lastName,
    birthdate,
    email,
    address,
    phoneNumber,
    profilePictureUrl,
) {
    init {
        student = this
    }
}

data class Teacher(
    override var firstName: String,
    override var lastName: String,
    override var birthdate: LocalDate,
    override var email: String,
    override var address: Address,
    override var phoneNumber: String,
    override var profilePictureUrl: String,
    var degrees: ArrayList<Degree>,
    var identityCard: File,
    var isValidated: Boolean
) : User(
    firstName,
    lastName,
    birthdate,
    email,
    address,
    phoneNumber,
    profilePictureUrl,
) {
    init {
        teacher = this
    }
}