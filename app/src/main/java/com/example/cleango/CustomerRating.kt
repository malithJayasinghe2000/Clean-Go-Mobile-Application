package com.example.cleango

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.cleango.data.model.JobRating
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CustomerRating : AppCompatActivity() {

    // Initialize Firebase Authentication
    private val auth = FirebaseAuth.getInstance()
    private val employeeID: String? = auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        // Initialize UI elements
        val messageEditText = findViewById<EditText>(R.id.feedback_message)
        val btnSubmit = findViewById<Button>(R.id.feedback_submit)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val dp = findViewById<ImageView>(R.id.dp)

        // Get data from the intent's extras
        val bundle = intent.extras
        val customerID = bundle?.getString("customerID")
        val bookingID = bundle?.getString("bookingID")
        val image = bundle?.getString("dp")

        // Load the customer's image using Glide if available
        if (image != null) {
            Glide.with(this)
                .load(image).circleCrop()
                .into(dp)
        }

        // Set a click listener for the submit button
        btnSubmit.setOnClickListener {
            val message = messageEditText.text.toString()
            val rating = ratingBar.rating.toDouble()

            // Check for empty feedback message
            if (message.isEmpty()) {
                messageEditText.error = "Feedback is required"
                messageEditText.requestFocus()
            } else if (rating == 0.0) {
                Toast.makeText(this, "Please rate the service", Toast.LENGTH_SHORT).show()
            } else {
                // Initialize the Firebase Realtime Database
                val database = FirebaseDatabase.getInstance().reference
                val feedbackRef = database.child("customerRating").push()

                // Create a JobRating object to store feedback data
                val jobRating = JobRating(
                    id = feedbackRef.key.toString(),
                    jobID = bookingID.orEmpty(),
                    employeeID = employeeID.orEmpty(),
                    customerID = customerID.orEmpty(),
                    rating = rating,
                    comment = message,
                    date = getCurrentDate()
                )

                // Push jobRating to the database
                feedbackRef.setValue(jobRating)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Feedback submitted", Toast.LENGTH_SHORT).show()
                        updateRatingInUserDatabase(employeeID, rating)
                        // Start the main activity and finish the current activity
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error submitting feedback", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Function to get the current date
    private fun getCurrentDate(): String {
        val calendar = java.util.Calendar.getInstance()
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val year = calendar.get(java.util.Calendar.YEAR)

        return "$day/$month/$year"
    }

    // Function to update the customer's rating in the user database
    private fun updateRatingInUserDatabase(customerID: String?, newRating: Double) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(customerID.orEmpty())
        userRef.get().addOnSuccessListener { snapshot ->
            val existingRating = snapshot.child("rating").value.toString().toDouble()
            val totalRatings = snapshot.child("ratingCount").value.toString().toDouble()
            val updatedTotalRatings = totalRatings + 1
            val updatedRating = ((existingRating * totalRatings) + newRating) / updatedTotalRatings

            //Round to 2 decimal places
            val roundedRating = String.format("%.1f", updatedRating).toDouble()

            // Update the customer's rating and rating count in the database
            userRef.child("rating").setValue(roundedRating)
            userRef.child("ratingCount").setValue(updatedTotalRatings)
        }.addOnFailureListener {
            Toast.makeText(this, "Error updating rating: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
