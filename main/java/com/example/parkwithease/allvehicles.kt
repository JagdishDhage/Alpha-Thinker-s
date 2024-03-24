package com.example.parkwithease

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class allvehicles : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allvehicles)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    // Handle click on the Home icon
                    // You can either do nothing if you're already on the home fragment
                    // or navigate to the home fragment if you're not
                    startActivity(Intent(this, home::class.java))
                    true
                }
                R.id.vehiclesFragment -> {
                    // Handle click on the Vehicles icon
                    // Navigate to the Vehicles fragment or activity
                    startActivity(Intent(this, allvehicles::class.java))
                    true
                }
                R.id.bookingFragment -> {
                    // Handle click on the Booking icon
                    // Navigate to the Booking fragment or activity
                    startActivity(Intent(this, Booking::class.java))
                    true
                }
                R.id.settingsFragment -> {
                    // Handle click on the Settings icon
                    // Navigate to the Settings fragment or activity
                    startActivity(Intent(this, profile::class.java))
                    true
                }
                else -> false
            }
        }

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Load vehicle data
        loadVehicleData()
    }

    private fun loadVehicleData() {
        // Get current user's ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        // Retrieve user's vehicles from Firestore
        if (userId != null) {
            firestore.collection("users").document(userId)
                .collection("vehicles")
                .get()
                .addOnSuccessListener { documents ->
                    // Initialize vehicle variables
                    var vehicleName1: String? = null
                    var vehicleNumber1: String? = null
                    var vehicleName2: String? = null
                    var vehicleNumber2: String? = null

                    // Loop through each document
                    var i = 0
                    for (document in documents) {
                        // Retrieve vehicle name and number from document
                        val vehicleName = document.getString("name")
                        val vehicleNumber = document.getString("number")

                        // Assign vehicle data to variables based on iteration
                        if (i == 0) {
                            vehicleName1 = vehicleName
                            vehicleNumber1 = vehicleNumber
                        } else if (i == 1) {
                            vehicleName2 = vehicleName
                            vehicleNumber2 = vehicleNumber
                            // If there are more than 2 vehicles, break the loop
                            break
                        }
                        i++
                    }

                    // Update UI with vehicle data
                    updateUI(vehicleName1, vehicleNumber1, vehicleName2, vehicleNumber2)
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    println("Error getting documents: $exception")
                }
        }
    }

    private fun updateUI(vehicleName1: String?, vehicleNumber1: String?, vehicleName2: String?, vehicleNumber2: String?) {
        // Find the vehicleCard1 and vehicleCard2 CardViews
        val vehicleCard1: CardView = findViewById(R.id.vehicleCard1)
        val vehicleCard2: CardView = findViewById(R.id.vehicleCard2)

        // Find TextViews within the vehicleCard1 CardView
        val vehicleNameTextView1: TextView = vehicleCard1.findViewById(R.id.vehicleName1)
        val vehicleNumberTextView1: TextView = vehicleCard1.findViewById(R.id.vehicleNumber1)

        // Set vehicle name and number to TextViews in the first card
        vehicleNameTextView1.text = vehicleName1
        vehicleNumberTextView1.text = vehicleNumber1

        // Find TextViews within the vehicleCard2 CardView
        val vehicleNameTextView2: TextView = vehicleCard2.findViewById(R.id.vehicleName2)
        val vehicleNumberTextView2: TextView = vehicleCard2.findViewById(R.id.vehicleNumber2)

        // Set vehicle name and number to TextViews in the second card
        vehicleNameTextView2.text = vehicleName2
        vehicleNumberTextView2.text = vehicleNumber2
    }

}
