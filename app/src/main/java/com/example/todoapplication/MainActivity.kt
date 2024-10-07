package com.example.todoapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer

class MainScreenActivity : AppCompatActivity() {
    private lateinit var alarmMessageTextView: TextView
    private lateinit var deleteAlarmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tasksButton: Button = findViewById(R.id.taskButton)
        val stopwatchButton: Button = findViewById(R.id.stopwatchButton)
        val alarmButton: Button = findViewById(R.id.reminderButton)
        alarmMessageTextView = findViewById(R.id.alarmMessageTextView)
        deleteAlarmButton = findViewById(R.id.deleteAlarmButton)

        // Retrieve and display the alarm message
        val sharedPreferences = getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        val alarmMessage = sharedPreferences.getString("ALARM_MESSAGE", "No alarm set")
        alarmMessageTextView.text = "Alarm: $alarmMessage"

        tasksButton.setOnClickListener {
            startActivity(Intent(this, TaskScreenActivity::class.java))
        }

        stopwatchButton.setOnClickListener {
            startActivity(Intent(this, StopwatchActivity::class.java))
        }

        alarmButton.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }

        // Set up delete alarm button
        deleteAlarmButton.setOnClickListener {
            deleteAlarm()
        }
    }

    private fun deleteAlarm() {
        // Clear the alarm message from shared preferences
        val sharedPreferences = getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("ALARM_MESSAGE")
            apply()
        }


    }
}
