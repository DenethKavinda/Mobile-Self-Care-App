package com.example.dailyselfcare

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class HabitsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var habitsList: LinearLayout
    private lateinit var habitProgress: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var addHabitButton: Button
    private lateinit var bottomNav: BottomNavigationView

    private val dateStr: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habits)

        dbHelper = DatabaseHelper(this)
        habitsList = findViewById(R.id.habitsList)
        habitProgress = findViewById(R.id.habitProgress)
        progressText = findViewById(R.id.progressText)
        addHabitButton = findViewById(R.id.addHabitButton)
        bottomNav = findViewById(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_habits

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); true }
                R.id.nav_habits -> true
                R.id.nav_mood -> { startActivity(Intent(this, MoodActivity::class.java)); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
                else -> false
            }
        }

        addHabitButton.setOnClickListener { showAddHabitDialog() }

        loadHabits()
    }

    @SuppressLint("ResourceAsColor")
    private fun loadHabits() {
        habitsList.removeAllViews()
        val habits = dbHelper.getHabits(dateStr)

        if (habits.isEmpty()) {
            val tv = TextView(this)
            tv.text = "No habits for today. Add some! 😊"
            tv.textSize = 18f
            tv.setPadding(12,12,12,12)
            tv.gravity = Gravity.CENTER
            habitsList.addView(tv)
        }

        for (habit in habits) {
            val card = CardView(this)
            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            cardParams.setMargins(0, 12, 0, 12)
            card.layoutParams = cardParams
            card.radius = 16f
            card.cardElevation = 6f
            card.setCardBackgroundColor(resources.getColor(android.R.color.white))
            card.useCompatPadding = true

            val container = LinearLayout(this)
            container.orientation = LinearLayout.HORIZONTAL
            container.gravity = Gravity.CENTER_VERTICAL
            container.setPadding(24,24,24,24)

            val tv = TextView(this)
            tv.text = habit.name
            tv.textSize = 18f
            tv.setTextColor(resources.getColor(R.color.black))
            tv.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val cb = CheckBox(this)
            cb.isChecked = habit.done
            cb.setOnCheckedChangeListener { _, isChecked ->
                dbHelper.updateHabitDone(habit.id, isChecked)
                animateProgress()
            }

            val deleteBtn = ImageButton(this)
            deleteBtn.setImageResource(android.R.drawable.ic_menu_delete)
            deleteBtn.setBackgroundResource(android.R.color.transparent)
            deleteBtn.setOnClickListener {
                dbHelper.deleteHabit(habit.id)
                loadHabits()
                animateProgress()
            }

            container.addView(tv)
            container.addView(cb)
            container.addView(deleteBtn)
            card.addView(container)

            // Fade-in animation for card
            val fade = AlphaAnimation(0f, 1f)
            fade.duration = 500
            card.startAnimation(fade)

            habitsList.addView(card)
        }

        animateProgress()
    }

    private fun animateProgress() {
        val habits = dbHelper.getHabits(dateStr)
        val done = habits.count { it.done }
        val total = habits.size
        val percent = if (total == 0) 0 else (done * 100 / total)

        val animator = ObjectAnimator.ofInt(habitProgress, "progress", habitProgress.progress, percent)
        animator.duration = 800
        animator.start()

        progressText.text = "$percent% Completed"
    }

    private fun showAddHabitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Habit")

        val input = EditText(this)
        input.hint = "Habit Name"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val habitName = input.text.toString()
            if (habitName.isNotEmpty()) {
                dbHelper.addHabit(habitName, dateStr)
                loadHabits()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}
