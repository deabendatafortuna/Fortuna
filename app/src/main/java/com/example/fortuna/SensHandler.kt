package com.example.fortuna

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.*
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.github.mikephil.charting.data.Entry
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

/* sens class implementation */

class SensHandler(private val context: Context) : SensorEventListener {
    var commonTimestamp: Float = 0.0f
    private var bufferCount = 0
    private var bufferCountGyro = 0
    var xAccArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var yAccArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var zAccArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var xGyroArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var yGyroArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var zGyroArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    public val sensorManager: SensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val stepDetector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private var stepDetectorSensor: Sensor? = null
    private var stepCount: Int = 0
    private val ambTemp: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    private var temperatureSensor: Sensor? = null
    private val gravity: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    private val headTracker: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_HEAD_TRACKER)
    private val heartbeat: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_BEAT)
    private val linearAcc: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val light: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val motionDetect: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT)
    private val pose6D0F: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_POSE_6DOF)
    private val pressure: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    private val proximity: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    private val significantMotion: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)
    private var _mainActivity: MainActivity? = null
    private lateinit var _graphicLibrary: GraphicLibrary
    private lateinit var _mapSensActivity: MapSensActivity
    private lateinit var directory: File
    private lateinit var file: File
    private lateinit var fileWriter: FileWriter

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    /* array desired switch sensors state to write on file
                              accelerometer 1 OK, gyroscope 2 OK,
                              stepDetector  3 OK,   ambTemp 4 ,
                              gravity 5 OK,       headTracker 6 ,
                              heartbeat 7,        linearAcc 8 OK,
                              light 9 OK   ,      motionDetect 10,
                              pose6D0F 11  ,      pressure 12    ,
                              proximity 13 OK,    significantMotion 14
                                1  2  3  4  5  6  7  8  9  10 11 12 13 14 */
    val desiredStates = arrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)

    fun initSensHandler(mainActivity: MainActivity, mapSensActivity: MapSensActivity) {
        _mainActivity = mainActivity
        _mapSensActivity= mapSensActivity
        xAccArrayListEntry.add(Entry(commonTimestamp,0.0f))
        yAccArrayListEntry.add(Entry(commonTimestamp,0.0f))
        zAccArrayListEntry.add(Entry(commonTimestamp,0.0f))
        xGyroArrayListEntry.add(Entry(commonTimestamp,0.0f))
        yGyroArrayListEntry.add(Entry(commonTimestamp,0.0f))
        zGyroArrayListEntry.add(Entry(commonTimestamp,0.0f))

        //directory = mainActivity.getExternalFilesDir(null)!!
        // Get directory Download public
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val now = Date()
        val formatter = SimpleDateFormat(buildString {
            append("yyyyMMddHHmmss")
        }, Locale.getDefault())
        val formattedDate = formatter.format(now)
        this.file = File(directory, "log_$formattedDate.txt")  /* File writer initialization */
        fileWriter = FileWriter(file, true)

        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        checkAndRequestActivityRecognitionPermission()

        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d("SensorActivity", "Ambient Temperature non disponibile")
        }

        accelerometer?.also { sensor: Sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        stepDetector?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        ambTemp?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gravity?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        headTracker?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        heartbeat?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        linearAcc?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        light?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        motionDetect?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        pose6D0F?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        pressure?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximity?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        significantMotion?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }


        /*
        fusedLocationClient = _mainActivity?.let {
            LocationServices.getFusedLocationProviderClient(
                it
            )
        }!!
        // Ottieni la posizione corrente
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                try {
                    writeToFile("GPS ; $commonTimestamp ; ${currentLatLng.latitude} ; ${currentLatLng.longitude}")
                }catch (e: IOException){
                    writeToFile("${e.toString()} \" Error to write Gyroscopic\" ")
                }
            }
        }
        */

    }

    fun initSensHandler(mainActivity: MainActivity, graphicLibrary: GraphicLibrary) {
        _mainActivity = mainActivity
        _graphicLibrary = graphicLibrary
        xAccArrayListEntry.add(Entry(commonTimestamp,0.0f))
        yAccArrayListEntry.add(Entry(commonTimestamp,0.0f))
        zAccArrayListEntry.add(Entry(commonTimestamp,0.0f))
        xGyroArrayListEntry.add(Entry(commonTimestamp,0.0f))
        yGyroArrayListEntry.add(Entry(commonTimestamp,0.0f))
        zGyroArrayListEntry.add(Entry(commonTimestamp,0.0f))

        //directory = mainActivity.getExternalFilesDir(null)!!
        // Get directory Download public
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val now = Date()
        val formatter = SimpleDateFormat(buildString {
            append("yyyyMMddHHmmss")
        }, Locale.getDefault())
        val formattedDate = formatter.format(now)
        this.file = File(directory, "log_$formattedDate.txt")  /* File writer initialization */
        fileWriter = FileWriter(file, true)

        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        checkAndRequestActivityRecognitionPermission()

        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
           Log.d("SensorActivity", "Ambient Temperature non disponibile")
        }

        accelerometer?.also { sensor: Sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        stepDetector?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        ambTemp?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gravity?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        headTracker?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        heartbeat?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        linearAcc?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        light?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        motionDetect?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        pose6D0F?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        pressure?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximity?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        significantMotion?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        /* commonTimestamp += 0.1f */
        commonTimestamp += 1.0f
        //writeToFile("commonTimestamp ; $commonTimestamp ;")
        event.let {
             when (event!!.sensor.type) {
                 Sensor.TYPE_ACCELEROMETER -> { /* ps 1 */
                    if (desiredStates[0] == 1) {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        xAccArrayListEntry.add(Entry(commonTimestamp,x))
                        yAccArrayListEntry.add(Entry(commonTimestamp, y))
                        zAccArrayListEntry.add(Entry(commonTimestamp, z))

                        if(bufferCount%100==0) {
                            if(_mainActivity?.mapFlag == true)
                            {
                                _mapSensActivity.startPlotRealSensorAcc(_mainActivity)
                                _mapSensActivity.startPlotRealSensorGyro(_mainActivity)
                            }
                            else {
                                _graphicLibrary.startPlotRealSensorAcc(_mainActivity)
                                _graphicLibrary.startPlotRealSensorGyro(_mainActivity)
                            }
                        }
                        try {
                            writeToFile("Acc ; $commonTimestamp ; $x ; $y ; $z")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write accelerometer\" ")
                        }
                        bufferCount += 1
                       /* if (x > 15 || y > 15 || z > 15) {
                            //println("Crash Detected!")
                            fileWriter.write("Crash Detected!"+"\n")
                        }*/
                    }
                }
                Sensor.TYPE_GYROSCOPE -> { /* ps 2 */
                    if (desiredStates[1] == 1) {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        xGyroArrayListEntry.add(Entry(commonTimestamp,x))
                        yGyroArrayListEntry.add(Entry(commonTimestamp,y))
                        zGyroArrayListEntry.add(Entry(commonTimestamp,z))

                        if(bufferCountGyro%100==0) {
                            if(_mainActivity?.mapFlag == true)
                            {
                                _mapSensActivity.startPlotRealSensorAcc(_mainActivity)
                                _mapSensActivity.startPlotRealSensorGyro(_mainActivity)
                            }
                            else {
                                _graphicLibrary.startPlotRealSensorAcc(_mainActivity)
                                _graphicLibrary.startPlotRealSensorGyro(_mainActivity)
                            }
                        }
                        try {
                            writeToFile("Gyro ; $commonTimestamp ; $x ; $y ; $z")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write Gyroscopic\" ")
                        }
                        bufferCountGyro += 1
                    }
                }
                Sensor.TYPE_STEP_DETECTOR -> { /* ps 3 */
                    if (desiredStates[2] == 1) {
                        try {
                            stepCount++
                            writeToFile("Step detected at timestamp: $commonTimestamp, total steps: $stepCount")
                            /*Log.d("MainActivity", "Step detected at timestamp: $commonTimestamp, total steps: $stepCount")*/
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write STEP_DETECTOR\" ")
                        }
                    }
                }
                Sensor.TYPE_AMBIENT_TEMPERATURE -> { /* ps 4 */
                    if (desiredStates[3] == 1) {
                        val temperature = event.values[0]
                        try {
                            writeToFile("AMBIENT_TEMPERATURE at timestamp; $commonTimestamp ; tmp $temperature")
                            Log.d("SensorActivity", "Ambient Temperature: $temperature, timestamp: $commonTimestamp")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_AMBIENT_TEMPERATURE\" ")
                        }
                    }
                }
                Sensor.TYPE_GRAVITY-> {
                    if (desiredStates[4] == 1) { /* ps 5 */
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
                        try {
                            writeToFile("GRAVITY ; $commonTimestamp ; ; $x ; $y ; $z")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_GRAVITY\" ")
                        }
                    }
                }
                Sensor.TYPE_HEAD_TRACKER-> {   /* ps 6 */
                    if (desiredStates[5] == 1) {
                        val hx = event.values[0]
                        val hy = event.values[1]
                        val hz = event.values[2]
                        try {
                            writeToFile("HEAD_TRACKER ; $commonTimestamp ; $hx ; $hy; $hz")
                         }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_HEAD_TRACKER\" ")
                        }
                    }
                }
                Sensor.TYPE_HEART_BEAT-> {     /* ps 7 */
                    if (desiredStates[6] == 1) {
                        val heartBeat = event.values[0]
                        try {
                            writeToFile("HEART_BEAT ; $commonTimestamp ; $heartBeat")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_HEART_BEAT\" ")
                        }
                    }
                }
                Sensor.TYPE_LINEAR_ACCELERATION-> { /* ps 8 */
                    if (desiredStates[7] == 1) {
                        val ax = event.values[0]
                        val ay = event.values[1]
                        val az = event.values[2]
                        try {
                            writeToFile("LINEAR_ACCELERATION ; $commonTimestamp ; $ax ; $ay ; $az")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_LINEAR_ACCELERATION\" ")
                        }
                    }
                }
                Sensor.TYPE_LIGHT-> {
                    if (desiredStates[8] == 1) { /* ps 9 */
                        val lightLevel = event.values[0]
                        try {
                            writeToFile("LIGHT ; $commonTimestamp ; $lightLevel")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_LIGHT\" ")
                        }
                    }
                }
                Sensor.TYPE_MOTION_DETECT-> { /* ps 10 */
                    if (desiredStates[9] == 1) {
                        val motionDetected = event.values[0]
                        try {
                            writeToFile("MOTION_DETECT ; $commonTimestamp ; $motionDetected")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_MOTION_DETECT\" ")
                        }
                    }
                }
                Sensor.TYPE_POSE_6DOF-> { /* ps 11 (6 Degrees of Freedom) è piuttosto avanzato e
                fornisce informazioni dettagliate sulla posizione e orientamento nello spazio.*/
                    if (desiredStates[10] == 1) {
                        val tx = event.values[0]  // Traslazione x
                        val ty = event.values[1]  // Traslazione y
                        val tz = event.values[2]  // Traslazione z
                        val qx = event.values[3]  // Quaternione x
                        val qy = event.values[4]  // Quaternione y
                        val qz = event.values[5]  // Quaternione z
                        val qw = event.values[6]  // Quaternione w
                        try {
                            writeToFile("POSE_6DOF ; $commonTimestamp ; $tx ; $ty ; $tz ; $qx ; $qy ; $qz ; $qw")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_POSE_6DOF\" ")
                        }
                    }
                }
                Sensor.TYPE_PRESSURE-> { /* ps 12 */
                    if (desiredStates[11] == 1) {
                        val pressure = event.values[0]
                        try {
                            writeToFile("PRESSURE ; $commonTimestamp ; $pressure")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_PRESSURE\" ")
                        }
                    }
                }
                Sensor.TYPE_PROXIMITY-> { /* ps 13 */
                    if (desiredStates[12] == 1) {
                        val proximity = event.values[0]
                        try {
                            writeToFile("PROXIMITY ; $commonTimestamp ; $proximity")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_PROXIMITY\" ")
                        }
                    }
                }
                Sensor.TYPE_SIGNIFICANT_MOTION-> { /* ps 14  è progettato per rilevare movimenti
                significativi senza drenare la batteria del dispositivo*/
                    if (desiredStates[13] == 1) {
                        val signmotionDetected = event.values[0]
                        try {
                            writeToFile("SIGNIFICANT_MOTION ; $commonTimestamp ; $signmotionDetected")
                        }catch (e: IOException){
                            writeToFile("${e.toString()} \" Error to write TYPE_SIGNIFICANT_MOTION\" ")
                        }

                    }
                }
            }
        }


    }
    fun writeToFile(data: String) {
        fileWriter.apply {
            write("$data\n")
            flush()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // accuracy change management
    }

    fun checkAndRequestActivityRecognitionPermission() {

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_REQUEST_CODE
            )
        } else {
            onActivityRecognitionPermissionGranted()
        }
    }

    companion object {
        private const val ACTIVITY_RECOGNITION_REQUEST_CODE = 1001
    }

    private fun onActivityRecognitionPermissionGranted() {
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d("SensHandler", "Activity Recognition permission granted")
    }

    fun onPause() {
        sensorManager.unregisterListener(this)
        fileWriter.close()
    }


}
