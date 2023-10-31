package com.example.cleango

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnEmployee = findViewById<Button>(R.id.btnEmployee)
        val btnCustomer = findViewById<Button>(R.id.btnCustomer)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnEmployee.setOnClickListener {
         //Go to register employee page
            val intent = Intent(this, RegisterEmployee::class.java)
            startActivity(intent)
        }

        btnCustomer.setOnClickListener {
         //Go to register customer page
            val intent = Intent(this, RegisterCustomer::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
         //Go to login page
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}