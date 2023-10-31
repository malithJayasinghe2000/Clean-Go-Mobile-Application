package com.example.cleango

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.cleango.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    private fun signIn(email:String, password:String): String? {

        var userId: String? = null

        // Sign in with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        Log.d("ID", user.uid, task.exception)
                        userId = user.uid
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@Login, "Incorrect Email or Password", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        return userId
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val btnRegister = findViewById<Button>(R.id.login_button)
        val signUp = findViewById<TextView>(R.id.signUp)
        val forgotPassword = findViewById<TextView>(R.id.forgotPass)

        forgotPassword.setOnClickListener(){
          //Firebase forgot password
            val email = emailEditText.text.toString().trim();
            if (email.isEmpty()) {
                emailEditText.error = "Email is required"
                emailEditText.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Please enter a valid email address"
                emailEditText.requestFocus()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Email sent.")
                            Toast.makeText(this@Login, "Email sent", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }


        btnRegister.setOnClickListener() {

            var validated = true

            //Get values from user input
            val email = emailEditText.text.toString().trim();
            val password = passwordEditText.text.toString().trim();

            // Validate email
            if (email.isEmpty()) {
                emailEditText.error = "Email is required"
                emailEditText.requestFocus()
                validated = false

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Please enter a valid email address"
                emailEditText.requestFocus()
                validated = false
            }

            // Validate password
            else if (password.isEmpty()) {
                passwordEditText.error = "Password is required"
                passwordEditText.requestFocus()
                validated = false

            } else if (password.length < 6) {
                passwordEditText.error = "Password should be at least 6 characters "
                passwordEditText.requestFocus()
                validated = false
            }

            if(validated){
                signIn(email, password)
            }

        }

        signUp.setOnClickListener(){
            val intent = Intent(this, RegisterEmployee::class.java)
            startActivity(intent)
        }
    }
}