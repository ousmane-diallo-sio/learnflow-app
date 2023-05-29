package com.example.learnflow

import android.app.ProgressDialog.show
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.view.setPadding
import com.example.learnflow.components.*
import com.example.learnflow.model.User
import com.example.learnflow.webservices.Api
import fr.kameouss.instamemeeditor.components.ImagePickerFragment

class MainActivity : AppCompatActivity() {

    private val SP_CB_KEY = "cbCredentialsChecked"
    private val SP_EMAIL_KEY = "credentialEmail"
    private val SP_PASSWORD_KEY = "credentialPassword"
    private lateinit var sharedPreferences: SharedPreferences

    private var isLoginView = true
        set(value) {
            field = value
            val loginVisibility = if (value) VISIBLE else GONE
            val registerVisibility = if (value) GONE else VISIBLE

            tvBottomCTA.setText(if (loginVisibility == VISIBLE) R.string.create_account_cta else R.string.login_cta)

            btnRegisterCTA.visibility = loginVisibility
            loginPart.visibility = loginVisibility
            btnLoginCTA.visibility = registerVisibility
            registerPart.visibility = registerVisibility
        }

    private lateinit var loginPart: LinearLayout
    private lateinit var registerPart: LinearLayout
    private lateinit var cbWrapper: LinearLayout
    private lateinit var ciEmail: CustomInput
    private lateinit var ciPassword: CustomInput
    private lateinit var cb: CheckBox
    private lateinit var tvBottomCTA: TextView
    private lateinit var btnLogin: CustomBtn
    private lateinit var btnRegisterCTA: CustomBtn
    private lateinit var btnLoginCTA: CustomBtn
    private lateinit var sliderRegisterProcess: Slider
    private lateinit var iSelectUserType: ItemsSelector


    // Student specific form
    private lateinit var siStudentSchoolLevel: SliderItem
    private lateinit var iSelectStudentSchoolLevel: ItemsSelector

    // Teacher specific form
    private lateinit var siTeacherIdentityCard: SliderItem
    private lateinit var ivTeacherIdentityCardPicker: ImageView
    private lateinit var siTeacherDocumentsMain: SliderItem
    private lateinit var llTeacherDocumentsMain: LinearLayout
    private lateinit var btnTeacherPickDocumentMain: CustomBtn

    private var currentUser: User = User()
    private lateinit var imgPickerFragment: ImagePickerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginPart = findViewById(R.id.loginPartMain)
        registerPart = findViewById(R.id.registerPartMain)
        cbWrapper = findViewById(R.id.cbWrapperMain)
        cb = cbWrapper.children.elementAt(0) as CheckBox
        ciEmail = findViewById(R.id.ciEmailMain)
        ciPassword = findViewById(R.id.ciPasswordMain)
        tvBottomCTA = findViewById(R.id.tvBottomCTAMain)
        btnLogin = findViewById(R.id.btnLoginMain)
        btnRegisterCTA = findViewById(R.id.btnRegisterCTAMain)
        btnLoginCTA = findViewById(R.id.btnLoginCTAMain)
        sliderRegisterProcess = findViewById(R.id.sliderRegisterProcessMain)
        iSelectUserType = findViewById(R.id.iSelectUserTypeMain)
        iSelectStudentSchoolLevel = findViewById(R.id.iSelectStudentSchoolLevelMain)
        siStudentSchoolLevel = findViewById(R.id.siStudentSchoolLevelMain)
        siTeacherIdentityCard = findViewById(R.id.siTeacherIdentityCardMain)
        ivTeacherIdentityCardPicker = findViewById(R.id.ivTeacherIdentityCardPickerMain)
        siTeacherDocumentsMain = findViewById(R.id.siTeacherDocumentsMain)
        llTeacherDocumentsMain = findViewById(R.id.llTeacherDocumentsMain)
        btnTeacherPickDocumentMain = findViewById(R.id.btnTeacherPickDocumentMain)

        imgPickerFragment = ImagePickerFragment()
        supportFragmentManager.beginTransaction().add(imgPickerFragment, "imgPickerFragmentMain").commit()

        sharedPreferences = getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        setListeners()
        setupSchoolLevels()

        sliderRegisterProcess.btnLastSlide = CustomBtn(this, null).apply {
            tv.text = getString(R.string.validate)
            setOnClickListener {
                // Api.register(currentUser)
                btnLoginCTA.performClick()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val email = sharedPreferences.getString(SP_EMAIL_KEY, null) ?: ""
        val password = sharedPreferences.getString(SP_PASSWORD_KEY, null) ?: ""
        if (email.isNotEmpty()) {
            ciEmail.et.setText(email)
        }
        if (password.isNotEmpty()) {
            ciPassword.et.setText(password)
        }
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

        btnRegisterCTA.setOnClickListener {
            (it as CustomBtn).isLoading = true
            isLoginView = !isLoginView
            sliderRegisterProcess.slideTo(0)
        }
        btnLoginCTA.setOnClickListener { isLoginView = !isLoginView }

        iSelectUserType.setOnElementSelected {
            sliderRegisterProcess.btnNext.disabled = false
            if (it.selectorId == "studentSelector") {
                sliderRegisterProcess.addItems(siStudentSchoolLevel)

                sliderRegisterProcess.removeItems(siTeacherIdentityCard)
                sliderRegisterProcess.removeItems(siTeacherDocumentsMain)
            } else {
                sliderRegisterProcess.addItems(siTeacherIdentityCard)
                sliderRegisterProcess.addItems(siTeacherDocumentsMain)

                sliderRegisterProcess.removeItems(siStudentSchoolLevel)
            }
            // set currentUser type to selected type
        }

        ivTeacherIdentityCardPicker.setOnClickListener {
            imgPickerFragment.pickImage { uri: Uri? ->
                ivTeacherIdentityCardPicker.setImageURI(uri)
                ivTeacherIdentityCardPicker.setPadding(0)
                ivTeacherIdentityCardPicker.imageTintList = null
                ivTeacherIdentityCardPicker.layoutParams.width = LayoutParams.MATCH_PARENT
                ivTeacherIdentityCardPicker.layoutParams.height = LayoutParams.MATCH_PARENT
                ivTeacherIdentityCardPicker.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

        btnTeacherPickDocumentMain.setOnClickListener {
            if (llTeacherDocumentsMain.childCount >= 4) {
                AlertDialog.Builder(this)
                    .setTitle("Vous ne pouvez pas ajouter plus de 4 documents")
                    .setMessage("Veuillez supprimer au moins un document avant d'en ajouter un nouveau")
                    .setPositiveButton("Je comprends") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                return@setOnClickListener
            } else {
                imgPickerFragment.pickImage { uri: Uri? ->
                    if (uri == null) return@pickImage

                    val customInput = CustomInput(this, null)
                    customInput.et.hint = "Saisissez une description"
                    customInput.et.inputType = InputType.TYPE_CLASS_TEXT
                    customInput.layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )


                    val ivOverview = ImageView(this).apply {
                        setImageURI(uri)
                        layoutParams = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        setPadding(20)
                    }
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog
                        .setPositiveButton(getText(R.string.close)) { dialog, _ ->
                            dialog.dismiss()
                            ivOverview.parent?.let { (it as ViewGroup).removeView(ivOverview) }
                        }
                        .setNegativeButton(getText(R.string.delete)) { dialog, _ -> dialog.dismiss()
                            llTeacherDocumentsMain.removeView(customInput)
                        }
                        .setView(ivOverview)
                        .create()
                    customInput.setAction({ alertDialog.show() }, uri)
                    llTeacherDocumentsMain.addView(customInput)

                }
            }
        }
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

    private fun setupSchoolLevels() {
        val schoolLevels = arrayOf("CP", "CE1", "CE2", "CM1", "CM2", "6ème", "5ème", "4ème", "3ème", "2nde", "1ère", "Terminale", "Bac +1", "Bac +2", "Bac +3", "Bac +4", "Bac +5")
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        schoolLevels.forEach {
            val selectorItem = SelectorItem(this, null)
            selectorItem.layoutParams = layoutParams
            selectorItem.setText(it)
            selectorItem.selectorId = "selector$it"
            iSelectStudentSchoolLevel.addItems(selectorItem)
        }
    }

}