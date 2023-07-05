package com.example.learnflow

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import com.example.learnflow.components.*
import com.example.learnflow.model.Address
import com.example.learnflow.model.User
import com.example.learnflow.model.UserType
import com.example.learnflow.network.StudentRegisterRequest
import com.example.learnflow.network.UserLoginRequest
import com.example.learnflow.utils.FieldValidator
import com.example.learnflow.utils.Utils
import com.example.learnflow.webservices.Api
import com.example.learnflow.webservices.Api.userType
import fr.kameouss.instamemeeditor.components.ImagePickerFragment
import java.io.IOException
import java.net.URLDecoder
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var sharedPreferences: SharedPreferences

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
    private lateinit var ciLogin: CustomInput
    private lateinit var ciPassword: CustomInput
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
    private lateinit var ivProfilePicRegister: ImagePickerFragment

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
        ivProfilePicRegister = supportFragmentManager.findFragmentById(R.id.fragImgPickerProfilePicRegisterMain) as ImagePickerFragment

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
        btnLogin.isLoading = false

        sliderRegisterProcess.btnLastSlide = CustomBtn(this, null).apply {
            tv.text = getString(R.string.validate)
            setOnClickListener {
                if (userType == UserType.STUDENT) {
                    viewModel.updateStudentRegisterRequest(
                        StudentRegisterRequest(
                            ciFirstnameRegister.et.text.toString(),
                            ciLastnameRegister.et.text.toString(),
                            ciEmailRegister.et.text.toString(),
                            ciBirthdateRegister.et.text.toString(),
                            Address(
                                ciCityRegister.et.text.toString(),
                                ciStreetRegister.et.text.toString(),
                                ciZipCodeRegister.et.text.toString(),
                                ciFurtherAddressRegister.et.text.toString()
                            ),
                            ciPasswordRegister.et.text.toString(),
                            ciPhoneNumberRegister.et.text.toString(),
                            iSelectStudentSchoolLevel.items.find { it.isSelected }?.tvItem?.text.toString(),
                        )
                    )

                    viewModel.registerStudent(this@MainActivity) { data, error ->
                        if (error != null) {
                            Log.e("MainActivity", "Error while registering user : ${error}")
                            Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                            return@registerStudent
                        }
                        Toast.makeText(this@MainActivity, "Bonjour ${data?.firstName}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    }
                }
            }
        }
        sliderRegisterProcess.validateForm = { sliderItem, index ->
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
        ciLogin.onInputValidation = { btnLogin.disabled = false }
        ciPassword.onInputValidation = { btnLogin.disabled = false }

        btnLogin.setOnClickListener {
            val userLoginRequest = UserLoginRequest(
                ciLogin.et.text.toString(),
                ciPassword.et.text.toString(),
                null
            )
            viewModel.login(this@MainActivity, userLoginRequest) { error ->
                if (error != null) {
                    Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    return@login
                }
                Toast.makeText(this@MainActivity, "Connexion", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            }
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

    private fun getImagePickerToValidate(): List<ImagePickerFragment> {
        return if (userType == UserType.STUDENT) {
            listOf(ivProfilePicRegister)
        } else {
            listOf(ivProfilePicRegister, ivTeacherIdentityCardPicker)
        }
    }

}