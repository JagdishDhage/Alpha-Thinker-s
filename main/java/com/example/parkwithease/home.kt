package com.example.parkwithease

import android.content.Intent
import android.graphics.Outline
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class home : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize views
        val profileIcon: ImageView = findViewById(R.id.profileIcon)
        val appName: TextView = findViewById(R.id.appName)
        val instantBookingButton: Button = findViewById(R.id.instantBookingButton)
        val preBookingButton: Button = findViewById(R.id.preBookingButton)
        val imageView: ImageView = findViewById(R.id.imageView)
        val addVehicleButton: Button = findViewById(R.id.addVehicleButton)
        val PostParking: Button = findViewById(R.id.PostParking)
        val socialparking: Button = findViewById(R.id.socialparking)

        // Initialize the userNameTextView with the TextView from the layout
        userNameTextView = findViewById(R.id.profileName)

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Set up spinner for cities


        // Set up button for adding vehicle
        addVehicleButton.setOnClickListener {
            // Navigate to AddVehicle activity
            startActivity(Intent(this, AddVehicle::class.java))
        }

        PostParking.setOnClickListener {
            // Navigate to AddVehicle activity
            startActivity(Intent(this, Availablepark::class.java))
        }
        socialparking.setOnClickListener {
            // Navigate to AddVehicle activity
            startActivity(Intent(this, SocietyParking::class.java))
        }
        instantBookingButton.setOnClickListener {
            // Navigate to AddVehicle activity
            startActivity(Intent(this, Booking::class.java))
        }
        preBookingButton.setOnClickListener {
            // Navigate to AddVehicle activity
            startActivity(Intent(this, PreBooking::class.java))
        }


        // Set up image buttons for navigation
        imageView.outlineProvider = RoundedOutlineProvider(12f)
        imageView.clipToOutline = true

        // Set up bottom navigation view
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    // Handle click on the Home icon
                    // You can either do nothing if you're already on the home fragment
                    // or navigate to the home fragment if you're not
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

        // Load user's data
        loadUserData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.homeFragment -> {
                // Handle click on the Home icon
                true
            }
            R.id.vehiclesFragment -> {
                // Handle click on the Vehicles icon
                startActivity(Intent(this, allvehicles::class.java))
                true
            }
            R.id.bookingFragment -> {
                // Handle click on the Booking icon
                startActivity(Intent(this, Booking::class.java))
                true
            }
            R.id.settingsFragment -> {
                // Handle click on the Settings icon
                startActivity(Intent(this, profile::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadUserData() {
        // Get current user's ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        // Retrieve user's document from Firestore
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Retrieve username from document
                        val username = document.getString("username")

                        // Set username to TextView
                        userNameTextView.text = username
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    println("Error getting documents: $exception")
                }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.custom_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            // Handle menu item clicks
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle click on Home
                    startActivity(Intent(this, allvehicles::class.java))
                    true
                }

                // Add more cases for other menu items if needed
                else -> false
            }
        }

        popupMenu.show()
    }

    class RoundedOutlineProvider(private val cornerRadius: Float) : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
        }
    }
}
