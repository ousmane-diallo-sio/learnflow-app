package com.example.learnflow.utils

import io.github.cdimascio.dotenv.dotenv

object EnvUtils {

    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

    private const val placeholder = "value-not-found"

    val API_BASE_URL = dotenv["API_BASE_URL"] ?: placeholder
    val MAPS_API_KEY = dotenv["MAPS_API_KEY"] ?: placeholder
}