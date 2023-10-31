package com.example.cleango

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class Booking : AppCompatActivity() {

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
    private lateinit var bookButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        //Get data from intent
        val bundle = intent.extras
        val empID = bundle!!.getString("empID")
        val name = bundle.getString("name")
        val jobTitle = bundle.getString("jobTitle")
        val price = bundle.getString("price")
        val rating = bundle.getString("rating")
        val serviceCategory = bundle.getString("serviceCategory")
        val description = bundle.getString("description")
        val image = bundle.getString("image")
        val area = bundle.getString("area")
        val phoneNumber = bundle.getString("phoneNumber")
        val email = bundle.getString("email")


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
        bookButton = findViewById(R.id.book)
        cancelButton = findViewById(R.id.cancel)

        dateEditText.showSoftInputOnFocus = false

        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        //Get current user ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val customerID = currentUser?.uid


        //Get user type from database
        val database = FirebaseDatabase.getInstance()
        val userTypeReference = database.getReference("users").child(customerID.toString()).child("userType")

        if(userTypeReference != null) {
            userTypeReference.get().addOnSuccessListener {
                val userType = it.value as? String
                userType?.let {
                    if (it == "Employee") {
                        bookingInfoEditText.visibility = EditText.GONE
                        bookingAddressEditText.visibility = EditText.GONE
                        dateEditText.visibility = EditText.GONE
                        bookButton.visibility = Button.GONE
                        cancelButton.visibility = Button.GONE
                    }else{
                        bookingInfoEditText.visibility = EditText.VISIBLE
                        bookingAddressEditText.visibility = EditText.VISIBLE
                        dateEditText.visibility = EditText.VISIBLE
                        bookButton.visibility = Button.VISIBLE
                        cancelButton.visibility = Button.VISIBLE
                    }
                }
            }
        }


        //Set data to views
        title.text = jobTitle
        employeeName.text = name
        service_price.text = "$" + price
        serviceArea.text = area
        itemDescription.text = description
        emp_phoneNumber.text = phoneNumber
        if(image != "") {
            Glide.with(this).load(image).into(dp_image)
        }

        fun validateData(date:String, bookingAddress:String, bookingInfo:String): Boolean {
            //Validate data
            var validated = true

            // Validate date
            if (date.isEmpty()) {
                dateEditText.error = "Date is required"
                dateEditText.requestFocus()
                validated = false
            }

            // Validate booking address
            else if (bookingAddress.isEmpty()) {
                bookingAddressEditText.error = "Booking address is required"
                bookingAddressEditText.requestFocus()
                validated = false
            }

            // Validate booking info
            else if (bookingInfo.isEmpty()) {
                bookingInfoEditText.error = "Booking info is required"
                bookingInfoEditText.requestFocus()
                validated = false
            }

            return validated
        }

        //Booking button click
        bookButton.setOnClickListener(){

            //Get data from edit texts
            val date = dateEditText.text.toString().trim()
            val bookingAddress = bookingAddressEditText.text.toString().trim()
            val bookingInfo = bookingInfoEditText.text.toString().trim()

            if (validateData(date, bookingAddress, bookingInfo)) {

                //Pass data to new activity
                val bundle = Bundle()
                bundle.putString("empID", empID)
                bundle.putString("date", date)
                bundle.putString("bookingAddress", bookingAddress)
                bundle.putString("bookingInfo", bookingInfo)

                //Pass to a activity
                val intent = androidx.core.content.ContextCompat.startActivity(
                    this,
                    Intent(this, Payment::class.java).putExtras(bundle),
                    null
                )
            }
        }

        //Cancel button click
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // Handle the date set by the user
                val selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                dateEditText.setText(selectedDate)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Set the minimum date to the current date
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

}