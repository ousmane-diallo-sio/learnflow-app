package com.example.learnflow.model

import android.graphics.Bitmap
import android.os.Parcelable
import com.example.learnflow.utils.Utils
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    var profilePicture: Document,
    var student: Student? = null,
    var teacher: Teacher? = null
) : Parcelable {

    @IgnoredOnParcel
    var profilePictureBitmap: Bitmap? = null
    init {
        profilePictureBitmap = Utils.base64ToBitmap(profilePicture.base64)
    }
}

@Parcelize
data class Student(
    var test: String,
) : Parcelable

@Parcelize
data class Teacher(var test: String) : Parcelable