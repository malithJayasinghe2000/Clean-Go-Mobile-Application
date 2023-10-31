package com.example.cleango

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cleango.data.model.Customer
import com.example.cleango.data.model.Employee
import com.example.cleango.data.model.JobRating
import com.example.cleango.data.model.RatingCombined
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllRatings : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    //Current userID
    private val auth = FirebaseAuth.getInstance()
    var userID = auth.currentUser?.uid;
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_ratings)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getUserType()

    }

    //Get user type from current userID
    private fun getUserType(){
        val database = FirebaseDatabase.getInstance()

        val userTypeReference = database.getReference("users").child(userID.toString()).child("userType")
        userTypeReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userType = snapshot.value as? String
                Log.d("UserTypeNew", userType.toString()   )
                userType?.let {
                    if (it == "Customer") {
                        fetchDataFromCustomerFirebase()
                    } else if (it == "Employee") {
                        fetchDataFromEmployeeFirebase()
                    } else {
                        Log.d("UserType", "User type not found")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun fetchDataFromCustomerFirebase() {
        val bookingsReference = database.getReference("customerRating") // Reference to bookings node
        val employeesReference = database.getReference("users") // Reference to employees node

        bookingsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = mutableListOf<RatingCombined>()

                for (bookingSnapshot in dataSnapshot.children) {
                    val ratings = bookingSnapshot.getValue(JobRating::class.java)
                    val employeeID = ratings?.employeeID
                    val customerID = ratings?.customerID

                    if(customerID != userID) continue

                    // Fetch employee details based on employeeID
                    employeesReference.child(customerID!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(employeeSnapshot: DataSnapshot) {
                            val customer = employeeSnapshot.getValue(Customer::class.java)

                            // Fetch customer details based on customerID
                            employeesReference.child(employeeID!!).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(customerSnapshot: DataSnapshot) {
                                    val employee = customerSnapshot.getValue(Employee::class.java)

                                    // Combine employee and customer details into BookingCombined object
                                    val ratingCombined = RatingCombined(ratings, employee, customer)
                                    dataList.add(ratingCombined)

                                    // Set up RecyclerView adapter after combining data
                                    val cardAdapter = CardAdapter(dataList)
                                    recyclerView.adapter = cardAdapter
                                }

                                override fun onCancelled(customerError: DatabaseError) {
                                    // Handle db error for customers
                                }
                            })
                        }

                        override fun onCancelled(employeeError: DatabaseError) {
                            // Handle db error for employees
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle db error for bookings
            }
        })
    }

    private fun fetchDataFromEmployeeFirebase() {
        val bookingsReference = database.getReference("empRating") // Reference to bookings node
        val employeesReference = database.getReference("users") // Reference to employees node

        bookingsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = mutableListOf<RatingCombined>()

                for (bookingSnapshot in dataSnapshot.children) {
                    val ratings = bookingSnapshot.getValue(JobRating::class.java)
                    val employeeID = ratings?.employeeID
                    val customerID = ratings?.customerID

                    if(employeeID != userID) continue

                    // Fetch employee details based on employeeID
                    employeesReference.child(employeeID!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(employeeSnapshot: DataSnapshot) {
                            val employee = employeeSnapshot.getValue(Employee::class.java)

                            // Fetch customer details based on customerID
                            employeesReference.child(customerID!!).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(customerSnapshot: DataSnapshot) {
                                    val customer = customerSnapshot.getValue(Customer::class.java)

                                    // Combine employee and customer details into BookingCombined object
                                    val ratingCombined = RatingCombined(ratings, employee, customer)
                                    dataList.add(ratingCombined)

                                    // Set up RecyclerView adapter after combining data
                                    val cardAdapter = CardAdapter(dataList)
                                    recyclerView.adapter = cardAdapter
                                }

                                override fun onCancelled(customerError: DatabaseError) {
                                    // Handle db error for customers
                                }
                            })
                        }

                        override fun onCancelled(employeeError: DatabaseError) {
                            // Handle db error for employees
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle db error for bookings
            }
        })
    }

    class CardAdapter(private val dataList: List<RatingCombined>) :
        RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.ratings_card, parent, false)
            return CardViewHolder(view)
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            val data = dataList[position]

            // Bind data to your ViewHolder views here
            holder.itemView.findViewById<TextView>(R.id.emp_name).text = data.employee?.name ?: ""
            holder.itemView.findViewById<RatingBar>(R.id.ratingBar).rating = data.rating?.rating?.toFloat() ?: 0.0F
            holder.itemView.findViewById<RatingBar>(R.id.ratingBar).isEnabled = false
            holder.itemView.findViewById<TextView>(R.id.comment).text = data.rating?.comment ?: ""

            //Load with glide
            Glide.with(holder.itemView.context).load(data.employee?.dp).circleCrop().into(holder.itemView.findViewById<ImageView>(R.id.dp))

        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // Define your ViewHolder views here
        }
    }
}
