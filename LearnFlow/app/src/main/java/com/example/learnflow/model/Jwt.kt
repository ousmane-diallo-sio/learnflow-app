package com.example.learnflow.model

data class Jwt(
    val expired: Boolean,
    val payload: JwtPayload,
    val stale: Boolean,
    val token: String,
    val valid: Boolean
)

data class JwtPayload(
    val email: String,
    val role: String,
    val stales: Long
)