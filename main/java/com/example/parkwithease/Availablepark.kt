package com.example.parkwithease

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class Availablepark : AppCompatActivity() {

    private lateinit var locationInputLayout: TextInputLayout
    private lateinit var parkingNameInputLayout: TextInputLayout
    private lateinit var vehicleTypeSpinner: Spinner
    private lateinit var priceInputLayout: TextInputLayout
    private lateinit var otherDetailsInputLayout: TextInputLayout
    private lateinit var uploadButton: Button
    private lateinit var cityInputLayout: TextInputLayout // Added initialization for cityInputLayout
    private val TAG = "Availablepark"
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_availablepark)

        // Initialize views
        locationInputLayout = findViewById(R.id.locationInputLayout)
        parkingNameInputLayout = findViewById(R.id.parkingNameInputLayout)
        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner)
        priceInputLayout = findViewById(R.id.priceInputLayout)
        otherDetailsInputLayout = findViewById(R.id.otherDetailsInputLayout)
        uploadButton = findViewById(R.id.uploadButton)
        cityInputLayout = findViewById(R.id.cityInputLayout) // Initialize cityInputLayout

        // Populate vehicle type spinner
        val vehicleTypes = listOf("Car", "Motorcycle", "Bicycle", "Others")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vehicleTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vehicleTypeSpinner.adapter = spinnerAdapter

        // Set click listener for upload button
        uploadButton.setOnClickListener {
            uploadParkingDetails()
        }
    }

    private fun uploadParkingDetails() {
        val location = locationInputLayout.editText?.text.toString()
        val parkingName = parkingNameInputLayout.editText?.text.toString()
        val vehicleType = vehicleTypeSpinner.selectedItem.toString()
        val price = priceInputLayout.editText?.text.toString()
        val otherDetails = otherDetailsInputLayout.editText?.text.toString()
        val cityInput = cityInputLayout.editText?.text.toString() // Changed variable name to avoid conflict with lateinit property

        // Validate input fields (you can add your validation logic here)

        // Store details in Firestore
        val societyName = parkingName // Set society name as parking name
        val parkingDetails = hashMapOf(
            "city" to cityInput,
            "location" to location,
            "OrganizationName" to parkingName,
            "vehicleType" to vehicleType,
            "price" to price,
            "Nearest-Mall" to otherDetails
        )

        // Store details in Firestore
        firestore.collection("Other-Park").document(cityInput).collection(otherDetails).document(societyName)
            .set(parkingDetails)
            .addOnSuccessListener {
                // Data stored successfully
                Toast.makeText(this@Availablepark, "Parking details uploaded successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, home::class.java))
            }
            .addOnFailureListener { e ->
                // Error occurred while storing data
                Toast.makeText(this@Availablepark, "Failed to upload parking details. Please try again.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error uploading parking details", e)
            }

    }
}
