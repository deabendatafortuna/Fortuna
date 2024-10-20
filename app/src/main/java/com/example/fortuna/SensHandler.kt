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
                                append("\n")
                                append("Error to write accelerometer")
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
                                append("\n")
                                append("Error to write Gyroscopic")
                                append("\n")
                            })
                        }
                        fileWriter.close()

                        bufferCount += 1
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
