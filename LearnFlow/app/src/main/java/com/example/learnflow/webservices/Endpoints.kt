package com.example.learnflow.webservices

import com.example.learnflow.utils.EnvUtils

object Endpoints {

    val loginStudent = "${EnvUtils.API_BASE_URL}/login/student"
    val loginTeacher = "${EnvUtils.API_BASE_URL}/login/teacher"
    val registerStudent = "${EnvUtils.API_BASE_URL}/register/student"
    val registerTeacher = "${EnvUtils.API_BASE_URL}/register/teacher"

}