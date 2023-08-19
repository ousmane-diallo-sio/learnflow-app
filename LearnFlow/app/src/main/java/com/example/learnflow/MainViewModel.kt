package com.example.learnflow

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnflow.model.Document
import com.example.learnflow.model.SchoolSubject
import com.example.learnflow.model.SchoolSubjectTeached
import com.example.learnflow.model.User
import com.example.learnflow.network.NetworkManager
import com.example.learnflow.network.StudentSignupDTO
import com.example.learnflow.network.TeacherSignupDTO
import com.example.learnflow.network.UserLoginDTO
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class MainViewModel : ViewModel() {

    private val userFlow = MutableStateFlow<User?>(null)
    val schoolSubjectsFlow = MutableStateFlow<List<SchoolSubject>>(emptyList())

    private val studentSignupDTOFlow = MutableStateFlow<StudentSignupDTO?>(null)
    private val teacherSignupDTOFlow = MutableStateFlow<TeacherSignupDTO?>(null)
    val teacherSignupDocumentsFlow = MutableStateFlow<MutableList<Document>>(mutableListOf())
    val teacherSchoolSubjectsTeached = mutableListOf<SchoolSubjectTeached>()

    fun onStart(mainActivity: MainActivity) {
        NetworkManager.observeNetworkConnectivity(mainActivity)
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userFlow.emit(user)
        }
    }

    fun updateStudentRegisterRequest(studentRegisterDTO: StudentSignupDTO) {
        viewModelScope.launch {
            studentSignupDTOFlow.emit(studentRegisterDTO)
        }
    }

    fun updateTeacherRegisterRequest(teacherRegisterDTO: TeacherSignupDTO) {
        viewModelScope.launch {
            teacherSignupDTOFlow.emit(teacherRegisterDTO)
        }
    }

    fun getSchoolSubjects(context: Activity) {
        fun handleRequestFailure() {
            Snackbar.make(
                context.findViewById(android.R.id.content),
                "Erreur lors de la récupération des matières scolaires",
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry") {
                    getSchoolSubjects(context)
                }
                .show()
        }

        viewModelScope.launch {
            try {
                val res = NetworkManager.getSchoolSubjectsAsync(context)?.await()

                res?.let { serverResponse ->
                    serverResponse.data?.let { data ->
                        schoolSubjectsFlow.value = data
                        return@launch
                    }
                }
                Log.e("MainViewModel", "API response data is null !!")
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)
                Log.e("MainViewModel", "Failed to get school subjects: ${serverResponse?.error}")
                handleRequestFailure()
            } catch (e: SocketTimeoutException) {
                Log.e("MainViewModel", "Connection timed out: $e")
                handleRequestFailure()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to get school subjects: ${e.message}")
                handleRequestFailure()
            }
        }
    }

    fun addOrReplaceTeacherDocument(document: Document) {
        val duplicateDocument = teacherSignupDocumentsFlow.value.find { it.name == document.name }
        duplicateDocument?.let {
            teacherSignupDocumentsFlow.value =
                teacherSignupDocumentsFlow.value.minus(it).toMutableList()
        }
        teacherSignupDocumentsFlow.value =
            teacherSignupDocumentsFlow.value.plus(document).toMutableList()
    }

    fun removeTeacherDocument(document: Document) {
        teacherSignupDocumentsFlow.value =
            teacherSignupDocumentsFlow.value.minus(document).toMutableList()
    }

    fun addOrRemoveSchoolSubjectTeached(schoolSubject: SchoolSubject, nbYearsExp: Int) {
        if (!teacherSchoolSubjectsTeached.removeIf {
                it.schoolSubject.name == schoolSubject.name
            }) {
            teacherSchoolSubjectsTeached.add(SchoolSubjectTeached(schoolSubject, nbYearsExp))
        }
    }

    fun editSchoolSubjectTeachedExp(schoolSubject: SchoolSubject, nbYearsExp: Int) {
        teacherSchoolSubjectsTeached.find { it.schoolSubject.name == schoolSubject.name }
            ?.run { this.nbYearsExp = nbYearsExp }
    }

    fun autoLogin(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkManager.autoLoginAsync(context)?.await()

                res?.let { serverResponse ->
                    serverResponse.data?.let { data ->
                        updateUser(data)
                        serverResponse.jwt?.let { jwt ->
                            NetworkManager.saveJwt(
                                context,
                                jwt
                            )
                        }
                        onSuccess()
                        return@launch
                    }
                }
                Log.e("MainViewModel", "API response data is null !!")
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)

                Log.e("MainViewModel", "Failed to login: ${serverResponse?.error}")
            } catch (e: SocketTimeoutException) {
                Log.e("MainViewModel", "Connection timed out: $e")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to login: ${e.message}")
            }
        }
    }

    fun login(
        context: Context,
        userLoginRequest: UserLoginDTO,
        callback: (error: String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val res = NetworkManager.loginAsync(context, userLoginRequest)?.await()

                res?.let { serverResponse ->
                    serverResponse.data?.let { data ->
                        updateUser(data)
                        serverResponse.jwt?.let { jwt ->
                            NetworkManager.saveJwt(
                                context,
                                jwt
                            )
                        }
                        callback(null)
                        return@launch
                    }
                }
                Log.e("MainViewModel", "API response data is null !!")
                callback("Erreur lors de la connexion")
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)

                Log.e("MainViewModel", "Failed to login: ${serverResponse?.error}")
                callback(serverResponse?.error ?: context.getString(R.string.an_error_occured))
            } catch (e: SocketTimeoutException) {
                Log.e("MainViewModel", "Connection timed out: $e")
                callback("Le serveur est injoignable")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to login: ${e.message}")
                callback(context.getString(R.string.an_error_occured))
            }
        }
    }

    fun registerStudent(context: Context, callback: (data: User?, error: String?) -> Unit) {
        val defaultErrorMsg = context.getString(R.string.an_error_occured)

        viewModelScope.launch {
            try {
                val res = NetworkManager.registerStudentAsync(context, studentSignupDTOFlow.value!!)
                    ?.await()

                res?.let { serverResponse ->
                    serverResponse.data?.let { data ->
                        updateUser(data)
                        serverResponse.jwt?.let { jwt ->
                            NetworkManager.saveJwt(
                                context,
                                jwt
                            )
                        }
                        callback(data, null)
                        return@launch
                    }
                    Log.e("MainViewModel", "API response data is null !!")
                    callback(null, defaultErrorMsg)
                }
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)

                Log.e("MainViewModel", "Failed to register: ${serverResponse?.error}")
                callback(
                    null,
                    serverResponse?.error ?: context.getString(R.string.an_error_occured)
                )
            } catch (e: SocketTimeoutException) {
                Log.e("MainViewModel", "Connection timed out: $e")
                callback(null, "Le serveur est injoignable")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to register: ${e.message}")
                callback(null, defaultErrorMsg)
            }
        }
    }

    fun registerTeacher(context: Context, callback: (data: User?, error: String?) -> Unit) {
        val defaultErrorMsg = context.getString(R.string.an_error_occured)

        viewModelScope.launch {
            try {
                val res = NetworkManager.registerTeacherAsync(context, teacherSignupDTOFlow.value!!)
                    ?.await()

                res?.let { serverResponse ->
                    serverResponse.data?.let { data ->
                        updateUser(data)
                        serverResponse.jwt?.let { jwt -> NetworkManager.saveJwt(context, jwt) }
                        callback(data, null)
                        return@launch
                    }
                    Log.e("MainViewModel", "API response data is null !!")
                    callback(null, defaultErrorMsg)
                }
            } catch (e: HttpException) {
                val serverResponse = NetworkManager.parseHttpException(e)

                Log.e("MainViewModel", "Failed to register: ${serverResponse?.error}")
                callback(
                    null,
                    serverResponse?.error ?: context.getString(R.string.an_error_occured)
                )
            } catch (e: SocketTimeoutException) {
                Log.e("MainViewModel", "Connection timed out: $e")
                callback(null, "Le serveur est injoignable")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to register: ${e.message}")
                callback(null, defaultErrorMsg)
            }
        }
    }

    /*
    * val productFlow = MutableStateFlow<Product?>(null)

    fun getProduct(barcode: String) {
        viewModelScope.launch {
            try {
                val product = NetworkManager.getProductAsync(barcode).await()
                Log.d("ProductDetailsViewModel", "getProduct: $product")
                productFlow.emit(product.toProduct())
            } catch (e: Exception) {
                Log.e("ProductDetailsViewModel", "getProduct: $e")
            }
        }
    }
    * */

}