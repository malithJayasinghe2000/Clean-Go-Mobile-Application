package com.example.cleango

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


//Get current user
val currentUser = FirebaseAuth.getInstance().currentUser
val employeeID = currentUser?.uid

class BookingDetailsEmpView : AppCompatActivity() {

    private lateinit var dp_image: ImageView
    private lateinit var employeeName: TextView
    private lateinit var emp_phoneNumber: TextView
    private lateinit var dateEditText: EditText
    private lateinit var bookingAddressEditText: EditText
    private lateinit var bookingInfoEditText: EditText
    private lateinit var ratingButton: Button
    private lateinit var txtStatus: TextView
    private lateinit var ratings: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details_emp_view)

        //Get data from intent
        val bundle = intent.extras
        val customerID = bundle!!.getString("customerID")
        val name = bundle.getString("name")
        val image = bundle.getString("dp")
        val phoneNumber = bundle.getString("phoneNumber")
        val accepted = bundle.getString("accepted")
        val completed = bundle.getString("completed")
        val bookingID = bundle.getString("orderID")
        val rating = bundle.getString("rating")

        if (bookingID != null) {
            Log.d("Booking", bookingID)
        }

        dp_image = findViewById(R.id.image)
        employeeName = findViewById(R.id.employee_name)
        emp_phoneNumber = findViewById(R.id.phone)
        dateEditText = findViewById(R.id.date)
        bookingAddressEditText = findViewById(R.id.booking_address)
        bookingInfoEditText = findViewById(R.id.booking_info)
        ratingButton = findViewById(R.id.rate)
        txtStatus = findViewById(R.id.status)
        ratings = findViewById(R.id.ratings)

        //Set data to views
        employeeName.text = name
        emp_phoneNumber.text = phoneNumber
        ratings.text = rating + " Ratings"

        if(accepted == "false") {
            txtStatus.text = "Status: Pending"
        } else if (accepted == "true" && completed == "false") {
            txtStatus.text = "Status: Accepted"
        } else if (accepted == "true" && completed == "true") {
            txtStatus.text = "Status: Completed"
        }

        dateEditText.setText(bundle.getString("orderDate"))
        bookingAddressEditText.setText(bundle.getString("orderAddress"))
        bookingInfoEditText.setText(bundle.getString("info"))

        //Disable edit texts
        dateEditText.isEnabled = false
        bookingAddressEditText.isEnabled = false
        bookingInfoEditText.isEnabled = false

        if(image != "") {
            Glide.with(this).load(image).into(dp_image)
        }

        ratingButton.setOnClickListener {
            val intent = Intent(this, CustomerRating::class.java)
            intent.putExtra("empID", employeeID)
            intent.putExtra("customerID", customerID)
            intent.putExtra("bookingID", bookingID)
            intent.putExtra("dp", image)
            startActivity(intent)
        }

    }

}