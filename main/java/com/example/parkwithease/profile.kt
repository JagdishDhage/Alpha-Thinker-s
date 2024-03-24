package com.example.parkwithease

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
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
}