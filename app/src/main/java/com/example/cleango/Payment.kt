package com.example.cleango

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.cleango.data.model.Bookings
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth

class Payment : AppCompatActivity() {

    // Get the current user ID
    private val auth = FirebaseAuth.getInstance()
    var userID = auth.currentUser?.uid;

    //Firebase Storage
    private val database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        //Get total from intent
        val empID = intent.getStringExtra("empID")
        val orderDate = intent.getStringExtra("date")
        val address = intent.getStringExtra("bookingAddress")
        val info = intent.getStringExtra("bookingInfo")

        //Initialize views
        val cardNumber = findViewById<EditText>(R.id.et_card_number)
        val expieryMM = findViewById<EditText>(R.id.et_expiration_month)
        val expieryYY = findViewById<EditText>(R.id.et_expiration_year)
        val cardCvv = findViewById<EditText>(R.id.et_cvv)
        val btnPay = findViewById<Button>(R.id.btn_pay)
        val btnBack = findViewById<Button>(R.id.btn_cancel)

        btnPay.setOnClickListener(){
            //Get user input
            val cardNum = cardNumber.text.toString()
            val MM = expieryMM.text.toString()
            val YY = expieryYY.text.toString()
            val Cvv = cardCvv.text.toString()

            //Validate data
            var validated = true

            // Validate card number
            if (cardNum.isEmpty()) {
                cardNumber.error = "Card number is required"
                cardNumber.requestFocus()
                validated = false
            }
            else if (cardNum.toLong() < 16) {
                cardNumber.error = "Please enter a valid card number"
                cardNumber.requestFocus()
                validated = false
            }

            // Validate expiration month
            else if (MM.isEmpty()) {
                expieryMM.error = "Expiration month is required"
                expieryMM.requestFocus()
                validated = false

            } else if (MM.length != 2 || MM.toInt() > 12) {
                expieryMM.error = "Please enter a valid expiration month"
                expieryMM.requestFocus()
                validated = false
            }

            // Validate expiration year
            else if (YY.isEmpty()) {
                expieryYY.error = "Expiration year is required"
                expieryYY.requestFocus()
                validated = false

            } else if (YY.length != 2 || YY.toInt() < 23) {
                expieryYY.error = "Please enter a valid expiration year"
                expieryYY.requestFocus()
                validated = false
            }

            // Validate cvv
            else if (Cvv.isEmpty()) {
                cardCvv.error = "CVV is required"
                cardCvv.requestFocus()
                validated = false
            } else if (Cvv.length != 3) {
                cardCvv.error = "Please enter a valid CVV"
                cardCvv.requestFocus()
                validated = false
            }


           // If all data is valid
            if (validated) {

                //Get current date
                val currentDate = java.util.Calendar.getInstance().time.toString()

                try{
                //Save on DB
                val bookingID = database.child("bookings").push().key
                val booking = Bookings(bookingID.toString(), empID.toString(), userID.toString(), orderDate.toString(),  currentDate.toString(), address.toString(), info.toString(), false,false)
                database.child("bookings").child(bookingID.toString()).setValue(booking)
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
                }
                catch (e: Exception){
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
                }
                //Go to home
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        btnBack.setOnClickListener(){
            finish()
        }
    }
}