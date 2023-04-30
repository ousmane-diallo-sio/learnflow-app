package com.example.learnflow.model

class Address {
    private val street: String
    private val city: String
    private val zipCode: String

    constructor(street: String, city: String, zipCode: String) {
        this.street = street
        this.city = city
        this.zipCode = zipCode
    }
}