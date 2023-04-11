package com.example.learnflow

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.learnflow.components.CustomInput

class MainActivity : AppCompatActivity() {

    private lateinit var cbWrapper: LinearLayout
    private lateinit var ciEmail: CustomInput
    private lateinit var ciPassword: CustomInput

    private val SP_EMAIL_KEY = "credentialEmail"
    private val SP_PASSWORD_KEY = "credentialPassword"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cbWrapper = findViewById(R.id.cbWrapperMain)
        ciEmail = findViewById(R.id.ciEmail)
        ciPassword = findViewById(R.id.ciPassword)

        sharedPreferences = getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        )

        setOnClickListeners()

        ciEmail.et.setText(sharedPreferences.getString(SP_EMAIL_KEY, null))
    }

    private fun setOnClickListeners() {
        val cb = cbWrapper.children.elementAt(0) as CheckBox
        val spEditor = sharedPreferences.edit()
        cbWrapper.setOnClickListener {
            cb.isChecked = !cb.isChecked
            if (cb.isChecked) {
                spEditor.putString(SP_EMAIL_KEY, ciEmail.et.text.toString())
                spEditor.putString(SP_PASSWORD_KEY, ciPassword.et.text.toString())
                spEditor.apply()
            }
        }
    }

}