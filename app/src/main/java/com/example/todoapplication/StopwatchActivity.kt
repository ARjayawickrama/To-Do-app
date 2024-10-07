package com.example.todoapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StopwatchActivity : AppCompatActivity() {
    private lateinit var chronometer: Chronometer
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private var isRunning: Boolean = false
    private var pauseOffset: Long = 0
    private val handler = Handler()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        chronometer = findViewById(R.id.chronometer)
        startTimeEditText = findViewById(R.id.startTimeEditText)
        endTimeEditText = findViewById(R.id.endTimeEditText)
        val startButton: Button = findViewById(R.id.startButton)
        val stopButton: Button = findViewById(R.id.stopButton)

        startButton.setOnClickListener {
            val startTimeInput = startTimeEditText.text.toString()
            if (!isRunning && validateMinuteInput(startTimeInput)) {
                val startTimeInMillis = startTimeInput.toLong() * 60000 // Convert minutes to milliseconds
                chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                chronometer.start()
                isRunning = true
                Toast.makeText(this, "Stopwatch started for: $startTimeInput minutes", Toast.LENGTH_SHORT).show()

                // Set the alarm based on the input minutes
                setupAlarm(startTimeInMillis)
            } else {
                Toast.makeText(this, "Please enter a valid start time (1-60 minutes)", Toast.LENGTH_SHORT).show()
            }
        }

        stopButton.setOnClickListener {
            stopChronometer()
            val endTimeInput = endTimeEditText.text.toString()
            if (validateMinuteInput(endTimeInput)) {
                Toast.makeText(this, "Stopwatch stopped after: $endTimeInput minutes", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid end time (1-60 minutes)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAlarm(startTimeInMillis: Long) {
        // Set a delayed action to stop the chronometer and play the alarm sound
        handler.postDelayed({
            stopChronometer()
            playAlarm()
        }, startTimeInMillis) // Use input minutes in milliseconds
    }

    private fun playAlarm() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
            mediaPlayer?.setVolume(1.0f, 1.0f) // Set the volume to the maximum (1.0 is max, 0.0 is mute)
            mediaPlayer?.start()
            Toast.makeText(this, "Alarm! Time's up!", Toast.LENGTH_SHORT).show()

            mediaPlayer?.setOnCompletionListener {
                it.release()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error playing alarm sound: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun stopChronometer() {
        if (isRunning) {
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            isRunning = false
        }
    }

    private fun validateMinuteInput(time: String): Boolean {
        val minutes = time.toIntOrNull()
        return minutes != null && minutes in 1..60 // Validates that the input is between 1 and 60 minutes
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        handler.removeCallbacksAndMessages(null) // Remove any pending callbacks
    }
}
