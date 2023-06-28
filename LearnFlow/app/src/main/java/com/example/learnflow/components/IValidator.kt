package com.example.learnflow.components

import android.widget.TextView

interface IValidator {

    var isRequired: Boolean
    var tvError: TextView
    var customValidator: CustomValidator?

    fun triggerError(error: String)
    fun hideError()
    fun validate(): Boolean
}

interface CustomValidator {
    var errorMessage: String
    var validate: (String) -> Boolean
}