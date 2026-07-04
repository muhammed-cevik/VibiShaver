package com.tdev.vibishaver

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isRunning = false
    private lateinit var vibrator: Vibrator
    private var powerLevel = 3

    private val amplitudes = intArrayOf(60, 110, 170, 220, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val btnShaver = findViewById<ImageView>(R.id.btnShaver)
        val seekBarPower = findViewById<SeekBar>(R.id.seekBarPower)
        val tvPowerLabel = findViewById<TextView>(R.id.tvPowerLabel)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvPowerNumber = findViewById<TextView>(R.id.tvPowerNumber)

        seekBarPower.max = 4
        seekBarPower.progress = 2
        tvPowerNumber.text = "3"
        tvPowerLabel.text = "Orta"

        seekBarPower.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                powerLevel = progress + 1
                tvPowerNumber.text = powerLevel.toString()
                val labels = arrayOf("Hafif", "Dusuk", "Orta", "Guclu", "Maksimum")
                tvPowerLabel.text = labels[progress]
                if (isRunning) restartVibration()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnShaver.setOnClickListener {
            if (isRunning) {
                stopShaver()
                tvStatus.text = "Kapali"
                btnShaver.alpha = 0.5f
            } else {
                startShaver()
                tvStatus.text = "Calisiyor"
                btnShaver.alpha = 1.0f
            }
        }
    }

    private fun startShaver() {
        isRunning = true
        val amp = amplitudes[powerLevel - 1]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(
                longArrayOf(0, 500),
                intArrayOf(0, amp),
                1
            )
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 500), 1)
        }
    }

    private fun stopShaver() {
        isRunning = false
        vibrator.cancel()
    }

    private fun restartVibration() {
        vibrator.cancel()
        startShaver()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopShaver()
    }

    override fun onPause() {
        super.onPause()
        if (isRunning) stopShaver()
    }
}
