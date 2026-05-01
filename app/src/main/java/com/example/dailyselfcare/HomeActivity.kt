package com.example.dailyselfcare

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var dateText: TextView
    private lateinit var timeText: TextView
    private lateinit var waterProgress: ProgressBar
    private lateinit var waterPercentageText: TextView
    private lateinit var addWaterButton: Button
    private lateinit var journalButton: CardView
    private lateinit var breathingButton: CardView
    private lateinit var tipsButton: CardView  // New Tips button

    private val prefsName = "WaterPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize views
        dateText = findViewById(R.id.dateText)
        timeText = findViewById(R.id.timeText)
        waterProgress = findViewById(R.id.waterProgress)
        waterPercentageText = findViewById(R.id.waterPercentageText)
        addWaterButton = findViewById(R.id.addWaterButton)
        journalButton = findViewById(R.id.journalButton)
        breathingButton = findViewById(R.id.breathingButton)
        tipsButton = findViewById(R.id.tipsButton) // Initialize tips button

        // Animate date & time
        startDateTimeAnimation()

        // Load saved water progress with animation
        updateWaterProgress(animated = true)

        // Click listeners
        addWaterButton.setOnClickListener {
            animateClick(it)
            startActivity(Intent(this, WaterTrackerActivity::class.java))
        }

        journalButton.setOnClickListener {
            animateClick(it)
            startActivity(Intent(this, JournalActivity::class.java))
        }

        breathingButton.setOnClickListener {
            animateClick(it)
            startActivity(Intent(this, BreathingActivity::class.java))
        }

        tipsButton.setOnClickListener {
            animateClick(it)
            startActivity(Intent(this, TipsActivity::class.java))
        }

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_habits -> { startActivity(Intent(this, HabitsActivity::class.java)); true }
                R.id.nav_mood -> { startActivity(Intent(this, MoodActivity::class.java)); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateWaterProgress(animated = true)
    }

    private fun updateWaterProgress(animated: Boolean = false) {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val dailyGoal = prefs.getInt("dailyGoal", 8)
        val completedLiters = prefs.getInt("completedLiters", 0)
        val progress = (completedLiters.toFloat() / dailyGoal.toFloat() * 100).toInt()

        if (animated) {
            val animator = ObjectAnimator.ofInt(waterProgress, "progress", 0, progress)
            animator.duration = 1000
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()
        } else {
            waterProgress.progress = progress
        }

        waterPercentageText.text = "$progress% Completed"
    }

    private fun startDateTimeAnimation() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                val now = Date()
                val dateFormat = SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

                dateText.text = dateFormat.format(now)
                timeText.text = timeFormat.format(now)

                val fade = AlphaAnimation(0.5f, 1.0f)
                fade.duration = 1000
                fade.repeatMode = Animation.REVERSE
                fade.repeatCount = Animation.INFINITE
                dateText.startAnimation(fade)

                timeText.animate().scaleX(1.1f).scaleY(1.1f).setDuration(500)
                    .withEndAction { timeText.animate().scaleX(1f).scaleY(1f).setDuration(500).start() }
                    .start()

                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun animateClick(view: View) {
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
            .withEndAction { view.animate().scaleX(1f).scaleY(1f).setDuration(100).start() }
            .start()
    }
}
