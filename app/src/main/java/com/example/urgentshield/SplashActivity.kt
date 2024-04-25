package com.example.urgentshield

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.res.Resources.Theme
import android.net.Uri
import android.os.Handler
import android.widget.VideoView

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(com.google.android.material.R.style.Theme_AppCompat_DayNight_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({

            // Start the main activity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
//
//        val videoView = findViewById<VideoView>(R.id.videoView)
//
//        // Set up the video path
//        val videoPath = "android.resource://" + packageName + "/" + R.raw.shield_splash
//        videoView.setVideoURI(Uri.parse(videoPath))
//
//        // Adjust the playback speed (0.5 is half the normal speed)
//        videoView.setOnPreparedListener { mediaPlayer ->
//            mediaPlayer.playbackParams = mediaPlayer.playbackParams.apply {
//                speed = 0.6f
//            }
//        }
//
//        // Start the video
//        videoView.setOnCompletionListener {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        videoView.start()
    }
}