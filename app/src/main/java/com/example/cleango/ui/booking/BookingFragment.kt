package com.example.cleango.ui.booking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cleango.BookingDetails
import com.example.cleango.R
import com.example.cleango.data.model.BookingCombined
import com.example.cleango.data.model.Bookings
import com.example.cleango.data.model.Employee
import com.example.cleango.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var categorySpinner: Spinner

    private lateinit var recyclerView: RecyclerView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Current user
    private val auth = FirebaseAuth.getInstance()
    var userID = auth.currentUser?.uid;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.ongoing_job_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        fetchDataFromFirebase()
        return root
    }

    private fun fetchDataFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val bookingsReference = database.getReference("bookings") // Reference to bookings node
        val employeesReference = database.getReference("users") // Reference to employees node

        bookingsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = mutableListOf<BookingCombined>()

                for (bookingSnapshot in dataSnapshot.children) {

                    val booking = bookingSnapshot.getValue(Bookings::class.java)

                    if(booking?.customerID != userID ){
                       continue
                    }

                    if( booking?.completed == true) {
                        continue
                    }

                    val employeeID = booking?.employeeID

                    // Fetch employee details based on employeeID
                    employeesReference.child(employeeID!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(employeeSnapshot: DataSnapshot) {
                            val employee = employeeSnapshot.getValue(Employee::class.java)
                            // Combine booking and employee details if needed
                             val combinedData = combineData(booking, employee)
                             dataList.add(combinedData)

                            val cardAdapter = CardAdapter(dataList)
                            recyclerView.adapter = cardAdapter

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle db error for employees
                            Toast.makeText(requireContext(), "Error getting data", Toast.LENGTH_SHORT).show()
                        }
                    })

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle db error for bookings
                Toast.makeText(requireContext(), "Error getting data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class CardAdapter(private val dataList: List<BookingCombined>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.booking_card, parent, false)
            return CardViewHolder(view)
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            val data = dataList[position]

            // Bind data to the CardView
            holder.itemView.findViewById<TextView>(R.id.job_title).text =
                data.employee?.jobTitle ?: "Job title";
            holder.itemView.findViewById<TextView>(R.id.job_date).text = data.booking?.orderDate ?: "Date"
            holder.itemView.findViewById<TextView>(R.id.area).text = data.employee?.serviceArea ?: "Area"
            //Display image using glide
            if(data.employee?.dp != "") {
                Glide.with(holder.itemView.context)
                    .load(data.employee?.dp).circleCrop()
                    .into(holder.itemView.findViewById(R.id.dp))
            }

            // Set a listener for the CardView
            holder.itemView.setOnClickListener {
                //Pass data to new activity
                val bundle = Bundle()
                bundle.putString("empID", data.employee?.id)
                bundle.putString("name", data.employee?.name)
                bundle.putString("jobTitle", data.employee?.jobTitle)
                bundle.putString("price", data.employee?.price)
                bundle.putString("rating", data.employee?.rating.toString())
                bundle.putString("dp", data.employee?.dp)
                bundle.putString("serviceCategory", data.employee?.serviceCategory)
                bundle.putString("description", data.employee?.jobDescription)
                bundle.putString("area", data.employee?.serviceArea)
                bundle.putString("phoneNumber", data.employee?.phoneNumber)
                bundle.putString("email", data.employee?.email)
                bundle.putString("orderDate", data.booking?.orderDate)
                bundle.putString("orderAddress", data.booking?.address)
                bundle.putString("info", data.booking?.info)
                bundle.putString("accepted", data.booking?.accepted.toString())
                bundle.putString("completed", data.booking?.completed.toString())
                bundle.putString("orderID", data.booking?.bookingID)


                //Pass to a activity
                val intent = androidx.core.content.ContextCompat.startActivity(holder.itemView.context, Intent(holder.itemView.context, BookingDetails::class.java).putExtras(bundle), null)
            }
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    private fun combineData(booking: Bookings?, employee: Employee?): BookingCombined {
        return BookingCombined(booking, employee)
    }

}