package com.devdroid.flashpro.alerter.Ui

import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devdroid.flashpro.alerter.databinding.ActivityScreenBinding

class ActivityScreen : AppCompatActivity() {

    private lateinit var binding: ActivityScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {


            seekBarBrightness.max = 255
            seekBarBrightness.progress = getScreenBrightness()

            seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    setScreenBrightness(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })

            btnColorRed.setOnClickListener { setScreenColor("#FF0000") }
            btnColorGreen.setOnClickListener { setScreenColor("#00FF00") }
            btnColorBlue.setOnClickListener { setScreenColor("#0000FF") }
            btnColorYellow.setOnClickListener { setScreenColor("#FFFF00") }
            btnColorWhite.setOnClickListener { setScreenColor("#FFFFFF") }

        }
    }

    // Method to get the current screen brightness
    private fun getScreenBrightness(): Int {
        return try {
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            100
        }
    }

    // Method to set the screen brightness
    private fun setScreenBrightness(brightness: Int) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness / 255f
        window.attributes = layoutParams
    }

    // Method to change the background color of the activity
    private fun setScreenColor(colorCode: String) {
        try {
            window.decorView.setBackgroundColor(android.graphics.Color.parseColor(colorCode))
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Invalid color code", Toast.LENGTH_SHORT).show()
        }
    }
}
