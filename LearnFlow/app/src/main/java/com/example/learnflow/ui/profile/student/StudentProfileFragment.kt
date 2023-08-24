package com.example.learnflow.ui.profile.student

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.learnflow.HomeViewModel
import com.example.learnflow.R
import com.example.learnflow.components.CustomBtn
import com.example.learnflow.databinding.FragmentStudentProfileBinding
import com.example.learnflow.model.User
import com.example.learnflow.utils.Utils
import fr.kameouss.instamemeeditor.components.ImagePickerFragment
import kotlinx.coroutines.launch


class StudentProfileFragment : Fragment() {

    private var _binding: FragmentStudentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentProfileViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgPickerFragment = ImagePickerFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .add(imgPickerFragment, "imgPickerFragmentMain")
            .commit()

        val swipeRefreshLayout = binding.swipeRefreshLayoutStudentProfile
        val clMain = binding.clMainStudentProfile
        val spinnerSchoolLevel = Spinner(context, Spinner.MODE_DIALOG).apply {
            visibility = View.INVISIBLE
        }

        val ivPicture = binding.ivPictureProfileStudent
        val etFirstname = binding.etFirstnameProfileStudent
        val etLastname = binding.etLastnameProfileStudent
        val etSchoolLevel = binding.etSchoolLevelProfileStudent
        val etPhoneNumber = binding.etPhoneNumberProfileStudent
        val etAddress = binding.etAddressProfileStudent

        val btnEditPicture = binding.btnEditPictureProfileStudent
        val btnEditFirstname = binding.btnEditFirstnameProfileStudent
        val btnEditLastname = binding.btnEditLastnameProfileStudent
        val btnEditSchoolLevel = binding.btnEditSchoolLevelProfileStudent
        val btnEditPhoneNumber = binding.btnEditPhoneNumberProfileStudent
        val btnEditAddress = binding.btnEditAddressProfileStudent
        val btnLogout = binding.btnLogoutProfileStudent

        val editBtns = listOf(
            btnEditFirstname,
            btnEditLastname,
            btnEditSchoolLevel,
            btnEditAddress
        )

        val editTexts = listOf(etFirstname, etLastname, etSchoolLevel, etPhoneNumber, etAddress)
        editTexts.forEach { it.inputType = InputType.TYPE_NULL }

        swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.getMe(requireActivity()) {
                swipeRefreshLayout.isRefreshing = false
            }
        }

        bindData()
        lifecycleScope.launch {
            homeViewModel.userFlow.collect {
                bindData()
                spinnerSchoolLevel.setSelection(
                    homeViewModel.schoolLevels.indexOf(homeViewModel.userFlow.value?.student?.schoolLevel)
                )
            }
        }

        spinnerSchoolLevel.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            homeViewModel.schoolLevels
        )
        spinnerSchoolLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                homeViewModel.userFlow.value?.let {
                    updateUser {
                        it.copy(
                            student = it.student?.copy(schoolLevel = homeViewModel.schoolLevels[position])
                        )
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        clMain.addView(spinnerSchoolLevel)

        btnEditPicture.setOnClickListener {
            handleDuplicateEdit(editBtns, btnEditPicture)
            btnEditPicture.triggerEditOrBaseStyle(true)
            imgPickerFragment.pickImage { uri ->
                homeViewModel.userFlow.value?.let { user ->
                    btnEditPicture.triggerEditOrBaseStyle(false)
                    ivPicture.setImageURI(uri)
                    val newProfilePic = user.profilePicture.copy(
                        base64 = Utils.bitmapToBase64(ivPicture.drawToBitmap()) ?: ""
                    )
                    updateUser {
                        it.copy(
                            profilePicture = newProfilePic,
                            student = it.student?.copy(
                                profilePicture = newProfilePic
                            )
                        )
                    }
                }
            }
        }

        btnEditFirstname.setOnClickListener {
            handleDuplicateEdit(editBtns, btnEditFirstname)
            homeViewModel.userFlow.value?.let { user ->
                if (etFirstname.isEditable()) {
                    updateUser {
                        it.copy(
                            firstName = etFirstname.text.toString(),
                            student = it.student?.copy(firstName = etFirstname.text.toString())
                        )
                    }
                }
                etFirstname.markEditableOrDisable(user.firstName.toEditable())
                btnEditFirstname.triggerEditOrBaseStyle(etFirstname.isEditable())
            }
        }

        btnEditLastname.setOnClickListener {
            handleDuplicateEdit(editBtns, btnEditLastname)
            homeViewModel.userFlow.value?.let { user ->
                if (etLastname.isEditable()) {
                    updateUser {
                        it.copy(
                            lastName = etLastname.text.toString(),
                            student = it.student?.copy(lastName = etLastname.text.toString())
                        )
                    }
                }
                etLastname.markEditableOrDisable(user.lastName.toEditable())
                btnEditLastname.triggerEditOrBaseStyle(etLastname.isEditable())
            }
        }

        btnEditSchoolLevel.setOnClickListener {
            spinnerSchoolLevel.performClick()
        }

        btnEditPhoneNumber.setOnClickListener {
            handleDuplicateEdit(editBtns, btnEditPhoneNumber)
            homeViewModel.userFlow.value?.let { user ->
                if (etPhoneNumber.isEditable()) {
                    updateUser {
                        it.copy(
                            phoneNumber = etPhoneNumber.text.toString(),
                            student = it.student?.copy(phoneNumber = etPhoneNumber.text.toString())
                        )
                    }
                }
                etPhoneNumber.markEditableOrDisable(
                    user.phoneNumber.toEditable(),
                    InputType.TYPE_CLASS_NUMBER
                )
                btnEditPhoneNumber.triggerEditOrBaseStyle(etPhoneNumber.isEditable())
            }
        }

        btnLogout.setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.logout(requireActivity()) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(context, "Déconnexion", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(
                            StudentProfileFragmentDirections.actionNavigationProfileToActivityMain()
                        )
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private fun bindData() {
        val ivPicture = binding.ivPictureProfileStudent
        val tvEmail = binding.tvEmailProfileStudent
        val tvBirthdate = binding.tvBirthdateProfileStudent
        val etFirstname = binding.etFirstnameProfileStudent
        val etLastname = binding.etLastnameProfileStudent
        val etSchoolLevel = binding.etSchoolLevelProfileStudent
        val etPhoneNumber = binding.etPhoneNumberProfileStudent
        val etAddress = binding.etAddressProfileStudent

        ivPicture.setImageBitmap(homeViewModel.userFlow.value?.profilePictureBitmap)
        tvEmail.text = homeViewModel.userFlow.value?.email
        tvBirthdate.text = homeViewModel.userFlow.value?.birthdate.toString()
        etFirstname.text = getString(
            R.string.profile_firstname,
            homeViewModel.userFlow.value?.firstName
        ).toEditable()
        etLastname.text = getString(
            R.string.profile_lastname,
            homeViewModel.userFlow.value?.lastName
        ).toEditable()
        etSchoolLevel.text = getString(
            R.string.profile_school_level,
            homeViewModel.userFlow.value?.student?.schoolLevel
        ).toEditable()
        etPhoneNumber.text = getString(
            R.string.profile_phone_number,
            homeViewModel.userFlow.value?.phoneNumber
        ).toEditable()
        etAddress.text = getString(
            R.string.profile_address,
            homeViewModel.userFlow.value?.address.toString()
        ).toEditable()
    }

    private fun updateUser(newValue: (User) -> User) {
        homeViewModel.userFlow.value?.let {
            lifecycleScope.launch {
                homeViewModel.updateUser(requireActivity(), newValue(it))
            }
        }
    }

    private fun handleDuplicateEdit(editBtns: List<CustomBtn>, currentBtn: CustomBtn) {
        editBtns.forEach { btn ->
            if (btn != currentBtn && btn.hasEditStyle()) {
                btn.performClick()
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun CustomBtn.triggerEditOrBaseStyle(useEditStyle: Boolean) {
        if (useEditStyle) {
            tag = "editStyle"
            setBtnBackgroundColor(
                ResourcesCompat.getColor(resources, R.color.crayola, null)
            )
            iconBefore.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.baseline_check_24,
                    null
                )
            )
            return
        }
        tag = "notEditStyle"
        setBtnBackgroundColor(
            ResourcesCompat.getColor(resources, R.color.coral, null)
        )
        iconBefore.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.baseline_edit_24,
                null
            )
        )
    }

    private fun CustomBtn.hasEditStyle(): Boolean {
        Log.d("hasEdit", "tag : $tag")
        return tag == "editStyle"
    }

    private fun EditText.markEditableOrDisable(
        defaultValue: Editable,
        newInputType: Int = InputType.TYPE_CLASS_TEXT
    ) {

        if (inputType == InputType.TYPE_NULL) {
            text = defaultValue
            inputType = newInputType
            requestFocus()
            return
        }
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        inputType = InputType.TYPE_NULL
        bindData()
    }

    private fun EditText.isEditable() = inputType != InputType.TYPE_NULL

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}