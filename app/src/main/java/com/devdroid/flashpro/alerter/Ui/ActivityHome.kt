package com.devdroid.flashpro.alerter.Ui

import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.ToggleButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devdroid.flashpro.alerter.R
import com.devdroid.flashpro.alerter.databinding.ActivityMainBinding

class ActivityHome : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStrobe.setOnClickListener(){
            startActivity(Intent(this@ActivityHome,ActivityStrobe::class.java))
        }
        binding.btnSos.setOnClickListener(){
            startActivity(Intent(this@ActivityHome, ActivitySos::class.java))
        }
        binding.btnScreen.setOnClickListener(){
            startActivity(Intent(this@ActivityHome, ActivityScreen::class.java))

        }

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        val toggleFlashlight = findViewById<ToggleButton>(R.id.toggleFlashlight)

        toggleFlashlight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                turnOnFlashlight()
            } else {
                turnOffFlashlight()
            }
        }

    }

    private fun turnOnFlashlight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true)
                Toast.makeText(this, "Flashlight ON", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Flashlight OFF", Toast.LENGTH_SHORT).show()
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to turn off flashlight", Toast.LENGTH_SHORT).show()
        }
    }


}