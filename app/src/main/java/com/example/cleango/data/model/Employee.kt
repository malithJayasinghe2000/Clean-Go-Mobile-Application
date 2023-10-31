package com.example.cleango.data.model

data class Employee(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val serviceCategory:String = "",
    val serviceArea:String = "",
    val jobTitle:String = "",
    val jobDescription:String = "",
    val price:String  = "",
    val userType: String = "Employee",
    val dp: String = "https://i.pinimg.com/originals/8b/16/7a/8b167af653c2399dd93b952a48740620.jpg",
    val rating: Double = 0.0,
    val ratingCount: Int = 0,

)