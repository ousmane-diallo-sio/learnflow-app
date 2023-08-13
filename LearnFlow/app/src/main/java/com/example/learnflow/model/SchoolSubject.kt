package com.example.learnflow.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SchoolSubject (
    val name: String,
) : Parcelable

@Parcelize
data class SchoolSubjectTeached (
    val schoolSubject: SchoolSubject,
    var nbYearsExp: Int,
) : Parcelable