package com.example.fortuna

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import com.example.fortuna.R.id
import com.example.fortuna.databinding.ActivityMainBinding
import android.content.Intent

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import java.io.IOException


class MainActivity : ComponentActivity(),  ActivityCompat.OnRequestPermissionsResultCallback, LocationListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var previewView: PreviewView
    private lateinit var cameraManager: CameraManager
    private lateinit var udpConnector: UDPConnector
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var udpReceiver: UDPListener
    private lateinit var udpSender: UDPSender

    private var graphicLibraryFlag: Boolean = false
    private var helpPeople: Boolean = true
    public var mapFlag: Boolean = true
    private lateinit var locationManager: LocationManager

    var latitude  = 0.0
    var longitude = 0.0
    private lateinit var _mapSensActivity: MapSensActivity
    private lateinit var _helpActivity: HelpActivity

    @SuppressLint("SourceLockedOrientationActivity", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT /* Vertical orientation blocked */

        previewView = binding.root.findViewById(id.previewView)


        if(helpPeople)
        {
            try {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e("MainActivity", "Impossibile avviare HelpActivity. È stata dichiarata nell'AndroidManifest.xml?", e)
                Toast.makeText(this, "Errore: schermata di aiuto non disponibile.", Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            if (graphicLibraryFlag) /* sensHandler = SensHandler(this) */ {
                if (mapFlag) {

                    _mapSensActivity = MapSensActivity(this)
                    _mapSensActivity.startPlotRealSensorAcc(this)

                    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            1
                        )
                        return
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

                } else {

                    val graphicLibrary: GraphicLibrary = GraphicLibrary(this)
                    graphicLibrary.startPlotRealSensorAcc(this)
                }
            }
            else {
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
        //sensHandler.onPause()
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

    override fun onLocationChanged(location: Location) {
        // Aggiorna la UI o esegui altre azioni con i dati di location
        this.latitude= location.latitude
        this.longitude = location.longitude
        _mapSensActivity.mySensHandler
        try {
            _mapSensActivity.mySensHandler.writeToFile("GPS ; ${_mapSensActivity.mySensHandler.commonTimestamp} ; ${location.latitude} ; ${location.longitude}")
        }catch (e: IOException){
            _mapSensActivity.mySensHandler.writeToFile("${e.toString()} \" Error to write Gyroscopic\" ")
        }
    }
}



