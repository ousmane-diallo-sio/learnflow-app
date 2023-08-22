package com.example.learnflow.ui.profile.student

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.learnflow.HomeViewModel
import com.example.learnflow.R
import com.example.learnflow.components.CustomBtn
import com.example.learnflow.databinding.FragmentStudentProfileBinding
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

        bindData()
        lifecycleScope.launch {
            homeViewModel.userFlow.collect { bindData() }
        }

        swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.getMe(requireActivity()) {
                swipeRefreshLayout.isRefreshing = false
            }
        }

        val editTexts = listOf(etFirstname, etLastname, etSchoolLevel, etPhoneNumber, etAddress)
        editTexts.forEach { it.inputType = InputType.TYPE_NULL }


        btnEditPicture.setOnClickListener {
            imgPickerFragment.pickImage { uri ->
                lifecycleScope.launch {
                    homeViewModel.userFlow.value?.let { userFlow ->
                        ivPicture.setImageURI(uri)
                        val newProfilePic = userFlow.profilePicture.copy(
                            base64 = Utils.bitmapToBase64(ivPicture.drawToBitmap()) ?: ""
                        )
                        homeViewModel.updateUser(
                            requireActivity(),
                            userFlow.copy(
                                profilePicture = newProfilePic,
                                student = userFlow.student?.copy(
                                    profilePicture = newProfilePic
                                )
                            )
                        )
                    }
                }
            }
        }

        btnEditFirstname.setOnClickListener {
            homeViewModel.userFlow.value?.firstName?.toEditable()?.let { firstName ->
                etFirstname.markEditableOrDisable(firstName)
            }
            btnEditFirstname.triggerEditOrBaseStyle(etFirstname.inputType != InputType.TYPE_NULL)

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

        btnLogout.setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.logout(requireActivity()) {
                    requireActivity().runOnUiThread{
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

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun CustomBtn.triggerEditOrBaseStyle(editStyle: Boolean): Unit {
        if (editStyle) {
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

    private fun EditText.markEditableOrDisable(defaultValue: Editable): Unit {
        if (inputType == InputType.TYPE_NULL) {
            text = defaultValue
            inputType = InputType.TYPE_CLASS_TEXT
            requestFocus()
            return
        }
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        inputType = InputType.TYPE_NULL
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}