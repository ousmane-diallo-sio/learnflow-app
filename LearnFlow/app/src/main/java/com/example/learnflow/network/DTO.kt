package com.example.learnflow.network

import android.os.Parcelable
import com.example.learnflow.model.Address
import com.example.learnflow.model.Document
import kotlinx.parcelize.Parcelize

data class StudentSignupDTO(
    val firstName: String,
    val lastName: String,
    val birthdate: String,
    val email: String,
    val address: Address,
    val phoneNumber: String,
    val profilePicture: Document,
    val schoolLevel: String,
    val password: String
)

data class TeacherSignupDTO(
    val firstName: String,
    val lastName: String,
    val birthdate: String,
    val email: String,
    val address: Address,
    val phoneNumber: String,
    val profilePicture: Document,
    val documents: MutableList<Document>,
    val password: String
)

/*
* const TeacherValidationSchema = Joi.object({
  firstName: Joi.string().min(2).max(20).required(),
  lastName: Joi.string().min(2).max(25).required(),
  birthdate: Joi.date().required(),
  email: Joi.string().email().required(),
  phoneNumber: Joi.string().pattern(/^((\+)33|0|0033)[1-9](\d{2}){4}$/).required(),
  profilePictureUrl: Joi.string().min(1).max(2048).required(),
  address: AddressValidationSchema.required(),
  documents: Joi.array().items(DocumentValidationSchema.optional()).required(),
  password: PasswordValidationSchema.required()
})
* */

@Parcelize
data class UserLoginDTO(
    val email: String?,
    val password: String?,
    val jwtToken: String?
) : Parcelable