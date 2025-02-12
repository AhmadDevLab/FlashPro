package com.devdroid.flashpro.alerter.Ui

import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devdroid.flashpro.alerter.R
import com.devdroid.flashpro.alerter.databinding.ActivitySosBinding

class ActivitySos : AppCompatActivity() {

    private lateinit var binding: ActivitySosBinding

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private var sosPattern = longArrayOf(200, 200, 200, 600, 600, 600, 200, 200, 200) // Default SOS pattern
    private var isSosActive = false
    private lateinit var handler: Handler
    private var sosSpeedFactor = 1 // Factor that adjusts the speed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnStartSos = binding.btnStartSos
        val seekBarSpeed = binding.seekBarSpeed
        val txtSpeed = binding.txtSpeed

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        handler = Handler(Looper.getMainLooper())

        seekBarSpeed.max = 8
        seekBarSpeed.progress = 0
        txtSpeed.text = "SOS Speed: 1"

        seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sosSpeedFactor = progress + 1 // Speed range from 1 to 9
                txtSpeed.text = "SOS Speed: $sosSpeedFactor"
                updateSosPattern()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnStartSos.setOnClickListener {
            if (!isSosActive) {
                startSosSignal()
                btnStartSos.text = "Stop SOS"
            } else {
                stopSosSignal()
                btnStartSos.text = "Start SOS"
            }
            isSosActive = !isSosActive
        }
    }

    private fun updateSosPattern() {
        val baseDelay = 200L
        sosPattern = longArrayOf(
            baseDelay / sosSpeedFactor, baseDelay / sosSpeedFactor, baseDelay / sosSpeedFactor, // Shrt.Flasher
            3 * baseDelay / sosSpeedFactor, 3 * baseDelay / sosSpeedFactor, 3 * baseDelay / sosSpeedFactor, // Lng flashes
            baseDelay / sosSpeedFactor, baseDelay / sosSpeedFactor, baseDelay / sosSpeedFactor // Shot flashes
        )
    }

    private fun startSosSignal() {
        handler.post(sosRunnable)
        Toast.makeText(this, "SOS signal started", Toast.LENGTH_SHORT).show()
    }

    private fun stopSosSignal() {
        handler.removeCallbacks(sosRunnable)
        turnOffFlashlight()
        Toast.makeText(this, "SOS signal stopped", Toast.LENGTH_SHORT).show()
    }

    private val sosRunnable = object : Runnable {
        private var index = 0
        override fun run() {
            if (index < sosPattern.size) {
                val delay = sosPattern[index]
                if (index % 2 == 0) {
                    turnOnFlashlight()
                } else {
                    turnOffFlashlight()
                }
                index++
                handler.postDelayed(this, delay)
            } else {
                index = 0
                handler.post(this)
            }
        }
    }

    private fun turnOnFlashlight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to turn on flashlight", Toast.LENGTH_SHORT).show()
        }
    }

    private fun turnOffFlashlight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to turn off flashlight", Toast.LENGTH_SHORT).show()
        }
    }
}
