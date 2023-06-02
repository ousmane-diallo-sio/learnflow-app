package com.example.learnflow.model

enum class UserType {
    STUDENT,
    TEACHER;

    override fun toString(): String {
        return when (this) {
            STUDENT -> "student"
            TEACHER -> "teacher"
        }
    }
}