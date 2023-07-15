package com.example.learnflow.network
import com.example.learnflow.model.Student
import com.example.learnflow.model.User
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkI {
    @POST("/auth/login/user")
    fun loginAsync(@Body requestBody: UserLoginRequest): Deferred<ServerResponse<User>>

    @POST("/auth/register/student")
    fun registerStudentAsync(@Body requestBody: StudentRegisterRequest): Deferred<ServerResponse<User>>
}