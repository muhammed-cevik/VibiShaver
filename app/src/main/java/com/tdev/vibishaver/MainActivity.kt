package com.tdev.vibishaver

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isRunning = false
    private var vibrationThread: Thread? = null
    private lateinit var vibrator: Vibrator

    // Güç seviyeleri: 1-5 arası, her seviyede farklı frekans ve amplitude
    private var powerLevel = 3  // default orta

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

        seekBarPower.max = 4  // 0-4 = 5 seviye
        seekBarPower.progress = 2  // default 3. seviye
        tvPowerNumber.text = "3"

        seekBarPower.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                powerLevel = progress + 1
                tvPowerNumber.text = powerLevel.toString()
                val labels = arrayOf("Çok Hafif", "Hafif", "Orta", "Güçlü", "Maksimum")
                tvPowerLabel.text = labels[progress]
                if (isRunning) {
                    restartVibration()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnShaver.setOnClickListener {
            if (isRunning) {
                stopShaver()
                tvStatus.text = "Kapalı"
                btnShaver.alpha = 0.6f
            } else {
                startShaver()
                tvStatus.text = "Çalışıyor..."
                btnShaver.alpha = 1.0f
            }
        }
    }

    private fun getVibrationPattern(): Pair<LongArray, IntArray> {
        // Her seviyede farklı titreşim deseni
        return when (powerLevel) {
            1 -> Pair(
                longArrayOf(0, 50, 150, 50),   // çok hafif - uzun aralar
                intArrayOf(0, 80, 0, 80)
            )
            2 -> Pair(
                longArrayOf(0, 60, 100, 60),
                intArrayOf(0, 120, 0, 120)
            )
            3 -> Pair(
                longArrayOf(0, 70, 60, 70),
                intArrayOf(0, 170, 0, 170)
            )
            4 -> Pair(
                longArrayOf(0, 80, 30, 80),
                intArrayOf(0, 210, 0, 210)
            )
            5 -> Pair(
                longArrayOf(0, 100, 10, 100),  // maksimum - neredeyse sürekli
                intArrayOf(0, 255, 0, 255)
            )
            else -> Pair(longArrayOf(0, 70, 60, 70), intArrayOf(0, 170, 0, 170))
        }
    }

    private fun startShaver() {
        isRunning = true
        val (pattern, amplitudes) = getVibrationPattern()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, amplitudes, 0)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, 0)
        }
    }

    private fun stopShaver() {
        isRunning = false
        vibrator.cancel()
    }

    private fun restartVibration() {
        vibrator.cancel()
        val (pattern, amplitudes) = getVibrationPattern()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, amplitudes, 0)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, 0)
        }
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
