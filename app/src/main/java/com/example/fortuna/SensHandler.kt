package com.example.fortuna

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.graphics.Color



/* sens class implementation */

class SensHandler(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    public val entriesX = mutableListOf<Entry>()
    public val entriesY = mutableListOf<Entry>()
    public val entriesZ = mutableListOf<Entry>()
    private var timestamp = 0f
    var XAccArrayListEntry = ArrayList<Entry>()
    var YAccArrayListEntry = ArrayList<Entry>()
    var ZAccArrayListEntry = ArrayList<Entry>()
    public lateinit var chartX: LineChart
    public lateinit var chartY: LineChart
    public lateinit var chartZ: LineChart

    private val accelerometerData = mutableListOf<FloatArray>() /* float array related to logic evaluation */



    init {
        /*
        entriesX.add(Entry(timestamp, 0f))
        entriesY.add(Entry(timestamp, 0f))
        entriesZ.add(Entry(timestamp, 0f))*/

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

                    /* real time */
                    /* entriesX.add(Entry(timestamp,x))
                    entriesY.add(Entry(timestamp,y))
                    entriesZ.add(Entry(timestamp,z)) */

                    timestamp += 0.1f

                    XAccArrayListEntry.add(Entry(timestamp,x))
                    YAccArrayListEntry.add(Entry(timestamp, y))
                    ZAccArrayListEntry.add(Entry(timestamp, z))

                    //val graphicLibrary: GraphicLibrary = GraphicLibrary()
                    //graphicLibrary.startPlotRealSensor(mainActivity)

                    /*
                    updateArray(chartX, entriesX, "Acc X")
                    updateArray(chartY, entriesY, "Acc Y")
                    updateArray(chartZ, entriesZ, "Acc Z")
                    */
                    /*
                    val lineDataSet = LineDataSet(entriesX, "Accelerometer Data X")
                    val lineData = LineData(lineDataSet)
                    lineChart.data = lineData
                    lineChart.invalidate()
                    */

                    /* save data acc */
                    accelerometerData.add(it.values.clone())

                    /* crash detection */
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
