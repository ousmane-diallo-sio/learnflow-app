package com.example.learnflow

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.view.setPadding
import com.example.learnflow.components.*
import com.example.learnflow.model.Address
import com.example.learnflow.model.User
import com.example.learnflow.model.UserType
import com.example.learnflow.utils.FieldValidator
import com.example.learnflow.utils.Utils
import com.example.learnflow.webservices.Api
import com.example.learnflow.webservices.Api.userType
import fr.kameouss.instamemeeditor.components.ImagePickerFragment
import java.io.IOException
import java.net.URLDecoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private val SP_CB_KEY = "cbCredentialsChecked"
    private val SP_EMAIL_KEY = "credentialEmail"
    private val SP_PASSWORD_KEY = "credentialPassword"
    private lateinit var sharedPreferences: SharedPreferences

    //private var user: User = User()
    private var user: User = User().apply {
        profilePictureUrl = "https://d38b044pevnwc9.cloudfront.net/cutout-nuxt/enhancer/2.jpg"
    }
    private lateinit var imgPickerFragment: ImagePickerFragment

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
    private lateinit var ciLogin: CustomInput
    private lateinit var ciPassword: CustomInput
    private lateinit var cb: CheckBox
    private lateinit var tvBottomCTA: TextView
    private lateinit var btnLogin: CustomBtn
    private lateinit var btnRegisterCTA: CustomBtn
    private lateinit var btnLoginCTA: CustomBtn

    // Register form
    private lateinit var sliderRegisterProcess: Slider
    private lateinit var iSelectUserType: ItemsSelector
    private lateinit var ciFirstnameRegister: CustomInput
    private lateinit var ciLastnameRegister: CustomInput
    private lateinit var ciEmailRegister: CustomInput
    private lateinit var ciBirthdateRegister: CustomInput
    private lateinit var ciPhoneNumberRegister: CustomInput
    private lateinit var ciCityRegister: CustomInput
    private lateinit var ciStreetRegister: CustomInput
    private lateinit var ciZipCodeRegister: CustomInput
    private lateinit var ciFurtherAddressRegister: CustomInput
    private lateinit var ciPasswordRegister: CustomInput
    private lateinit var ivProfilePicRegister: ImageView

    // Student specific form
    private lateinit var siStudentSchoolLevel: SliderItem
    private lateinit var iSelectStudentSchoolLevel: ItemsSelector

    // Teacher specific form
    private lateinit var siTeacherIdentityCard: SliderItem
    private lateinit var ivTeacherIdentityCardPicker: ImagePickerFragment
    private lateinit var siTeacherDocumentsMain: SliderItem
    private lateinit var llTeacherDocumentsMain: LinearLayout
    private lateinit var btnTeacherPickDocumentMain: CustomBtn


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginPart = findViewById(R.id.loginPartMain)
        registerPart = findViewById(R.id.registerPartMain)
        cbWrapper = findViewById(R.id.cbWrapperMain)
        cb = cbWrapper.children.elementAt(0) as CheckBox
        ciLogin = findViewById(R.id.ciEmailMain)
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
        ivTeacherIdentityCardPicker = supportFragmentManager.findFragmentById(R.id.fragImgPickerTeacherIdentityCardMain) as ImagePickerFragment
        siTeacherDocumentsMain = findViewById(R.id.siTeacherDocumentsMain)
        llTeacherDocumentsMain = findViewById(R.id.llTeacherDocumentsMain)
        btnTeacherPickDocumentMain = findViewById(R.id.btnTeacherPickDocumentMain)
        ciFirstnameRegister = findViewById(R.id.ciFirstNameRegisterMain)
        ciLastnameRegister = findViewById(R.id.ciLastNameRegisterMain)
        ciEmailRegister = findViewById(R.id.ciEmailRegisterMain)
        ciPhoneNumberRegister = findViewById(R.id.ciPhoneNumberRegisterMain)
        ciCityRegister = findViewById(R.id.ciCityRegisterMain)
        ciStreetRegister = findViewById(R.id.ciStreetRegisterMain)
        ciZipCodeRegister = findViewById(R.id.ciZipCodeRegisterMain)
        ciFurtherAddressRegister = findViewById(R.id.ciFurtherAddressRegisterMain)
        ciPasswordRegister = findViewById(R.id.ciPasswordRegisterMain)
        ciBirthdateRegister = findViewById(R.id.ciBirthdateRegisterMain)
        ivProfilePicRegister = findViewById(R.id.ivProfilePicRegisterMain)

        imgPickerFragment = ImagePickerFragment()
        supportFragmentManager.beginTransaction().add(imgPickerFragment, "imgPickerFragmentMain").commit()

        sharedPreferences = getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        setupSchoolLevels()
    }

    override fun onStart() {
        super.onStart()
        val email = sharedPreferences.getString(SP_EMAIL_KEY, null) ?: ""
        val password = sharedPreferences.getString(SP_PASSWORD_KEY, null) ?: ""
        if (email.isNotEmpty()) {
            ciLogin.et.setText(email)
        }
        if (password.isNotEmpty()) {
            ciPassword.et.setText(password)
        }
        cb.isChecked = sharedPreferences.getBoolean(SP_CB_KEY, false)
        btnLogin.isLoading = false

        sliderRegisterProcess.btnLastSlide = CustomBtn(this, null).apply {
            tv.text = getString(R.string.validate)
            setOnClickListener {
                user.firstName = ciFirstnameRegister.et.text.toString()
                user.lastName = ciLastnameRegister.et.text.toString()
                user.email = ciEmailRegister.et.text.toString()
                user.birthdate = ciBirthdateRegister.et.text.toString()
                user.address?.city = ciCityRegister.et.text.toString()
                user.address?.street = ciStreetRegister.et.text.toString()
                user.address?.zipCode = ciZipCodeRegister.et.text.toString()
                user.address?.complement = ciFurtherAddressRegister.et.text.toString()
                user.password = ciPasswordRegister.et.text.toString()
                user.phoneNumber = ciPhoneNumberRegister.et.text.toString()
                user.schoolLevel = iSelectStudentSchoolLevel.items.find { it.isSelected }?.tvItem?.text.toString()

                Api.register(this@MainActivity, user) {response ->
                    runOnUiThread {
                        try {
                            val responseMsg = URLDecoder.decode(response.message, "UTF-8")
                            if (response.code !in 200..299) {
                                Log.e("MainActivity", "Error while registering user : $responseMsg")
                                Toast.makeText(this@MainActivity, responseMsg, Toast.LENGTH_SHORT).show()
                                throw IOException("Unexpected code $responseMsg")
                            }
                            Api.currentUser = User.fromJson(response.body!!.string())
                            Toast.makeText(this@MainActivity, "Bonjour ${user.firstName}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        } catch (e: Exception) {
                            Log.e("MainActivity", e.toString())
                        }
                    }
                }
            }
        }
        // TODO("Investigate how to make the validator detect the ImagePickerFragment")
        sliderRegisterProcess.validateForm = { sliderItem ->
            Utils.getAllNestedChildren(sliderItem)
                .filter { it is IValidator }
                .all { (it as IValidator).validate() }
        }
        ciBirthdateRegister.customValidator = object : CustomValidator {
            override var errorMessage: String = "Vous devez avoir au moins 6 ans pour vous inscrire"
            override var validate: (String) -> Boolean = { string ->
                FieldValidator.date(
                    string,
                    LocalDate.now().minusYears(6)
                )
            }
        }
        ciZipCodeRegister.customValidator = object : CustomValidator {
            override var errorMessage: String = "Code postal invalide"
            override var validate: (String) -> Boolean = { string ->
                FieldValidator.zipCode(string)
            }
        }
        setListeners()
    }

    private fun setListeners() {
        cbWrapper.setOnClickListener {
            cb.isChecked = !cb.isChecked
        }

        ciLogin.onInputValidation = { btnLogin.disabled = !it }
        ciPassword.onInputValidation = { btnLogin.disabled = !it }

        btnLogin.setOnClickListener {
            if (cb.isChecked) {
                saveCredentials()
            } else {
                deleteCredentials()
            }
            Api.login(this, ciLogin.et.text.toString(), ciPassword.et.text.toString())
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
                userType = UserType.STUDENT

                sliderRegisterProcess.addItems(siStudentSchoolLevel)
                sliderRegisterProcess.removeItems(siTeacherIdentityCard)
                sliderRegisterProcess.removeItems(siTeacherDocumentsMain)
            } else {
                userType = UserType.TEACHER

                sliderRegisterProcess.addItems(siTeacherIdentityCard)
                sliderRegisterProcess.addItems(siTeacherDocumentsMain)
                sliderRegisterProcess.removeItems(siStudentSchoolLevel)
            }
            // set currentUser type to selected type
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
        ivProfilePicRegister.setOnClickListener {
            imgPickerFragment.pickImage { uri: Uri? ->
                if (uri == null) return@pickImage
                ivProfilePicRegister.setImageURI(uri)
            }
        }
    }

    private fun saveCredentials() {
        val spEditor = sharedPreferences.edit()
        spEditor.putBoolean(SP_CB_KEY, cb.isChecked)
        spEditor.putString(SP_EMAIL_KEY, ciLogin.et.text.toString())
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