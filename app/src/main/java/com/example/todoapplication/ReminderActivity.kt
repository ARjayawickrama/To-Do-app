package com.example.todoapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AlarmActivity : AppCompatActivity() {
    private lateinit var timePicker: TimePicker
    private lateinit var messageEditText: EditText
    private lateinit var setAlarmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        timePicker = findViewById(R.id.timePicker)
        messageEditText = findViewById(R.id.reminderMessageEditText)
        setAlarmButton = findViewById(R.id.setReminderButton)

        // Request notification permission
        requestNotificationPermission()

        setAlarmButton.setOnClickListener {
            setAlarm()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private fun setAlarm() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
        calendar.set(Calendar.MINUTE, timePicker.minute)
        calendar.set(Calendar.SECOND, 0)

        // Check if the alarm time is in the future
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            // Add 1 day if the time is in the past
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(this, AlarmReceiver::class.java)
        val alarmMessage = messageEditText.text.toString().ifEmpty { "Alarm!" } // Get the user-defined message
        intent.putExtra("ALARM_MESSAGE", alarmMessage)

        // Save the alarm message to SharedPreferences
        val sharedPreferences = getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("ALARM_MESSAGE", alarmMessage)
            apply()
        }

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        // Log the alarm setting
        Log.d("AlarmActivity", "Alarm set for $alarmMessage at: ${calendar.timeInMillis}")

        Toast.makeText(this, "Alarm set for ${calendar.time}", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
