package com.example.learnflow.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class Degree(
    val name: String,
    val level: String,
    val image: File
) : Parcelable
