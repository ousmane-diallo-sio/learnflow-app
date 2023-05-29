package com.example.learnflow.components

import android.widget.LinearLayout
import com.example.learnflow.R
import com.example.learnflow.utils.StringValidator

interface IValidator {

    fun triggerError(error: String)

    fun hideError()

    fun validate(): Boolean

}