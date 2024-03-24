package com.example.parkwithease

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView // Import TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddVehicle : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val editTextVehicleName = findViewById<EditText>(R.id.editTextVehicleName)
        val editTextVehicleNumber = findViewById<EditText>(R.id.editTextVehicleNumber)
        val editTextVehicleBrand = findViewById<EditText>(R.id.editTextVehicleBrand)
        val editTextVehicleColor = findViewById<EditText>(R.id.editTextVehicleColor)
        val buttonAddVehicle = findViewById<Button>(R.id.buttonAddVehicle)

        buttonAddVehicle.setOnClickListener {
            val vehicleName = editTextVehicleName.text.toString().trim()
            val vehicleNumber = editTextVehicleNumber.text.toString().trim()
            val vehicleBrand = editTextVehicleBrand.text.toString().trim()
            val vehicleColor = editTextVehicleColor.text.toString().trim()

            if (vehicleName.isNotEmpty() && vehicleNumber.isNotEmpty() &&
                vehicleBrand.isNotEmpty() && vehicleColor.isNotEmpty()) {
                addVehicleToFirestore(vehicleName, vehicleNumber, vehicleBrand, vehicleColor)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addVehicleToFirestore(name: String, number: String, brand: String, color: String) {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val vehicleData = hashMapOf(
                "name" to name,
                "number" to number,
                "brand" to brand,
                "color" to color
            )

            firestore.collection("users").document(userId)
                .collection("vehicles").add(vehicleData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Vehicle added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add vehicle: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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
                        val username = document.getString("username") // Replace "username" with the correct field name

                        // Set username to TextView
                        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
                        userNameTextView.text = username
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    println("Error getting documents: $exception")
                }
        }
    }


}
