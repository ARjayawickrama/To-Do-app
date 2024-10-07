package com.example.todoapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class TimerActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var timeInput: EditText
    private lateinit var setTimeButton: Button
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private var countDownTimer: CountDownTimer? = null
    private var timeInMillis: Long = 0

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Handle permission denial
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        timerTextView = findViewById(R.id.timerTextView)
        timeInput = findViewById(R.id.timeInput)
        setTimeButton = findViewById(R.id.setTimeButton)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setTimeButton.setOnClickListener {
            val timeInputValue = timeInput.text.toString()
            if (timeInputValue.isNotEmpty()) {
                val time = timeInputValue.toLongOrNull()
                if (time != null && time > 0) {
                    timeInMillis = time * 1000
                    timerTextView.text = formatTime(timeInMillis)
                } else {
                    timerTextView.text = "Invalid input"
                }
            } else {
                timerTextView.text = "Please enter a time"
            }
        }

        startButton.setOnClickListener { startTimer() }
        stopButton.setOnClickListener { stopTimer() }
    }

    private fun startTimer() {
        if (timeInMillis > 0) {
            countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timerTextView.text = formatTime(millisUntilFinished)
                }

                override fun onFinish() {
                    timerTextView.text = "00:00:00"
                    sendNotification()
                }
            }.start()
        } else {
            timerTextView.text = "Set time first"
        }
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        timerTextView.text = "Timer stopped"
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun sendNotification() {
        val channelId = "timer_channel"
        val channelName = "Timer Notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.description = "Channel for Timer Notifications"
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure this drawable exists
            .setContentTitle("Timer Finished: Your Task Complete")
            .setContentText("Your task is complete, and the timer has finished.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(1, notificationBuilder.build())
        }
    }
}
