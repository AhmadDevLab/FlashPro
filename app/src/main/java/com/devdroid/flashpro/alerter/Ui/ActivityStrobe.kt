package com.devdroid.flashpro.alerter.Ui

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.devdroid.flashpro.alerter.R
import com.devdroid.flashpro.alerter.databinding.ActivityStrobeBinding

class ActivityStrobe : AppCompatActivity() {

    private lateinit var binding: ActivityStrobeBinding

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var toggleStrobe: ToggleButton
    private lateinit var seekBarFrequency: SeekBar
    private lateinit var textViewFrequency: TextView
    private var strobeHandler: Handler? = null
    private var strobeRunnable: Runnable? = null
    private var isStrobeOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStrobeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        toggleStrobe = binding.toggleStrobe
        seekBarFrequency = binding.seekBarFrequency
        textViewFrequency = binding.textViewFrequency

        strobeHandler = Handler(Looper.getMainLooper())

        toggleStrobe.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startStrobeLight()
            } else {
                stopStrobeLight()
            }
        }

        seekBarFrequency.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewFrequency.text = "Frequency: $progress"
                // Adjust the S.R
                if (toggleStrobe.isChecked) {
                    startStrobeLight()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startStrobeLight() {
        val frequency = 100 - seekBarFrequency.progress
        strobeRunnable = object : Runnable {
            override fun run() {
                try {
                    // Toggle the flashlight
                    cameraManager.setTorchMode(cameraId, !isStrobeOn)
                    isStrobeOn = !isStrobeOn
                    strobeHandler?.postDelayed(this, frequency.toLong())
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }
        strobeHandler?.post(strobeRunnable!!)
    }

    private fun stopStrobeLight() {
        strobeHandler?.removeCallbacks(strobeRunnable!!)
        try {
            // Turn off the flashlight
            cameraManager.setTorchMode(cameraId, false)
            isStrobeOn = false
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}
