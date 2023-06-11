package com.example.learnflow.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object FieldValidator {

    fun email(string: String): Boolean {
        val pattern = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        return pattern.matches(string)
    }

    fun date (string: String): Boolean {
        val pattern = Regex("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$")
        return pattern.matches(string)
    }

    fun date (string: String, maxDate: LocalDate): Boolean {
        if (!date(string)) return false
        val date = LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE))
        return date?.isBefore(maxDate) ?: false
    }

    fun phoneNumber(string: String): Boolean {
        val pattern = Regex("^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}\$")
        return pattern.matches(string)
    }

    fun password(string: String): Boolean {
        val pattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$")
        return pattern.matches(string)
    }

}