package com.example.parkwithease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.os.CountDownTimer
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Booking : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var citySpinner: Spinner
    private lateinit var firstMallNameTextView: TextView
    private lateinit var firstMallVehicleSpinner: Spinner
    private lateinit var secondMallNameTextView: TextView
    private lateinit var secondMallVehicleSpinner: Spinner
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var userId: String
    private lateinit var firstMallBookButton: Button
    private lateinit var secondMallBookButton: Button

    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    startActivity(Intent(this, home::class.java))
                    true
                }
                R.id.vehiclesFragment -> {
                    startActivity(Intent(this, allvehicles::class.java))
                    true
                }
                R.id.bookingFragment -> {
                    startActivity(Intent(this, Booking::class.java))
                    true
                }
                R.id.settingsFragment -> {
                    startActivity(Intent(this, profile::class.java))
                    true
                }
                else -> false
            }
        }

        firestore = FirebaseFirestore.getInstance()

        citySpinner = findViewById(R.id.spinnerCities)
        firstMallNameTextView = findViewById(R.id.firstMallName)
        firstMallVehicleSpinner = findViewById(R.id.firstMallVehicleSpinner)
        secondMallNameTextView = findViewById(R.id.secondMallName)
        secondMallVehicleSpinner = findViewById(R.id.secondMallVehicleSpinner)
        firstMallBookButton = findViewById(R.id.firstMallBookButton)
        secondMallBookButton = findViewById(R.id.secondMallBookButton)


        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        fetchCities()

        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCity = parent?.getItemAtPosition(position).toString()
                fetchMallNames(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        firstMallBookButton.setOnClickListener {
            val selectedCity = citySpinner.selectedItem.toString()
            val selectedMall = firstMallNameTextView.text.toString()
            val vehicleNumber = firstMallVehicleSpinner.selectedItem.toString() // Get selected vehicle number

            allocateParking(selectedCity, selectedMall, vehicleNumber) // Pass vehicle number to allocateParking
        }

        secondMallBookButton.setOnClickListener {
            val selectedCity = citySpinner.selectedItem.toString()
            val selectedMall = secondMallNameTextView.text.toString()
            val vehicleNumber = secondMallVehicleSpinner.selectedItem.toString() // Get selected vehicle number
            allocateParking(selectedCity, selectedMall, vehicleNumber) // Pass vehicle number to allocateParking
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.homeFragment -> {
                startActivity(Intent(this, home::class.java))
                true
            }
            R.id.vehiclesFragment -> {
                startActivity(Intent(this, allvehicles::class.java))
                true
            }
            R.id.bookingFragment -> {
                startActivity(Intent(this, Booking::class.java))
                true
            }
            R.id.settingsFragment -> {
                startActivity(Intent(this, profile::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchCities() {
        val citiesRef = firestore.collection("city's")
        citiesRef.get()
            .addOnSuccessListener { documents ->
                val cities = documents.mapNotNull { it.id }
                val spinnerAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                citySpinner.adapter = spinnerAdapter
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting cities", exception)
            }
    }

    private fun fetchMallNames(city: String) {
        if (userId.isNotBlank()) {
            firestore.collection("city's").document(city)
                .collection("parking")
                .get()
                .addOnSuccessListener { documents ->
                    var mallName1: String? = null
                    var mallName2: String? = null

                    documents.forEachIndexed { index, document ->
                        val mallName = document.getString("Mall-Name")
                        if (index == 0) {
                            mallName1 = mallName
                        } else if (index == 1) {
                            mallName2 = mallName
                            return@forEachIndexed
                        }
                    }

                    firstMallNameTextView.text = mallName1
                    secondMallNameTextView.text = mallName2

                    fetchVehicles(city, mallName1, mallName2)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: $exception")
                }
        }
    }

    private fun fetchVehicles(city: String, mall1: String?, mall2: String?) {
        mall1?.let { fetchVehiclesForMall(city, it, firstMallVehicleSpinner) }
        mall2?.let { fetchVehiclesForMall(city, it, secondMallVehicleSpinner) }
    }

    private fun fetchVehiclesForMall(city: String, mall: String, spinner: Spinner) {
        val vehiclesRef = firestore.collection("users").document(userId).collection("vehicles")
        vehiclesRef.get()
            .addOnSuccessListener { documents ->
                val vehicleNumbers = mutableListOf<String>()
                for (document in documents) {
                    val vehicleNumber = document.getString("number") // Change to "number" for vehicle number
                    vehicleNumber?.let { vehicleNumbers.add(it) }
                }
                val spinnerAdapter =
                    ArrayAdapter(this@Booking, android.R.layout.simple_spinner_item, vehicleNumbers)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = spinnerAdapter
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting vehicle numbers for $city, $mall", exception)
            }
    }

    private fun allocateParking(city: String, mallName: String, vehicleNumber: String) {
        val parkingRef = firestore.collection("city's").document(city)
            .collection("parking").document(mallName).collection("Park")

        parkingRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot.documents) {
                    val blockData = documentSnapshot.data

                    // If the block data is not null
                    if (blockData != null) {
                        // Filter out available parking blocks (where value is true)
                        val availableBlocks = blockData.filterValues { it == true }.keys.toList()

                        // If there are available parking blocks, allocate one
                        if (availableBlocks.isNotEmpty()) {
                            // Check if the available parking spot is not already occupied
                            val availableParkingNumber = availableBlocks.first()
                            if (blockData[availableParkingNumber] == true) {
                                // Obtain a reference to the specific available parking block
                                val specificParkingRef = parkingRef.document(documentSnapshot.id)

                                // Update the specific parking spot to mark it as occupied
                                val updateMap = hashMapOf<String, Any>(
                                    availableParkingNumber to false
                                )
                                specificParkingRef.update(updateMap)
                                    .addOnSuccessListener {
                                        // Allocation successful
                                        storeParkingDetails(city, mallName, availableParkingNumber, vehicleNumber)
                                        // Display the allocated parking number to the user
                                        val allocatedParkingMessage = "Your allocated parking spot is: $availableParkingNumber\nVehicle Number: $vehicleNumber"
                                        startTimer(availableParkingNumber, vehicleNumber)
                                        showPopup(allocatedParkingMessage,)


                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(TAG, "Error updating parking block: $exception")
                                    }
                                return@addOnSuccessListener // Exit the loop after allocating parking
                            }
                        }
                    } else {
                        Log.d(TAG, "Parking block document does not exist")
                    }
                }

                // No available parking space
                val noParkingSpaceMessage = "No available parking space"
                Toast.makeText(this@Booking, noParkingSpaceMessage, Toast.LENGTH_SHORT).show()

                // Update the ParkingNumber TextView to indicate no parking space available

            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching parking block: $exception")
            }
    }


    private fun storeParkingDetails(city: String, mallName: String, availableBlocks: String, vehicleNumber: String) {
        val detailsRef = firestore.collection("city's").document(city)
            .collection("parking").document(mallName).collection("details")
            .document(availableBlocks) // Store under vehicle number document

        // Store allocated parking number and vehicle number only
        val allocatedParkingData = hashMapOf(
            "parkingNumber" to availableBlocks,
            "vehicleNumber" to vehicleNumber
        )
        detailsRef.set(allocatedParkingData)
            .addOnSuccessListener {
                Log.d(TAG, "Parking details stored successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error storing parking details: $exception")
            }
    }

    private fun showPopup(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                // Do something when the OK button is clicked, if needed
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun startTimer(allocatedParkingSpot: String, vehicleNumber: String) {

        timer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update timer display if needed
            }

            override fun onFinish() {
                // Timer finished, display the allocated parking spot and vehicle number
                val message = "Your allocated parking spot is: $allocatedParkingSpot\nVehicle Number: $vehicleNumber"
                showPopup(message)
            }
        }.start()
    }



    private fun clearVehicleSpinners() {
        val emptyAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, emptyArray<String>())
        emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        firstMallVehicleSpinner.adapter = emptyAdapter
        secondMallVehicleSpinner.adapter = emptyAdapter
    }

    companion object {
        private const val TAG = "BookingActivity"
    }
}

