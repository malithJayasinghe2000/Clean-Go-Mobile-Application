package com.example.cleango.data.model

data class Bookings(
    val bookingID: String = "",
    val employeeID: String = "",
    val customerID: String  = "",
    val orderDate: String = "",
    val currentDate: String = "",
    val address: String = "",
    val info: String = "",
    val accepted: Boolean = false,
    val completed: Boolean = false,
)

