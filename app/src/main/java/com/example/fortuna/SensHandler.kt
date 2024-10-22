package com.example.fortuna

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Environment
import com.github.mikephil.charting.data.Entry
import java.text.SimpleDateFormat
import java.util.*
import java.io.File
import java.io.FileWriter
import java.io.IOException


/* sens class implementation */

class SensHandler(context: Context) : SensorEventListener {
    var timestampAcc = 0f
    var timestampGyro = 0f
    private var bufferCount = 0
    var xAccArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var yAccArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var zAccArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var xGyroArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var yGyroArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()
    var zGyroArrayListEntry: ArrayList<Entry> = ArrayList<Entry>()

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val stepDetector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val ambTemp: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
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
    // val STORAGE_PERMISSION_CODE = 100
    private lateinit var directory: File
    private lateinit var file: File

    fun initSensHandler(mainActivity: MainActivity, graphicLibrary: GraphicLibrary) {
        _mainActivity = mainActivity
        _graphicLibrary = graphicLibrary
        xAccArrayListEntry.add(Entry(timestampAcc,0.0f))
        yAccArrayListEntry.add(Entry(timestampAcc,0.0f))
        zAccArrayListEntry.add(Entry(timestampAcc,0.0f))
        xGyroArrayListEntry.add(Entry(timestampGyro,0.0f))
        yGyroArrayListEntry.add(Entry(timestampGyro,0.0f))
        zGyroArrayListEntry.add(Entry(timestampGyro,0.0f))
        //directory = mainActivity.getExternalFilesDir(null)!!
        // Get directory Download public
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val now = Date()
        val formatter = SimpleDateFormat(buildString {
        append("yyyyMMddHHmmss")
    }, Locale.getDefault())
        val formattedDate = formatter.format(now)
        this.file = File(directory, "log_$formattedDate.txt")

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
        event?.let {
             when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                        val x = it.values[0]
                        val y = it.values[1]
                        val z = it.values[2]

                        timestampAcc += 0.1f

                        xAccArrayListEntry.add(Entry(timestampAcc,x))
                        yAccArrayListEntry.add(Entry(timestampAcc, y))
                        zAccArrayListEntry.add(Entry(timestampAcc, z))

                        if(bufferCount%100==0) {
                            _graphicLibrary.startPlotRealSensorAcc(_mainActivity)
                            _graphicLibrary.startPlotRealSensorGyro(_mainActivity)
                        }
                        val fileWriter = FileWriter(file,true)

                        try {
                            fileWriter.write(" Acc $timestampAcc;$x;$y;$z\n")
                        }catch (e: IOException){
                            fileWriter.write(buildString {
                                append(e.toString())
                                append(" Error to write accelerometer")
                                append("\n")
                            })
                        }
                        bufferCount += 1
                       /* if (x > 15 || y > 15 || z > 15) {
                            //println("Crash Detected!")
                            fileWriter.write("Crash Detected!"+"\n")
                        }*/
                       fileWriter.close()
                    }
                Sensor.TYPE_GYROSCOPE -> {
                        val x = it.values[0]
                        val y = it.values[1]
                        val z = it.values[2]

                        timestampGyro += 0.1f

                        xGyroArrayListEntry.add(Entry(timestampGyro,x))
                        yGyroArrayListEntry.add(Entry(timestampGyro,y))
                        zGyroArrayListEntry.add(Entry(timestampGyro,z))

                        if(bufferCount%100==0) {
                            _graphicLibrary.startPlotRealSensorGyro(_mainActivity)
                            _graphicLibrary.startPlotRealSensorAcc(_mainActivity)
                        }
                        val fileWriter = FileWriter(file,true)

                        try {
                            fileWriter.write(buildString {
                                append("Gyro ")
                                append(timestampAcc.toString())
                                append(";")
                                append(x)
                                append(";")
                                append(y)
                                append(";")
                                append(z)
                                append("\n")
                            })
                        }catch (e: IOException){
                            fileWriter.write(buildString {
                                append(e.toString())
                                append(" Error to write Gyroscopic")
                                append("\n")
                            })
                        }
                        fileWriter.close()
                        bufferCount += 1
                }
                Sensor.TYPE_STEP_DETECTOR -> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("STEP_DETECTOR ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write STEP_DETECTOR")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("AMBIENT_TEMPERATURE ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write AMBIENT_TEMPERATURE")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_GRAVITY-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("GRAVITY ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write GRAVITY")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_HEAD_TRACKER-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("HEAD_TRACKER ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write HEAD_TRACKER")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_HEART_BEAT-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("HEART_BEAT ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write HEART_BEAT")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_LINEAR_ACCELERATION-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("LINEAR_ACCELERATION ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write LINEAR_ACCELERATION")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_LIGHT-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("LIGHT ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write LIGHT")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_MOTION_DETECT-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("MOTION_DETECT ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write MOTION_DETECT")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_POSE_6DOF-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("POSE_6DOF ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write POSE_6DOF")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_PRESSURE-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("PRESSURE ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write PRESSURE")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_PROXIMITY-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("PROXIMITY ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write PROXIMITY")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
                Sensor.TYPE_SIGNIFICANT_MOTION-> {
                    val fileWriter = FileWriter(file,true)
                    try {
                        fileWriter.write(buildString {
                            append("SIGNIFICANT_MOTION ")
                            append(it.timestamp.toString())
                            append(";")
                            append(it.values[0])
                            append("\n")
                        })
                    }catch (e: IOException){
                        fileWriter.write(buildString {
                            append(e.toString())
                            append(" Error to write SIGNIFICANT_MOTION")
                            append("\n")
                        })
                    }
                    fileWriter.close()
                }
            }
        }


}

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // accuracy change management
    }

    fun unregister() {
        sensorManager.unregisterListener(this)
    }



}
