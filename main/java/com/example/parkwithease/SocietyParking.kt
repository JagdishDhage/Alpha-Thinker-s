package com.example.parkwithease

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog

class SocietyParking : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_society_parking)

        // Fetch city names and set them as options for the city spinner
        val citySpinner = findViewById<Spinner>(R.id.citySpinner)
        val citiesCollection = db.collection("Other-Park")
        citiesCollection.get().addOnSuccessListener { documents ->
            val cityNames = documents.map { it.id }
            if (cityNames.isNotEmpty()) {
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                citySpinner.adapter = adapter
            }
        }

        // Set up a listener for city spinner selection
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                val selectedCityName = citySpinner.selectedItem.toString()
                val mallSpinner = findViewById<Spinner>(R.id.mallSpinner)

                // Fetch mall names based on the selected city
                val mallsCollection = db.collection("Other-Park").document(selectedCityName)
                mallsCollection.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val mallNames = documentSnapshot.data?.keys?.toList() ?: emptyList()
                        if (mallNames.isNotEmpty()) {
                            val adapter = ArrayAdapter(this@SocietyParking, android.R.layout.simple_spinner_item, mallNames)
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            mallSpinner.adapter = adapter
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching mall names: ", exception)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing if nothing is selected
            }
        }

        val fetchParkingButton = findViewById<Button>(R.id.fetchParkingButton)
        fetchParkingButton.setOnClickListener {
            val cityName = citySpinner.selectedItem.toString()
            val mallName = findViewById<Spinner>(R.id.mallSpinner).selectedItem.toString()

            if (cityName.isNotEmpty() && mallName.isNotEmpty()) {
                fetchSocietyParking(cityName, mallName)
            } else {
                Toast.makeText(this, "Please select both city and mall", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchSocietyParking(cityName: String, mallName: String) {
        db.collection("Other-Park").document(cityName)
            .collection(mallName)
            .get()
            .addOnSuccessListener { documents ->
                val parkingDetailsList = mutableListOf<String>()
                for (document in documents) {
                    // Assuming each document contains society parking data
                    val societyName = document.id

                    // Fetch data for societyName here and handle accordingly
                    val societyDetailsDocRef = db.collection("Other-Park").document(cityName)
                        .collection(mallName).document(societyName)
                    societyDetailsDocRef.get().addOnSuccessListener { societyDocument ->
                        if (societyDocument.exists()) {
                            // Check if the selected mall matches the value in the "Nearest-Mall" field
                            val nearestMall = societyDocument.getString("Nearest-Mall")
                            if (nearestMall == mallName) {
                                // Construct parking details string
                                val societyDetails = societyDocument.data
                                val detailsString = societyDetails?.map { "${it.key}: ${it.value}" }?.joinToString("\n")
                                detailsString?.let {
                                    parkingDetailsList.add(it)
                                }
                            } else {
                                Log.d(TAG, "Selected mall does not match Nearest-Mall value for $societyName")
                            }
                        } else {
                            Log.d(TAG, "Society details not found for $societyName")
                        }

                        // Show parking details in a dialog
                        showParkingDetailsDialog(parkingDetailsList)
                    }.addOnFailureListener { exception ->
                        Log.e(TAG, "Error fetching society details: ", exception)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }


    private fun showParkingDetailsDialog(parkingDetailsList: List<String>) {
        val detailsString = parkingDetailsList.joinToString("\n\n")
        val dialog = AlertDialog.Builder(this)
            .setTitle("Parking Details")
            .setMessage(detailsString)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }


    companion object {
        private const val TAG = "SocietyParkingActivity"
    }
}
