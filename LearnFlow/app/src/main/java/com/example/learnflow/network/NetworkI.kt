package com.example.learnflow.network
import com.example.learnflow.model.User
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkI {

    @POST("/register/student")
    fun registerStudentAsync(@Body requestBody: StudentRegistrationRequest): Deferred<ServerResponse<User.Student>>
}