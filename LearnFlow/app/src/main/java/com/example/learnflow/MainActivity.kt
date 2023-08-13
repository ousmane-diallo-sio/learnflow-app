package com.example.learnflow

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.example.learnflow.components.*
import com.example.learnflow.model.Address
import com.example.learnflow.model.Document
import com.example.learnflow.model.DocumentType
import com.example.learnflow.model.User
import com.example.learnflow.model.UserType
import com.example.learnflow.network.NetworkManager
import com.example.learnflow.network.NetworkManager.userType
import com.example.learnflow.network.StudentSignupDTO
import com.example.learnflow.network.TeacherSignupDTO
import com.example.learnflow.network.UserLoginDTO
import com.example.learnflow.utils.FieldValidator
import com.example.learnflow.utils.Utils
import com.google.android.material.snackbar.Snackbar
import fr.kameouss.instamemeeditor.components.ImagePickerFragment
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class MainActivity : AppCompatActivity(), TeacherSignupConfirmationListener {

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
    private lateinit var ipProfilePicRegister: ImagePicker

    // Student specific form
    private lateinit var siStudentSchoolLevel: SliderItem
    private lateinit var iSelectStudentSchoolLevel: ItemsSelector

    // Teacher specific form
    private lateinit var siTeacherIdentityCard: SliderItem
    private lateinit var ipTeacherIdentityCardPicker: ImagePicker
    private lateinit var siTeacherDocuments: SliderItem
    private lateinit var llTeacherDocuments: LinearLayout
    private lateinit var btnTeacherPickDocument: CustomBtn
    private lateinit var siTeacherSchoolSubjectsTeached: SliderItem
    private lateinit var llSchoolSubjectsTeacher: LinearLayout


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
        siTeacherDocuments = findViewById(R.id.siTeacherDocumentsMain)
        llTeacherDocuments = findViewById(R.id.llTeacherDocumentsMain)
        btnTeacherPickDocument = findViewById(R.id.btnTeacherPickDocumentMain)
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
        ipProfilePicRegister = findViewById(R.id.imgPickerProfilePicRegisterMain)
        ipTeacherIdentityCardPicker = findViewById(R.id.imgPickerTeacherIdentityCardMain)
        siTeacherSchoolSubjectsTeached = findViewById(R.id.siTeacherSchoolSubjectsTeachedMain)
        llSchoolSubjectsTeacher = findViewById(R.id.llSchoolSubjectsTeacherMain)

        imgPickerFragment = ImagePickerFragment()
        supportFragmentManager.beginTransaction().add(imgPickerFragment, "imgPickerFragmentMain")
            .commit()

        sharedPreferences = getSharedPreferences(
            getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        setupSchoolLevels()
        setupSchoolSubjectsTeached()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart(this)
        viewModel.getSchoolSubjects(this)

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
        siTeacherDocuments.slideValidator = {
            val isValid = llTeacherDocuments.childCount > 0
            if (!isValid) {
                Snackbar.make(
                    registerPart,
                    "Vous devez ajouter au moins un justificatif",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            isValid
        }

        ciLogin.onInputValidation = { btnLogin.disabled = false }
        ciPassword.onInputValidation = { btnLogin.disabled = false }
        ciPassword.et.setOnEditorActionListener{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnLogin.performClick()
                return@setOnEditorActionListener true
            }
            false
        }

        btnLogin.setOnClickListener {
            btnLogin.isLoading = true
            val userLoginRequest = UserLoginDTO(
                ciLogin.et.text.toString(),
                ciPassword.et.text.toString(),
                null
            )
            viewModel.login(this@MainActivity, userLoginRequest) { error ->
                btnLogin.isLoading = false
                if (error != null) {
                    Snackbar.make(loginPart, error, Snackbar.LENGTH_SHORT).show()
                    return@login
                }
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            }
        }

        btnRegisterCTA.setOnClickListener {
            isLoginView = !isLoginView
            sliderRegisterProcess.slideTo(0)
        }
        btnLoginCTA.setOnClickListener { isLoginView = !isLoginView }

        iSelectUserType.setOnElementSelected {
            sliderRegisterProcess.btnNext.disabled = false
            if (it.selectorId == "studentSelector") {
                userType = UserType.STUDENT

                sliderRegisterProcess.addItems(siStudentSchoolLevel)
                sliderRegisterProcess.removeItems(
                    siTeacherIdentityCard,
                    siTeacherDocuments,
                    siTeacherSchoolSubjectsTeached
                )
            } else {
                userType = UserType.TEACHER
                sliderRegisterProcess.addItems(
                    siTeacherIdentityCard,
                    siTeacherDocuments,
                    siTeacherSchoolSubjectsTeached
                )
                sliderRegisterProcess.removeItems(siStudentSchoolLevel)
            }
        }
        btnTeacherPickDocument.setOnClickListener {
            if (llTeacherDocuments.childCount >= 4) {
                AlertDialog.Builder(this)
                    .setTitle("Vous ne pouvez pas ajouter plus de 4 documents")
                    .setMessage("Veuillez supprimer au moins un document avant d'en ajouter un nouveau")
                    .setPositiveButton("Je comprends") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                return@setOnClickListener
            }

            imgPickerFragment.pickImage { uri: Uri? ->
                if (uri == null) return@pickImage

                val ivOverview = ImageView(this).apply {
                    layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    setImageURI(uri)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    setPadding(20)
                }

                val document = Document(
                    "document-${UUID.randomUUID()}",
                    null,
                    Utils.bitmapToBase64(ivOverview.drawable.toBitmap()) ?: "",
                    DocumentType.IMAGE
                )

                val customInput = CustomInput(this, null)
                customInput.layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                customInput.et.hint = "Intitulé du document"
                customInput.et.inputType = InputType.TYPE_CLASS_TEXT
                customInput.textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        document.name = s.toString()
                        viewModel.addOrReplaceTeacherDocument(document)
                    }

                }

                val alertDialog = AlertDialog.Builder(this)
                alertDialog
                    .setPositiveButton(getText(R.string.close)) { dialog, _ ->
                        dialog.dismiss()
                        ivOverview.parent?.let { (it as ViewGroup).removeView(ivOverview) }
                    }
                    .setNegativeButton(getText(R.string.delete)) { dialog, _ ->
                        dialog.dismiss()
                        llTeacherDocuments.removeView(customInput)
                        viewModel.removeTeacherDocument(document)
                    }
                    .setView(ivOverview)
                    .create()
                customInput.setAction({ alertDialog.show() }, uri)
                llTeacherDocuments.addView(customInput)

            }
        }

        ipTeacherIdentityCardPicker.setOnClickListener {
            imgPickerFragment.pickImage { uri: Uri? ->
                if (uri == null) return@pickImage
                ipTeacherIdentityCardPicker.ivImage.setImageURI(uri)
                viewModel.addOrReplaceTeacherDocument(
                    Document(
                        "Pièce d'identité",
                        null,
                        Utils.bitmapToBase64(ipTeacherIdentityCardPicker.ivImage.drawable.toBitmap())
                            ?: "",
                        DocumentType.IMAGE
                    ),
                )
                ipTeacherIdentityCardPicker.hideError()
            }
        }
        ipProfilePicRegister.setOnClickListener {
            imgPickerFragment.pickImage { uri: Uri? ->
                if (uri == null) return@pickImage
                ipProfilePicRegister.ivImage.setImageURI(uri)
                ipProfilePicRegister.hideError()
                sliderRegisterProcess.btnLastSlide = CustomBtn(this, null).apply {
                    tv.text = getString(R.string.validate)
                    setOnClickListener { handleSignup() }
                }
            }
        }
    }

    private fun setupSchoolLevels() {
        val schoolLevels = arrayOf(
            "CP",
            "CE1",
            "CE2",
            "CM1",
            "CM2",
            "6ème",
            "5ème",
            "4ème",
            "3ème",
            "2nde",
            "1ère",
            "Terminale",
            "Bac +1",
            "Bac +2",
            "Bac +3",
            "Bac +4",
            "Bac +5"
        )
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        schoolLevels.forEach {
            val selectorItem = SelectorItem(this, null)
            selectorItem.layoutParams = layoutParams
            selectorItem.setText(it)
            selectorItem.selectorId = "selector$it"
            iSelectStudentSchoolLevel.addItems(selectorItem)
        }
    }

    private fun setupSchoolSubjectsTeached() {
        lifecycleScope.launch {
            viewModel.schoolSubjectsFlow.collect { schoolSubjects ->
                schoolSubjects.forEach { schoolSubject ->
                    val checkboxCounterItem = CheckboxCounterItem(this@MainActivity)

                    checkboxCounterItem.tv.text = schoolSubject.name
                    checkboxCounterItem.ci.et.setText("1")
                    checkboxCounterItem.setOnClickListener {
                        viewModel.addOrRemoveSchoolSubjectTeached(schoolSubject, 1)
                    }

                    checkboxCounterItem.ci.textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            if (s.toString().isNotEmpty() && s.toString().toInt() > 50) {
                                checkboxCounterItem.ci.et.setText("1")
                                Snackbar.make(
                                    findViewById(android.R.id.content),
                                    "Vous ne pouvez pas avoir plus de 50 ans d'expérience",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {
                            viewModel.editSchoolSubjectTeachedExp(
                                schoolSubject,
                                s.toString().toIntOrNull() ?: 1
                            )
                        }
                    }
                    llSchoolSubjectsTeacher.addView(checkboxCounterItem)
                }
            }
        }
    }

    private fun handleSignup() {

        fun onRegisterStudent(data: User?, error: String?) {
            if (error != null) {
                Log.e("MainActivity", "Error while registering user : ${error}")
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                return
            }
            Toast.makeText(
                this@MainActivity,
                "Bonjour ${data?.firstName}",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        }

        fun onRegisterTeacher(data: User?, error: String?) {
            if (error != null) {
                Log.e("MainActivity", "Error while registering user : ${error}")
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                return
            }
            val teacherSignupConfirmation = TeacherSignupConfirmation()
            teacherSignupConfirmation.show(supportFragmentManager, "teacherSignupConfirmation")
        }

        if (userType == UserType.STUDENT) {
            viewModel.updateStudentRegisterRequest(
                StudentSignupDTO(
                    firstName = ciFirstnameRegister.et.text.toString(),
                    lastName = ciLastnameRegister.et.text.toString(),
                    email = ciEmailRegister.et.text.toString(),
                    birthdate = NetworkManager.formatDateFRToISOString(
                        ciBirthdateRegister.et.text.toString()
                    ) ?: "",
                    address = Address(
                        ciCityRegister.et.text.toString(),
                        ciStreetRegister.et.text.toString(),
                        ciZipCodeRegister.et.text.toString(),
                        ciFurtherAddressRegister.et.text.toString().ifEmpty { null }
                    ),
                    password = ciPasswordRegister.et.text.toString(),
                    phoneNumber = ciPhoneNumberRegister.et.text.toString(),
                    schoolLevel = iSelectStudentSchoolLevel.items.find { it.isItemSelected }?.tvItem?.text.toString(),
                    profilePicture = Document(
                        "Photo de profil",
                        null,
                        Utils.bitmapToBase64(ipProfilePicRegister.ivImage.drawable.toBitmap())
                            ?: "",
                        DocumentType.IMAGE
                    )
                )
            )

            viewModel.registerStudent(this@MainActivity) { data, error ->
                onRegisterStudent(data, error)
            }
        } else {
            viewModel.updateTeacherRegisterRequest(
                TeacherSignupDTO(
                    firstName = ciFirstnameRegister.et.text.toString(),
                    lastName = ciLastnameRegister.et.text.toString(),
                    email = ciEmailRegister.et.text.toString(),
                    birthdate = NetworkManager.formatDateFRToISOString(
                        ciBirthdateRegister.et.text.toString()
                    ) ?: "",
                    address = Address(
                        ciCityRegister.et.text.toString(),
                        ciStreetRegister.et.text.toString(),
                        ciZipCodeRegister.et.text.toString(),
                        ciFurtherAddressRegister.et.text.toString().ifEmpty { null }
                    ),
                    password = ciPasswordRegister.et.text.toString(),
                    phoneNumber = ciPhoneNumberRegister.et.text.toString(),
                    documents = viewModel.teacherSignupDocumentsFlow.value,
                    schoolSubjectsTeached = viewModel.teacherSchoolSubjectsTeached,
                    profilePicture = Document(
                        "Photo de profil",
                        null,
                        Utils.bitmapToBase64(ipProfilePicRegister.ivImage.drawable.toBitmap())
                            ?: "",
                        DocumentType.IMAGE
                    )
                )
            )
            viewModel.registerTeacher(this@MainActivity) { data, error ->
                onRegisterTeacher(data, error)
            }
        }
    }

    override fun onTeacherSignupConfirmationDismissed() {
        isLoginView = true
    }

}