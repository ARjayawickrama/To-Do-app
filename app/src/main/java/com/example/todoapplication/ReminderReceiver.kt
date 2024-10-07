package com.example.todoapplication

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity

class StopwatchActivityRenamed : AppCompatActivity() {
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

        // Set up the alarms for 1 minute and 2 minutes
        setupAlarms()

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

    private fun setupAlarms() {
        // Alarm for 1 minute
        handler.postDelayed({
            playAlarm()
        }, 60000) // 1 minute in milliseconds

        // Alarm for 2 minutes
        handler.postDelayed({
            playAlarm()
        }, 120000) // 2 minutes in milliseconds
    }

    private fun playAlarm() {
        // Load your sound file from raw resources
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer?.start()

        // Release the media player once done
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the media player if it's still playing
        mediaPlayer?.release()
    }
}
