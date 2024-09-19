package com.example.fortuna

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.github.mikephil.charting.data.Entry

/* sens class implementation */

class SensHandler(context: Context) : SensorEventListener {
    public var timestamp = 0f
    private var bufferCount = 0
    public var XAccArrayListEntry = ArrayList<Entry>()
    public var YAccArrayListEntry = ArrayList<Entry>()
    public var ZAccArrayListEntry = ArrayList<Entry>()
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val accelerometerData = mutableListOf<FloatArray>() /* float array related to logic evaluation */
    private var _mainActivity: MainActivity? = null
    private lateinit var _graphicLibrary: GraphicLibrary


    init {

    }

    fun initSensHandler(mainActivity: MainActivity, graphicLibrary: GraphicLibrary) {
        _mainActivity = mainActivity
        _graphicLibrary = graphicLibrary
        XAccArrayListEntry.add(Entry(timestamp,0.0f))
        YAccArrayListEntry.add(Entry(timestamp,0.0f))
        ZAccArrayListEntry.add(Entry(timestamp,0.0f))
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

                    timestamp += 0.1f

                    XAccArrayListEntry.add(Entry(timestamp,x))
                    YAccArrayListEntry.add(Entry(timestamp, y))
                    ZAccArrayListEntry.add(Entry(timestamp, z))

                    if(bufferCount%500==0) {
                        _graphicLibrary.startPlotRealSensor(_mainActivity)
                    }
                    bufferCount += 1
                    accelerometerData.add(it.values.clone())

                    if (x > 15 || y > 15 || z > 15) {
                        println("Crash Detected!")
                    }

                    }
                Sensor.TYPE_GYROSCOPE -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    // gyroscope management
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
