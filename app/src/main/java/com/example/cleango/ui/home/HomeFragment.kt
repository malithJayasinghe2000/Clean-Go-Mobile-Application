package com.example.cleango.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cleango.Booking
import com.example.cleango.R
import com.example.cleango.data.model.Employee
import com.example.cleango.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var categorySpinner: Spinner

    private lateinit var recyclerView: RecyclerView
    private lateinit var userType: String

    //Current user ID
    private val auth = FirebaseAuth.getInstance()
    var userID = auth.currentUser?.uid;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.emp_card_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        categorySpinner = root.findViewById(R.id.categorySpinner)
        val categoryOptions = resources.getStringArray(R.array.category_options)
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryOptions)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter


        // Set a listener for category selection changes
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Fetch data from Firebase based on the selected category
                fetchDataFromFirebase(categoryOptions[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Fetch data from Firebase Realtime Database and set it to the RecyclerView
        fetchDataFromFirebase("All")
        return root
    }

    private fun fetchDataFromFirebase(category: String) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = mutableListOf<Employee>()

//                userType = dataSnapshot.child(userID.toString()).child("userType").value.toString()
//
//                //Add to shared preferences
//                val sharedPref = requireActivity().getSharedPreferences("sharedPref", 0)
//                val editor = sharedPref.edit()
//                editor.putString("userType", userType)
//                editor.apply()

                for (snapshot in dataSnapshot.children) {

                    if(userID.toString() == snapshot.key.toString()){
                        continue
                    }

                    if(snapshot.child("userType").value.toString() == "Customer"){
                        continue
                    }

                    val employee = snapshot.getValue(Employee::class.java)
                    if (category == "All") {
                        dataList.add(employee!!)
                    } else if (employee?.serviceCategory == category) {
                        dataList.add(employee!!)
                    }
                }

                dataList.sortBy { it.rating }

                val cardAdapter = CardAdapter(dataList)
                recyclerView.adapter = cardAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handle db error
                Toast.makeText(requireContext(), "Error getting data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class CardAdapter(private val dataList: List<Employee>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.employee_card, parent, false)
            return CardViewHolder(view)
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            val data = dataList[position]
            // Bind data to the CardView
            holder.itemView.findViewById<TextView>(R.id.emp_name).text = data.name
            holder.itemView.findViewById<TextView>(R.id.job_title).text = data.jobTitle
            holder.itemView.findViewById<TextView>(R.id.price).text = "$" + data.price
            holder.itemView.findViewById<TextView>(R.id.rating).text = data.rating.toString() + " Ratings"
            //Display image using glide
            Glide.with(holder.itemView.context).load(data.dp).into(holder.itemView.findViewById(R.id.dp))

            // Set a listener for the CardView
            holder.itemView.setOnClickListener {
             //Pass data to new activity
                val bundle = Bundle()
                bundle.putString("empID", data.id)
                bundle.putString("name", data.name)
                bundle.putString("jobTitle", data.jobTitle)
                bundle.putString("price", data.price)
                bundle.putString("rating", data.rating.toString())
                bundle.putString("serviceCategory", data.serviceCategory)
                bundle.putString("description", data.jobDescription)
                bundle.putString("image", data.dp)
                bundle.putString("area", data.serviceArea)
                bundle.putString("phoneNumber", data.phoneNumber)
                bundle.putString("email", data.email)
                bundle.putString("userType", data.userType)


                //Pass to a activity
                val intent = androidx.core.content.ContextCompat.startActivity(holder.itemView.context, Intent(holder.itemView.context, Booking::class.java).putExtras(bundle), null)
            }
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}
