package com.example.dailyselfcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import kotlin.random.Random

class MoodActivity : AppCompatActivity() {

    private lateinit var moodSpinner: Spinner
    private lateinit var tipsList: LinearLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var moodLayout: ConstraintLayout

    private val moodTips = mapOf(
        "Happy 😊" to listOf(
            "Keep smiling 😄",
            "Listen to uplifting music 🎶",
            "Share your happiness with a friend ❤️",
            "Write down things you are grateful for 🙏",
            "Do a random act of kindness 🤗"
        ),
        "Neutral 😐" to listOf(
            "Take a short walk 🚶‍♂️",
            "Listen to calming music 🎵",
            "Drink water 💧",
            "Do some deep breathing exercises 🧘‍♀️",
            "Write your thoughts in a journal ✍️"
        ),
        "Sad 😔" to listOf(
            "Talk to someone you trust 🗣️",
            "Watch a feel-good movie 🎬",
            "Do gentle stretching or yoga 🧘",
            "Listen to relaxing music 🎶",
            "Write down your feelings ✍️"
        ),
        "Angry 😡" to listOf(
            "Take deep breaths 🌬️",
            "Go for a brisk walk 🏃‍♂️",
            "Listen to calming music 🎧",
            "Try meditation or mindfulness 🧘‍♂️",
            "Write down what makes you angry 📝"
        ),
        "Excited 🤩" to listOf(
            "Channel energy into a creative project 🎨",
            "Dance to your favorite song 💃",
            "Plan something fun 🗓️",
            "Share your excitement with a friend 😊",
            "Celebrate small wins 🎉"
        )
    )

    private val moodEmojis = mapOf(
        "Happy 😊" to listOf("😄","😆","😊","😁","😎"),
        "Neutral 😐" to listOf("😐","😑","😶","😌","🤔"),
        "Sad 😔" to listOf("😔","😢","😥","😭","😓"),
        "Angry 😡" to listOf("😡","😠","😤","🤬","👿"),
        "Excited 🤩" to listOf("🤩","🥳","🤗","😃","🤪")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood)

        moodSpinner = findViewById(R.id.moodSpinner)
        tipsList = findViewById(R.id.tipsList)
        bottomNav = findViewById(R.id.bottomNavigation)
        moodLayout = findViewById(R.id.moodLayout)
        bottomNav.selectedItemId = R.id.nav_mood

        // Bottom Navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); true }
                R.id.nav_habits -> { startActivity(Intent(this, HabitsActivity::class.java)); true }
                R.id.nav_mood -> true
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
                else -> false
            }
        }

        // Setup Spinner
        val moods = moodTips.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        moodSpinner.adapter = adapter

        moodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMood = moods[position]
                displayTips(selectedMood)
                showAnimatedEmojis(selectedMood)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun displayTips(mood: String) {
        tipsList.removeAllViews()
        val tips = moodTips[mood] ?: listOf("Stay positive! 🌟")
        for (tip in tips) {
            val tv = TextView(this)
            tv.text = "• $tip"
            tv.textSize = 18f
            tv.setPadding(12, 12, 12, 12)
            tipsList.addView(tv)
        }
    }

    private fun showAnimatedEmojis(mood: String) {
        // Remove previous emojis
        if (moodLayout.childCount > 2) {
            moodLayout.removeViews(2, moodLayout.childCount - 2) // Keep spinner and tips
        }

        val emojis = moodEmojis[mood] ?: listOf("⭐")
        repeat(15) {
            val emoji = TextView(this)
            emoji.text = emojis.random()
            emoji.textSize = Random.nextInt(24, 40).toFloat()
            emoji.x = Random.nextInt(0, moodLayout.width.coerceAtLeast(1)).toFloat()
            emoji.y = moodLayout.height.toFloat()
            moodLayout.addView(emoji)

            val animator = ObjectAnimator.ofFloat(emoji, "translationY", moodLayout.height.toFloat(), -100f)
            animator.duration = Random.nextLong(3000, 6000)
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.interpolator = LinearInterpolator()
            animator.start()
        }
    }
}
