package com.example.learnflow.utils

object StringValidator {

    fun email(string: String): Boolean {
        val pattern = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        return pattern.matches(string)
    }

}