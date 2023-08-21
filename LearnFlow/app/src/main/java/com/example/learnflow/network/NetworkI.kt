package com.example.learnflow.network
import com.example.learnflow.model.SchoolSubject
import com.example.learnflow.model.User
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkI {
    @POST("/auth/autologin/user")
    fun autoLoginAsync(): Deferred<ServerResponse<User>>

    @POST("/auth/login/user")
    fun loginAsync(@Body requestBody: UserLoginDTO): Deferred<ServerResponse<User>>

    @POST("/register/student")
    fun registerStudentAsync(@Body requestBody: StudentSignupDTO): Deferred<ServerResponse<User>>

    @POST("/register/teacher")
    fun registerTeacherAsync(@Body requestBody: TeacherSignupDTO): Deferred<ServerResponse<User>>

    @GET("/users/me")
    fun getMeAsync(): Deferred<ServerResponse<User>>

    @PATCH("/users/me")
    fun updateUserAsync(@Body requestBody: User): Deferred<ServerResponse<User>>

    @GET("/teachers/{query}")
    fun getTeachersAsync(@Path("query") query: String): Deferred<ServerResponse<List<User>>>

    @GET("/students/{query}")
    fun getStudentsAsync(@Path("query") query: String): Deferred<ServerResponse<List<User>>>

    @GET("/school-subjects")
    fun getSchoolSubjectsAsync(): Deferred<ServerResponse<List<SchoolSubject>>>
}