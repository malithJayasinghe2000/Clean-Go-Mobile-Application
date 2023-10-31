package com.example.cleango.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.cleango.AllRatings
import com.example.cleango.Home
import com.example.cleango.data.model.Employee
import com.example.cleango.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Current user ID
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val currentUserID = currentUser?.uid


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fetchDataFromFirebase()

        binding.signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Home::class.java)
            startActivity(intent)
        }


        binding.resetPasswordText.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(currentUser?.email.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Email sent", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.btnRatings.setOnClickListener {
            val intent = Intent(requireContext(), AllRatings::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchDataFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users").child(currentUserID.toString()) // Replace "users" with your actual database path

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val employee = snapshot.getValue(Employee::class.java)

                // Update the UI here with the employee object
                binding.profileName.setText(employee?.name)
                binding.profileEmail.setText(employee?.email)
                binding.profilePhone.setText(employee?.phoneNumber)

                binding.profileName.isEnabled = false
                binding.profileEmail.isEnabled = false
                binding.profilePhone.isEnabled = false

                //display image using glide
                Glide.with(requireContext())
                    .load(employee?.dp).circleCrop()
                    .into(binding.profilePicture)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to read value.", Toast.LENGTH_SHORT).show()
            }
        })


    }
}