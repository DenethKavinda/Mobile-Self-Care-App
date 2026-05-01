package com.example.dailyselfcare

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var usernameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var contactEdit: EditText
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button
    private lateinit var inputCard: View

    private lateinit var dbHelper: DatabaseHelper
    private var currentEmail: String = "" // Current user email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        usernameEdit = findViewById(R.id.usernameEdit)
        emailEdit = findViewById(R.id.emailEdit)
        contactEdit = findViewById(R.id.contactEdit)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)
        backButton = findViewById(R.id.backButton)
        inputCard = findViewById(R.id.inputCard)

        dbHelper = DatabaseHelper(this)

        // Load current user details
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        currentEmail = prefs.getString("email", "") ?: ""
        loadUserProfile(currentEmail)

        // Animate input card
        animateFadeIn(inputCard)

        // Button listeners
        updateButton.setOnClickListener { animateButton(updateButton); updateProfile() }
        deleteButton.setOnClickListener { animateButton(deleteButton); confirmDelete() }
        backButton.setOnClickListener { animateButton(backButton); finish() }
    }

    private fun animateFadeIn(view: View) {
        val fade = AlphaAnimation(0f, 1f)
        fade.duration = 600
        view.startAnimation(fade)
        view.visibility = View.VISIBLE
    }

    private fun animateButton(button: View) {
        val animator = ObjectAnimator.ofFloat(button, "translationY", 0f, 10f, 0f)
        animator.duration = 300
        animator.start()
    }

    private fun loadUserProfile(email: String) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE email = ?",
            arrayOf(email)
        )
        if (cursor.moveToFirst()) {
            usernameEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("username")))
            emailEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")))
            contactEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("contact")))
        }
        cursor.close()
        db.close()
    }

    private fun updateProfile() {
        val username = usernameEdit.text.toString().trim()
        val email = emailEdit.text.toString().trim()
        val contact = contactEdit.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "⚠️ All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val success = dbHelper.updateUser(currentEmail, username, email, contact)
        if (success) {
            Toast.makeText(this, "✅ Profile updated successfully", Toast.LENGTH_SHORT).show()
            currentEmail = email
            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit()
            prefs.putString("email", email)
            prefs.apply()
        } else {
            Toast.makeText(this, "❌ Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteAccount() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAccount() {
        val success = dbHelper.deleteUser(currentEmail)
        if (success) {
            Toast.makeText(this, "✅ Account deleted successfully", Toast.LENGTH_SHORT).show()
            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit()
            prefs.clear().apply()
            finish()
        } else {
            Toast.makeText(this, "❌ Failed to delete account", Toast.LENGTH_SHORT).show()
        }
    }
}
