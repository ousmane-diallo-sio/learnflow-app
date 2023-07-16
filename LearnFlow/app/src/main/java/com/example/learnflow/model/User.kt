package com.example.learnflow.model

import android.os.Parcelable
import android.util.Log
import com.example.learnflow.network.ServerResponse
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import kotlinx.parcelize.Parcelize
import okhttp3.FormBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class LocalDateTypeAdapter : TypeAdapter<LocalDateTime>() {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: LocalDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(formatter.format(value))
        }
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): LocalDateTime? {
        return if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            null
        } else {
            val dateString = `in`.nextString()
            LocalDateTime.parse(dateString, formatter)
        }
    }
}

@Parcelize
data class User(
    var firstName: String,
    var lastName: String,
    var birthdate: LocalDateTime,
    var email: String,
    var address: Address,
    var phoneNumber: String,
    var profilePictureUrl: String,
    var student: Student? = null,
    var teacher: Teacher? = null
) : Parcelable {
    companion object {
        fun toUser(response: ServerResponse<User>): User? = response.let { resp ->
            return try {
                return User(
                    firstName = resp.data?.firstName ?: "",
                    lastName = resp.data?.lastName ?: "",
                    birthdate = resp.data?.birthdate ?: LocalDateTime.now(),
                    email = resp.data?.email ?: "",
                    address = resp.data?.address ?: Address(
                        "street", "city", "zipCode", "complement"
                    ),
                    phoneNumber = resp.data?.phoneNumber ?: "",
                    profilePictureUrl = resp.data?.profilePictureUrl ?: "",
                    student = resp.data?.student,
                    teacher = resp.data?.teacher
                )
            } catch (e: Exception) {
                Log.e("User", "Unable to parse User: $e")
                return null
            }
        }
    }
}

@Parcelize
data class Student(
    var test: String,
) : Parcelable

@Parcelize
data class Teacher(var test: String) : Parcelable