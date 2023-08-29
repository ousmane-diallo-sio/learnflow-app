package com.example.learnflow.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    var street: String,
    var city: String,
    var zipCode: String,
    var complement: String? = null
) : Parcelable {

    override fun toString(): String {
        return "$street, $city, $zipCode"
    }
}