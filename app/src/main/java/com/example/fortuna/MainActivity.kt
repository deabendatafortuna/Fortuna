package com.example.fortuna

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import com.example.fortuna.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var previewView: PreviewView
    private lateinit var cameraManager: CameraManager
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewView = binding.root.findViewById(R.id.previewView)
        cameraManager = CameraManager(this, previewView.surfaceProvider)
        cameraManager.startCameraOrAskPermissions()

        playMp3()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraManager.handlePermissionsResult(requestCode)
    }

    private fun playMp3()
    {
        mediaPlayer = MediaPlayer.create(this, R.raw.over_the_horizon)
        mediaPlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        // L'app sta per essere messa in background
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }

    override fun onStop() {
        super.onStop()
        // L'app non è più visibile
        mediaPlayer?.pause()
    }
}
