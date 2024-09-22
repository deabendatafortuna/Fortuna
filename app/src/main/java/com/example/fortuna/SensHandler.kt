package com.example.fortuna

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.github.mikephil.charting.data.Entry

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

                    if(bufferCount%500==0) {
                        _graphicLibrary.startPlotRealSensorAcc(_mainActivity)
                        _graphicLibrary.startPlotRealSensorGyro(_mainActivity)
                    }
                    bufferCount += 1

                    if (x > 15 || y > 15 || z > 15) {
                        println("Crash Detected!")
                    }

                    }
                Sensor.TYPE_GYROSCOPE -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]

                    timestampGyro += 0.1f

                    XGyroArrayListEntry.add(Entry(timestampGyro,x))
                    YGyroArrayListEntry.add(Entry(timestampGyro,y))
                    ZGyroArrayListEntry.add(Entry(timestampGyro,z))

                    if(bufferCount%500==0) {
                        _graphicLibrary.startPlotRealSensorGyro(_mainActivity)
                        _graphicLibrary.startPlotRealSensorAcc(_mainActivity)
                    }
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
