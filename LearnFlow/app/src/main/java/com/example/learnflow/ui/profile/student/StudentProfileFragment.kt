package com.example.learnflow.ui.profile.student

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.learnflow.HomeViewModel
import com.example.learnflow.R
import com.example.learnflow.databinding.FragmentStudentProfileBinding
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

        val tvEmail = binding.tvEmailProfileStudent
        val tvBirthdate = binding.tvBirthdateProfileStudent

        val etFirstname = binding.etFirstnameProfileStudent
        val etLastname = binding.etLastnameProfileStudent
        val etSchoolLevel = binding.etSchoolLevelProfileStudent
        val etPhoneNumber = binding.etPhoneNumberProfileStudent
        val etAddress = binding.etAddressProfileStudent
        val editTexts = listOf(etFirstname, etLastname, etSchoolLevel, etPhoneNumber, etAddress)

        val btnEditFirstname = binding.btnEditFirstnameProfileStudent
        val btnEditLastname = binding.btnEditLastnameProfileStudent
        val btnEditSchoolLevel = binding.btnEditSchoolLevelProfileStudent
        val btnEditPhoneNumber = binding.btnEditPhoneNumberProfileStudent
        val btnEditAddress = binding.btnEditAddressProfileStudent
        val btnValidate = binding.btnValidateProfileStudent
        val btns = listOf(
            btnEditFirstname,
            btnEditLastname,
            btnEditSchoolLevel,
            btnEditPhoneNumber,
            btnEditAddress
        )

        updateAll()
        lifecycleScope.launch {
            homeViewModel.userFlow.collect {
                updateAll()
            }
        }

        editTexts.forEach { it.inputType = InputType.TYPE_NULL }

        btnEditFirstname.setOnClickListener {
            // EditText component
            // Data source
            // Input type
            // Button component
            if (etFirstname.inputType == InputType.TYPE_NULL) {
                etFirstname.text = homeViewModel.userFlow.value?.firstName?.toEditable()
                etFirstname.inputType = InputType.TYPE_CLASS_TEXT
                etFirstname.requestFocus()
                btnEditFirstname.setBtnBackgroundColor(
                    ResourcesCompat.getColor(resources, R.color.crayola, null)
                )
                btnEditFirstname.iconBefore.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.baseline_check_24,
                        null
                    )
                )
                return@setOnClickListener
            }
            val imm = getSystemService(requireContext(), InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            etFirstname.inputType = InputType.TYPE_NULL
            btnEditFirstname.setBtnBackgroundColor(
                ResourcesCompat.getColor(resources, R.color.coral, null)
            )
            btnEditFirstname.iconBefore.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.baseline_edit_24,
                    null
                )
            )
            lifecycleScope.launch {
                homeViewModel.userFlow.value?.let {
                    homeViewModel.updateUser(
                        requireActivity(),
                        it.copy(
                            firstName = etFirstname.text.toString(),
                            student = it.student?.copy(
                                firstName = etFirstname.text.toString()
                            )
                        )
                    )
                }
            }
        }
    }

    private fun updateAll() {
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

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}