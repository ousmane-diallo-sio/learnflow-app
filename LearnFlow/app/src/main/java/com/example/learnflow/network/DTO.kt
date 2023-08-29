package com.example.learnflow.network

import android.os.Parcelable
import com.example.learnflow.model.Address
import com.example.learnflow.model.Document
import com.example.learnflow.model.SchoolSubjectTeached
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
    val password: String,
    val schoolSubjectsTeached: List<SchoolSubjectTeached>
)

@Parcelize
data class UserLoginDTO(
    val email: String?,
    val password: String?,
    val jwt: String?
) : Parcelable