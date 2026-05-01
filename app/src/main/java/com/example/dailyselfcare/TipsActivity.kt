package com.example.dailyselfcare

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class TipsActivity : AppCompatActivity() {

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var scrollView: ScrollView
    private lateinit var tipsContainer: LinearLayout
    private lateinit var backHomeButton: Button

    private val categories = listOf(
        "💧 Hydration",
        "🧘 Mindfulness",
        "📝 Journaling",
        "🌞 Sunlight & Outdoors",
        "🥗 Nutrition",
        "💤 Sleep",
        "🏃 Physical Activity",
        "😌 Relaxation"
    )

    private val tipsMap = mapOf(
        "💧 Hydration" to listOf(
            "Drink at least 8 cups of water daily",
            "Start your day with a glass of water",
            "Carry a water bottle with you",
            "Track your water intake"
        ),
        "🧘 Mindfulness" to listOf(
            "Practice deep breathing 5 min/day",
            "Try guided meditation apps",
            "Focus on one task at a time",
            "Take mindful breaks during work"
        ),
        "📝 Journaling" to listOf(
            "Write down your daily thoughts",
            "Track mood and habits",
            "Set goals and reflect on them",
            "Use prompts to explore feelings"
        ),
        "🌞 Sunlight & Outdoors" to listOf(
            "Walk outside for 15–30 min daily",
            "Expose yourself to morning sunlight",
            "Practice outdoor mindfulness",
            "Garden or spend time in nature"
        ),
        "🥗 Nutrition" to listOf(
            "Eat at least 5 portions of fruits & vegetables",
            "Avoid skipping meals",
            "Include protein in each meal",
            "Limit processed sugar and snacks",
            "Stay consistent with meal times"
        ),
        "💤 Sleep" to listOf(
            "Sleep 7–8 hours nightly",
            "Maintain consistent sleep schedule",
            "Avoid screens 1 hour before bed",
            "Keep bedroom dark and quiet"
        ),
        "🏃 Physical Activity" to listOf(
            "Do at least 30 minutes of exercise daily",
            "Stretch before and after workouts",
            "Mix cardio and strength exercises",
            "Take short walking breaks during work"
        ),
        "😌 Relaxation" to listOf(
            "Listen to calming music",
            "Take warm baths",
            "Practice progressive muscle relaxation",
            "Use aromatherapy or candles",
            "Disconnect from devices periodically"
        )
    )

    private var showingTips = false
    private var currentCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tips)

        rootLayout = findViewById(R.id.tipsRoot)
        scrollView = findViewById(R.id.tipsScrollView)
        tipsContainer = findViewById(R.id.tipsContainer)
        backHomeButton = findViewById(R.id.backHomeButton)

        backHomeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        showCategories()
        startBackgroundEmojiAnimation()
    }

    private fun showCategories() {
        tipsContainer.removeAllViews()
        showingTips = false
        currentCategory = ""

        for (category in categories) {
            val tv = TextView(this)
            tv.text = category
            tv.textSize = 22f
            tv.setTextColor(Color.parseColor("#1B4332"))
            tv.setPadding(20, 20, 20, 20)
            tv.gravity = Gravity.CENTER_VERTICAL
            tv.setBackgroundResource(R.drawable.category_bg)
            tv.setOnClickListener {
                currentCategory = category
                showTips(category)
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 12, 0, 12)
            tipsContainer.addView(tv, params)
        }
    }

    private fun showTips(category: String) {
        tipsContainer.removeAllViews()
        showingTips = true

        val backBtn = TextView(this)
        backBtn.text = "⬅️ Back to Categories"
        backBtn.textSize = 18f
        backBtn.setTextColor(Color.parseColor("#40916C"))
        backBtn.setPadding(20, 20, 20, 20)
        backBtn.gravity = Gravity.CENTER
        backBtn.setOnClickListener { showCategories() }
        tipsContainer.addView(backBtn)

        val tipsList = tipsMap[category] ?: emptyList()
        for (tip in tipsList) {
            val tv = TextView(this)
            tv.text = "• $tip"
            tv.textSize = 18f
            tv.setTextColor(Color.parseColor("#2E3A59"))
            tv.setPadding(20, 15, 20, 15)
            tv.setBackgroundResource(R.drawable.tip_bg)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 10, 0, 10)
            tipsContainer.addView(tv, params)
        }
    }

    private fun startBackgroundEmojiAnimation() {
        val emojis = listOf("💧","🧘","📝","🌞","🥗","💤","🏃","😌")
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            var index = 0
            override fun run() {
                val emoji = TextView(this@TipsActivity)
                emoji.text = emojis[index % emojis.size]
                emoji.textSize = 32f
                emoji.alpha = 0.3f
                emoji.setTextColor(Color.WHITE)
                emoji.x = (Math.random() * rootLayout.width).toFloat()
                emoji.y = (Math.random() * rootLayout.height).toFloat()
                rootLayout.addView(emoji)

                val fade = AlphaAnimation(0.3f, 0f)
                fade.duration = 4000
                fade.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                    override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                    override fun onAnimationEnd(animation: android.view.animation.Animation?) { rootLayout.removeView(emoji) }
                    override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                })
                emoji.startAnimation(fade)

                index++
                handler.postDelayed(this, 800)
            }
        })
    }
}
