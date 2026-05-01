package com.example.dailyselfcare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        dbHelper = DatabaseHelper(this)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val emailInput = findViewById<EditText>(R.id.signupEmail)
        val passwordInput = findViewById<EditText>(R.id.signupPassword)
        val contactInput = findViewById<EditText>(R.id.signupContact)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val loginRedirect = findViewById<TextView>(R.id.loginRedirect)

        signupButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val contact = contactInput.text.toString().trim()

            when {
                username.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty() -> {
                    Toast.makeText(this, "⚠️ Please fill in all fields!", Toast.LENGTH_SHORT).show()
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "📧 Please enter a valid email!", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(this, "🔒 Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                }
                contact.length < 10 -> {
                    Toast.makeText(this, "📞 Please enter a valid contact number", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val isInserted = dbHelper.addUser(username, email, password, contact)
                    if (isInserted) {
                        Toast.makeText(this, "✅ Account created successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "⚠️ Email already exists!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        loginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
