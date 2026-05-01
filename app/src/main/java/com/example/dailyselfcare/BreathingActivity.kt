package com.example.dailyselfcare

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class BreathingActivity : AppCompatActivity() {

    private lateinit var breathingCircle: ImageView
    private lateinit var startButton: Button
    private lateinit var backButton: Button
    private lateinit var breathingText: TextView
    private lateinit var breathingModeSpinner: Spinner
    private lateinit var quoteText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var isBreathing = false

    private val quotes = listOf(
        "Breathe in peace, breathe out stress 🌿",
        "Let your breath guide your calm 🌸",
        "Inhale confidence, exhale doubt 🌼",
        "Peace begins with a single breath ☁️",
        "Just breathe — you are enough 💫"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breathing)

        breathingCircle = findViewById(R.id.breathingCircle)
        startButton = findViewById(R.id.startBreathingButton)
        backButton = findViewById(R.id.backButton)
        breathingText = findViewById(R.id.breathingText)
        breathingModeSpinner = findViewById(R.id.breathingModeSpinner)
        quoteText = findViewById(R.id.quoteText)

        // Spinner setup
        val modes = listOf("Box Breathing", "4-7-8 Breathing", "Alternate Nostril")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        breathingModeSpinner.adapter = adapter

        // Random quote
        quoteText.text = quotes.random()

        // Start breathing session
        startButton.setOnClickListener {
            if (!isBreathing) startBreathingSession()
        }

        // Back button
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startBreathingSession() {
        isBreathing = true
        val mode = breathingModeSpinner.selectedItem.toString()
        when (mode) {
            "Box Breathing" -> runBreathingPattern(4000, 4000, 4000, 4000)
            "4-7-8 Breathing" -> runBreathingPattern(4000, 7000, 8000, 0)
            "Alternate Nostril" -> runBreathingPattern(4000, 4000, 4000, 0)
        }
    }

    private fun runBreathingPattern(inhale: Long, hold: Long, exhale: Long, hold2: Long) {
        val pattern = mutableListOf<Pair<String, Long>>()
        pattern.add("Inhale" to inhale)
        if (hold > 0) pattern.add("Hold" to hold)
        pattern.add("Exhale" to exhale)
        if (hold2 > 0) pattern.add("Hold" to hold2)

        var index = 0
        fun nextPhase() {
            if (!isBreathing) return
            val (text, duration) = pattern[index]
            breathingText.text = text
            animateCircle(text)
            handler.postDelayed({
                index = (index + 1) % pattern.size
                nextPhase()
            }, duration)
        }
        nextPhase()
    }

    private fun animateCircle(phase: String) {
        val scale = when (phase) {
            "Inhale" -> 1.3f
            "Exhale" -> 0.8f
            else -> 1.0f
        }

        val anim = ScaleAnimation(
            1.0f, scale,
            1.0f, scale,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim.duration = 1500
        anim.fillAfter = true
        breathingCircle.startAnimation(anim)
    }
}
