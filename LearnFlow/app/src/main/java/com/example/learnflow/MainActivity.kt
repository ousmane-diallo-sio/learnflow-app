package com.example.learnflow

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.*
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.learnflow.components.CustomBtn
import com.example.learnflow.components.CustomInput
import com.example.learnflow.webservices.Api

class MainActivity : AppCompatActivity() {

    private lateinit var loginPart: LinearLayout
    private lateinit var registerPart: LinearLayout
    private lateinit var cbWrapper: LinearLayout
    private lateinit var ciEmail: CustomInput
    private lateinit var ciPassword: CustomInput
    private lateinit var cb: CheckBox
    private lateinit var btnLogin: CustomBtn
    private lateinit var btnRegisterCTA: CustomBtn
    private lateinit var btnLoginCTA: CustomBtn

    private val SP_CB_KEY = "cbCredentialsChecked"
    private val SP_EMAIL_KEY = "credentialEmail"
    private val SP_PASSWORD_KEY = "credentialPassword"
    private lateinit var sharedPreferences: SharedPreferences

    private var isLoginView = true
    set(value) {
        field = value
        val visibilityLogin = if (value) VISIBLE else GONE
        val visibilityRegister = if (value) GONE else VISIBLE
        btnRegisterCTA.visibility = visibilityLogin
        loginPart.visibility = visibilityLogin

        btnLoginCTA.visibility = visibilityRegister
        registerPart.visibility = visibilityRegister
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginPart = findViewById(R.id.loginPartMain)
        registerPart = findViewById(R.id.registerPartMain)
        cbWrapper = findViewById(R.id.cbWrapperMain)
        cb = cbWrapper.children.elementAt(0) as CheckBox
        ciEmail = findViewById(R.id.ciEmailMain)
        ciPassword = findViewById(R.id.ciPasswordMain)
        btnLogin = findViewById(R.id.btnLoginMain)
        btnRegisterCTA = findViewById(R.id.btnRegisterCTAMain)
        btnLoginCTA = findViewById(R.id.btnLoginCTAMain)

        sharedPreferences = getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        ciEmail.et.setText(sharedPreferences.getString(SP_EMAIL_KEY, null))
        ciPassword.et.setText(sharedPreferences.getString(SP_PASSWORD_KEY, null))
        cb.isChecked = sharedPreferences.getBoolean(SP_CB_KEY, false)
        btnLogin.isLoading = false
        handleLoginBtn()
    }

    private fun setListeners() {
        cbWrapper.setOnClickListener {
            cb.isChecked = !cb.isChecked
        }
        ciEmail.setOnTextChanged { s: CharSequence?, start: Int, before: Int, count: Int ->
            handleLoginBtn()
        }
        ciPassword.setOnTextChanged { s: CharSequence?, start: Int, before: Int, count: Int ->
            handleLoginBtn()
        }

        btnLogin.setOnClickListener {
            btnLogin.isLoading = true
            if (cb.isChecked) {
                saveCredentials()
            } else {
                deleteCredentials()
            }
            Api.login(this, ciEmail.et.text.toString(), ciPassword.et.text.toString())
        }

        btnRegisterCTA.setOnClickListener { isLoginView = !isLoginView }
        btnLoginCTA.setOnClickListener { isLoginView = !isLoginView }
    }

    private fun saveCredentials() {
        val spEditor = sharedPreferences.edit()
        spEditor.putBoolean(SP_CB_KEY, cb.isChecked)
        spEditor.putString(SP_EMAIL_KEY, ciEmail.et.text.toString())
        spEditor.putString(SP_PASSWORD_KEY, ciPassword.et.text.toString())
        spEditor.apply()
    }

    private fun deleteCredentials() {
        val spEditor = sharedPreferences.edit()
        spEditor
            .remove(SP_EMAIL_KEY)
            .remove(SP_PASSWORD_KEY)
            .remove(SP_CB_KEY)
            .apply()
    }

    private fun handleLoginBtn() {
        if (ciEmail.et.text.isEmpty() || ciPassword.et.text.isEmpty()) {
            btnLogin.disabled = true
            return
        }
        btnLogin.disabled = false
    }

}