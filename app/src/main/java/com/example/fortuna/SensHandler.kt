package com.example.fortuna

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.util.TimeZone
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import java.text.SimpleDateFormat
import java.util.*
import java.io.File
import java.io.FileWriter
import java.io.IOException


/* sens class implementation */

class SensHandler(context: Context) : SensorEventListener {
    public var timestampAcc = 0f
    public var timestampGyro = 0f
    private var bufferCount = 0
    public var XAccArrayListEntry = ArrayList<Entry>()
    public var YAccArrayListEntry = ArrayList<Entry>()
    public var ZAccArrayListEntry = ArrayList<Entry>()
    public var XGyroArrayListEntry = ArrayList<Entry>()
    public var YGyroArrayListEntry = ArrayList<Entry>()
    public var ZGyroArrayListEntry = ArrayList<Entry>()
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private var _mainActivity: MainActivity? = null
    private lateinit var _graphicLibrary: GraphicLibrary
    private val STORAGE_PERMISSION_CODE = 100
    private lateinit var directory: File
    private lateinit var file: File


    init {

    }

    fun initSensHandler(mainActivity: MainActivity, graphicLibrary: GraphicLibrary) {
        _mainActivity = mainActivity
        _graphicLibrary = graphicLibrary
        XAccArrayListEntry.add(Entry(timestampAcc,0.0f))
        YAccArrayListEntry.add(Entry(timestampAcc,0.0f))
        ZAccArrayListEntry.add(Entry(timestampAcc,0.0f))
        XGyroArrayListEntry.add(Entry(timestampGyro,0.0f))
        YGyroArrayListEntry.add(Entry(timestampGyro,0.0f))
        ZGyroArrayListEntry.add(Entry(timestampGyro,0.0f))
        //directory = mainActivity.getExternalFilesDir(null)!!
        // Ottieni la directory Download pubblica
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val now = Date()
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val formattedDate = formatter.format(now)
        file = File(directory, "log_"+formattedDate+".txt")


        accelerometer?.also { sensor ->
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

                        XAccArrayListEntry.add(Entry(timestampAcc,x))
                        YAccArrayListEntry.add(Entry(timestampAcc, y))
                        ZAccArrayListEntry.add(Entry(timestampAcc, z))

                        if(bufferCount%100==0) {
                            _graphicLibrary.startPlotRealSensorAcc(_mainActivity)
                            _graphicLibrary.startPlotRealSensorGyro(_mainActivity)
                        }

                        val fileWriter = FileWriter(file,true)

                        try {
                            fileWriter.write(timestampAcc.toString()+";"+x+";"+y+";"+z+"\n")
                        }catch (e: IOException){
                            fileWriter.write("Errore durante la scrittura Accellerometri"+"\n")
                        }

                        bufferCount += 1

                        if (x > 15 || y > 15 || z > 15) {
                            //println("Crash Detected!")
                            fileWriter.write("Crash Detected!"+"\n")
                        }
                       fileWriter.close()
                    }
                Sensor.TYPE_GYROSCOPE -> {
                        val x = it.values[0]
                        val y = it.values[1]
                        val z = it.values[2]

                        timestampGyro += 0.1f

                        XGyroArrayListEntry.add(Entry(timestampGyro,x))
                        YGyroArrayListEntry.add(Entry(timestampGyro,y))
                        ZGyroArrayListEntry.add(Entry(timestampGyro,z))

                        if(bufferCount%100==0) {
                            _graphicLibrary.startPlotRealSensorGyro(_mainActivity)
                            _graphicLibrary.startPlotRealSensorAcc(_mainActivity)
                        }
                        val fileWriter = FileWriter(file,true)
                        try {
                            fileWriter.write(timestampAcc.toString()+";"+x+";"+y+";"+z+"\n")
                        }catch (e: IOException){
                            fileWriter.write("Errore durante la scrittura Giroscopi"+"\n")
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
