package com.tdev.vibishaver

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isRunning = false
    private lateinit var vibrator: Vibrator

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
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        btnShaver.setOnClickListener {
            if (isRunning) {
                vibrator.cancel()
                isRunning = false
                tvStatus.text = "Kapali"
                btnShaver.alpha = 0.5f
            } else {
                isRunning = true
                tvStatus.text = "DIRRRTTT"
                btnShaver.alpha = 1.0f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Maksimum 255 amplitude, 0 bekleme, sonsuz tekrar
                    val effect = VibrationEffect.createWaveform(
                        longArrayOf(0, 1000),
                        intArrayOf(0, 255),
                        1
                    )
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 1000), 1)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vibrator.cancel()
    }

    override fun onPause() {
        super.onPause()
        if (isRunning) {
            vibrator.cancel()
            isRunning = false
        }
    }
}
