package com.example.dailyselfcare

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class WaterTrackerActivity : AppCompatActivity() {

    private var dailyGoal = 8
    private var completedLiters = 0
    private val waterImages = mutableListOf<ImageView>()

    private lateinit var waterIconsLayout: LinearLayout
    private lateinit var backHomeButton: Button
    private lateinit var setGoalButton: Button
    private lateinit var completedText: TextView

    private val prefsName = "WaterPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_tracker)

        waterIconsLayout = findViewById(R.id.waterIconsLayout)
        backHomeButton = findViewById(R.id.backHomeButton)
        setGoalButton = findViewById(R.id.setGoalButton)
        completedText = findViewById(R.id.completedText)

        // Load saved goal and completed liters
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        dailyGoal = prefs.getInt("dailyGoal", 8)
        completedLiters = prefs.getInt("completedLiters", 0)

        setupWaterIcons()

        backHomeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        setGoalButton.setOnClickListener {
            showSetGoalDialog()
        }
    }

    private fun setupWaterIcons() {
        waterIconsLayout.removeAllViews()
        waterImages.clear()

        for (i in 1..dailyGoal) {
            val img = ImageView(this)
            img.layoutParams = LinearLayout.LayoutParams(100, 100).apply { setMargins(8, 8, 8, 8) }
            img.setImageResource(if (i <= completedLiters) R.drawable.ic_water_full else R.drawable.ic_water_empty)

            val index = i
            img.setOnClickListener { toggleLiter(index) }
            waterImages.add(img)
            waterIconsLayout.addView(img)
        }
        updateCompletedText()
    }

    private fun toggleLiter(index: Int) {
        completedLiters = if (index <= completedLiters) index - 1 else index
        saveProgress()
        setupWaterIcons()
    }

    private fun showSetGoalDialog() {
        val editText = EditText(this)
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        editText.hint = "Enter daily goal (liters)"

        AlertDialog.Builder(this)
            .setTitle("Set Daily Water Goal")
            .setView(editText)
            .setPositiveButton("Save") { _: DialogInterface, _: Int ->
                val input = editText.text.toString()
                if (input.isNotEmpty()) {
                    dailyGoal = input.toInt()
                    if (completedLiters > dailyGoal) completedLiters = dailyGoal
                    saveProgress()
                    setupWaterIcons()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveProgress() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        prefs.edit().putInt("completedLiters", completedLiters)
            .putInt("dailyGoal", dailyGoal)
            .apply()
    }

    private fun updateCompletedText() {
        completedText.text = "$completedLiters / $dailyGoal liters completed"
    }
}
