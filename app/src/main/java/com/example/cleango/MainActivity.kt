package com.example.cleango

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cleango.data.model.Employee
import com.example.cleango.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserID = currentUser?.uid.toString()

        fetchDataFromFirebase(currentUserID) { userType ->
            if (userType.isNotEmpty()) {
                Log.d("userType", userType)

                val navView: BottomNavigationView = binding.navView

                if (userType == "Customer")
                    navView.menu.findItem(R.id.navigation_jobs).isVisible = false

                if (userType == "Employee")
                    navView.menu.findItem(R.id.navigation_dashboard).isVisible = false

                // Save in shared preferences
                val sharedPref = this.getPreferences(MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("userType", userType)
                    commit()
                }

                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                val appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.navigation_home,
                        R.id.navigation_dashboard,
                        R.id.navigation_notifications,
                        R.id.navigation_jobs
                    )
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
            } else {
                // Handle the case when userType is empty
            }
        }
    }

    private fun fetchDataFromFirebase(currentUserID: String, callback: (String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("users").child(currentUserID)
        ref.get().addOnSuccessListener {
            val userType = it.child("userType").value?.toString() ?: ""
            Log.d("userTypeX", userType)
            callback(userType)
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            callback("")
        }
    }
}
