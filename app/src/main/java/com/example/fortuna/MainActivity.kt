package com.example.fortuna

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import com.example.fortuna.databinding.ActivityMainBinding
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity


class MainActivity : ComponentActivity(),  ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var previewView: PreviewView
    private lateinit var cameraManager: CameraManager
    private lateinit var udpConnector:UDPConnector
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var udpReceiver: UDPListener
    private lateinit var udpSender: UDPSender

    private var graphicLibraryFlag: Boolean = true

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Blocca l'orientamento in verticale
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        previewView = binding.root.findViewById(R.id.previewView)

        if(graphicLibraryFlag)
        {
            val graphicLibrary: GraphicLibrary = GraphicLibrary(this)
            graphicLibrary.startPlotRealSensorAcc(this)
        }
        else
        {

            cameraManager = CameraManager(this, previewView.surfaceProvider)
            cameraManager.startCameraOrAskPermissions()

            udpConnector = UDPConnector { playMp3() }
            udpConnector.startTryConnect()

            /*udpSender = UDPSender("192.168.1.20", 8001)
            udpReceiver = UDPListener(8000) { message ->
                runOnUiThread {
                    if (message == "playMp3")
                        playMp3()
                    if (message == "stopMp3")
                        mediaPlayer?.stop()

                    udpSender?.sendUdpPacket("Received message: $message")
                }
            }

            udpReceiver.startListening()*/


        }




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



