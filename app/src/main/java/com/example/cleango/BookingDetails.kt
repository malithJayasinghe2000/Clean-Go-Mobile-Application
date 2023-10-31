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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class BookingDetails : AppCompatActivity() {

    private lateinit var dp_image: ImageView
    private lateinit var title: TextView
    private lateinit var employeeName: TextView
    private lateinit var service_price: TextView
    private lateinit var serviceArea: TextView
    private lateinit var itemDescription: TextView
    private lateinit var emp_phoneNumber: TextView
    private lateinit var bookingInfo: TextView
    private lateinit var dateEditText: EditText
    private lateinit var bookingAddressEditText: EditText
    private lateinit var bookingInfoEditText: EditText
    private lateinit var ratingButton: Button
    private lateinit var cancelButton: Button
    private lateinit var txtStatus: TextView
    private lateinit var ratings: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)

        //Get data from intent
        val bundle = intent.extras
        val empID = bundle!!.getString("empID")
        val name = bundle.getString("name")
        val jobTitle = bundle.getString("jobTitle")
        val price = bundle.getString("price")
        val rating = bundle.getString("rating")
        val serviceCategory = bundle.getString("serviceCategory")
        val description = bundle.getString("description")
        val image = bundle.getString("dp")
        val area = bundle.getString("area")
        val phoneNumber = bundle.getString("phoneNumber")
        val email = bundle.getString("email")
        val accepted = bundle.getString("accepted")
        val completed = bundle.getString("completed")
        val bookingID = bundle.getString("orderID")
        val info = bundle.getString("info")
        val view = bundle.getString("view")

        if (bookingID != null) {
            Log.d("Booking", bookingID)
        }

        dp_image = findViewById(R.id.image)
        title = findViewById(R.id.title)
        employeeName = findViewById(R.id.employee_name)
        service_price = findViewById(R.id.price)
        serviceArea = findViewById(R.id.service_area)
        itemDescription = findViewById(R.id.item_description)
        emp_phoneNumber = findViewById(R.id.phone)
        bookingInfo = findViewById(R.id.booking)
        dateEditText = findViewById(R.id.date)
        bookingAddressEditText = findViewById(R.id.booking_address)
        bookingInfoEditText = findViewById(R.id.booking_info)
        ratingButton = findViewById(R.id.rate)
        txtStatus = findViewById(R.id.status)
        cancelButton = findViewById(R.id.cancel)
        ratings = findViewById(R.id.ratings)



        if(accepted == "true" && completed == "false") {
            txtStatus.text = "Status: Accepted"
        } else if(accepted == "true" && completed == "true") {
            txtStatus.text = "Status: Completed"
        } else {
            txtStatus.text = "Status: Pending"
        }


        //Set data to views
        title.text = jobTitle
        employeeName.text = name
        service_price.text = "$" + price
        serviceArea.text = area
        itemDescription.text = description
        emp_phoneNumber.text = phoneNumber
        ratings.text = rating + " Rating"

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
            val intent = Intent(this, Rating::class.java)
            intent.putExtra("empID", empID)
            intent.putExtra("bookingID", bookingID)
            intent.putExtra("dp", image)
            startActivity(intent)
        }

            cancelButton.setOnClickListener {
            //Remove from realtime database

                //Prompt user
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Cancel Booking")
                builder.setMessage("Are you sure you want to cancel this booking?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    //Remove from realtime database
                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("bookings").child(bookingID.toString())
                    myRef.removeValue()
                    //Go back to dashboard
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                   builder.setNegativeButton("No") { dialog, which ->
                        //Do nothing
                    }

                builder.show()
        }
    }

}