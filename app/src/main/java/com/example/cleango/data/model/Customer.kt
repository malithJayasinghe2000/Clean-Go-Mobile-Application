package com.example.cleango.data.model

data class Customer(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address:String= "",
    val userType: String = "Customer",
    val dp: String = "https://www.pngmart.com/files/21/Admin-Profile-Vector-PNG-Clipart.png",
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
)
