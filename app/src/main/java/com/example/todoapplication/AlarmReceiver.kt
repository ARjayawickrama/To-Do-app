package com.example.todoapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AlarmReceiver : AppCompatActivity() {
    private lateinit var chronometer: Chronometer
    private var isRunning: Boolean = false
    private var pauseOffset: Long = 0
    private val handler = Handler()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        chronometer = findViewById(R.id.chronometer)
        val startButton: Button = findViewById(R.id.startButton)
        val stopButton: Button = findViewById(R.id.stopButton)

        // Set up the alarm for 1 minute
        setupAlarm()

        startButton.setOnClickListener {
            if (!isRunning) {
                chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                chronometer.start()
                isRunning = true
            }
        }

        stopButton.setOnClickListener {
            if (isRunning) {
                chronometer.stop()
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
                isRunning = false
            }
        }
    }

    private fun setupAlarm() {
        // Set a delayed action to play the alarm sound after 1 minute
        handler.postDelayed({
            playAlarm()
        }, 600) // 1 minute in milliseconds
    }

    private fun playAlarm() {
        // Load your sound file from raw resources
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer?.start()

        // Show a Toast message when the alarm plays
        Toast.makeText(this, "Alarm! Time's up!", Toast.LENGTH_SHORT).show()

        // Release the media player once done
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the media player if it's still playing
        mediaPlayer?.release()
        handler.removeCallbacksAndMessages(null) // Remove any pending callbacks
    }
}
