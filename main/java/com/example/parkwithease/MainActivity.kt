package com.example.parkwithease

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check if the user is already logged in and the session is still valid
        val lastLogin = sharedPreferences.getLong("last_login", 0)
        val thirtyDaysInMillis = 30 * 24 * 60 * 60 * 1000 // 30 days in milliseconds
        if (System.currentTimeMillis() - lastLogin < thirtyDaysInMillis) {
            // User has logged in within the last 30 days, proceed to HomeActivity
            startActivity(Intent(this, home::class.java))
            finish() // Finish current activity to prevent going back to the login screen
            return
        }

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textViewLogin = findViewById<TextView>(R.id.textViewLogin)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (validateInput(email, password)) {
                authenticateUser(email, password)
            }
        }

        textViewLogin.setOnClickListener {
            // Log message indicating that login text view is clicked
            Log.d("MainActivity", "Login text view clicked")

            // Navigate to RegisterActivity
            val intent = Intent(this, register::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return false
        }

        // Add more validation logic for email format, password strength, etc. if needed

        return true
    }

    private fun authenticateUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser = auth.currentUser!!

                    // Save login timestamp
                    val editor = sharedPreferences.edit()
                    editor.putLong("last_login", System.currentTimeMillis())
                    editor.apply()

                    Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()

                    // Start HomeActivity upon successful authentication
                    startActivity(Intent(this, home::class.java))
                    finish() // Finish current activity to prevent going back to login screen
                } else {
                    Log.e("MainActivity", "Authentication failed: ${task.exception?.message}")
                    Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
