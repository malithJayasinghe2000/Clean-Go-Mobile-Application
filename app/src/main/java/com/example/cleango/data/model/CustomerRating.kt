package com.example.cleango.data.model

data class CustomerRating(
    val id: String = "",
    val jobID: String = "",
    val customerID: String = "",
    val employeeID: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val date: String = "",
)
