package com.example.fortuna

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.content.Intent
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import java.io.IOException
/*import kotlinx.coroutines. */



class GraphicLibrary {
    //private var mainActivity: MainActivity? = null
    /*private var job: Job? = null */
    private lateinit var mySensHandler: SensHandler
    var lineDataSetAcc1: LineDataSet? = null
    var lineDataSetAcc2: LineDataSet? = null
    var lineDataSetAcc3: LineDataSet? = null
    var lineDataSetGyro1: LineDataSet? = null
    var lineDataSetGyro2: LineDataSet? = null
    var lineDataSetGyro3: LineDataSet? = null

    constructor (mainActivity: MainActivity){
        initializeSensClass(mainActivity)
    }

    private fun initializeSensClass(mainActivity: MainActivity) {

        mainActivity.setContentView(R.layout.activity_graphic_library)

        try {
            val button = mainActivity.findViewById<Button>(R.id.button_open_map)
            button.setOnClickListener {
                val intent = Intent(mainActivity, GmapActivity::class.java)
                mainActivity.startActivity(intent)
            }
        }catch (e: IOException){
            Log.d("MainActivity", "${e.toString()} \" Error to Create Activity GMap\" ")
        }

        val fakeData = JsonFakeData(mainActivity)
        fakeData.start()

        val lineAccChart = mainActivity.findViewById<LineChart>(R.id.lineAccChart)

        lineAccChart.setBackgroundColor(Color.BLACK)

        mySensHandler = SensHandler(mainActivity)
        mySensHandler.initSensHandler(mainActivity,this)

        lineDataSetAcc1 = LineDataSet(mySensHandler.xAccArrayListEntry, "acc1")
        lineDataSetAcc1?.color = Color.RED
        lineDataSetAcc1?.valueTextColor = Color.RED
        lineDataSetAcc1?.setDrawValues(false)
        lineDataSetAcc1?.setCircleColor(Color.RED)
        lineDataSetAcc1?.circleRadius = 1f
        lineDataSetAcc1?.valueTextColor = Color.WHITE

        lineDataSetAcc2 = LineDataSet(mySensHandler.yAccArrayListEntry, "acc2")
        lineDataSetAcc2?.color = Color.BLUE
        lineDataSetAcc2?.valueTextColor = Color.BLUE
        lineDataSetAcc2?.setDrawValues(false)
        lineDataSetAcc2?.setCircleColor(Color.BLUE)
        lineDataSetAcc2?.circleRadius = 1f
        lineDataSetAcc2?.valueTextColor = Color.WHITE

        lineDataSetAcc3 = LineDataSet(mySensHandler.zAccArrayListEntry, "acc3")
        lineDataSetAcc3?.color = Color.GREEN
        lineDataSetAcc3?.valueTextColor = Color.GREEN
        lineDataSetAcc3?.setDrawValues(false)
        lineDataSetAcc3?.setCircleColor(Color.GREEN)
        lineDataSetAcc3?.circleRadius = 1f
        lineDataSetAcc3?.valueTextColor = Color.WHITE

        val legend = lineAccChart.legend
        legend.textColor = Color.WHITE

        val lineAccData = LineData(lineDataSetAcc1)
        lineAccData.addDataSet(lineDataSetAcc2)
        lineAccData.addDataSet(lineDataSetAcc3)

        lineAccChart.data = lineAccData

        lineAccChart.description.isEnabled = true
        lineAccChart.description.text = "Accelerometer"
        lineAccChart.description.textColor = Color.WHITE

        val lineGyroChart = mainActivity.findViewById<LineChart>(R.id.lineGyroChart)

        lineGyroChart.setBackgroundColor(Color.BLACK)

        lineDataSetGyro1 = LineDataSet(mySensHandler.xGyroArrayListEntry, "gyro1")

        lineDataSetGyro2 = LineDataSet(mySensHandler.yGyroArrayListEntry, "gyro2")

        lineDataSetGyro3 = LineDataSet(mySensHandler.zGyroArrayListEntry, "gyro3")

        val legendGyro = lineGyroChart.legend
        legendGyro.textColor = Color.WHITE

        val lineGyroData = LineData(lineDataSetGyro1)
        lineGyroData.addDataSet(lineDataSetGyro2)
        lineGyroData.addDataSet(lineDataSetGyro3)

        lineGyroChart.data = lineGyroData

        lineGyroChart.description.isEnabled = true
        lineGyroChart.description.text = "Gyroscope"
        lineGyroChart.description.textColor = Color.WHITE
    }

    fun lineDataSetColor(lineDataSet: LineDataSet,color: Int)
    {
        lineDataSet.color = color
        lineDataSet.valueTextColor = color
        lineDataSet.setDrawValues(false)
        lineDataSet.setCircleColor(color)
        lineDataSet.circleRadius = 1f
        lineDataSet.valueTextColor = Color.WHITE
    }

    fun startPlotRealSensorAcc(mainActivity: MainActivity?) {

        var lineDataSetAcc1 = LineDataSet(mySensHandler.xAccArrayListEntry, "acc1")
        lineDataSetColor(lineDataSetAcc1,Color.RED)
        var lineDataSetAcc2 = LineDataSet(mySensHandler.yAccArrayListEntry, "acc2")
        lineDataSetColor(lineDataSetAcc2,Color.BLUE)
        var lineDataSetAcc3 = LineDataSet(mySensHandler.zAccArrayListEntry, "acc3")
        lineDataSetColor(lineDataSetAcc3,Color.GREEN)

        val lineAccData = LineData(lineDataSetAcc1)
        lineAccData.addDataSet(lineDataSetAcc2)
        lineAccData.addDataSet(lineDataSetAcc3)

        val lineAccChart = mainActivity?.findViewById<LineChart>(R.id.lineAccChart)
        lineAccChart?.data = lineAccData

        // Imposta i limiti dell'asse delle x
        val xAxis: XAxis? = lineAccChart?.xAxis
        /* xAxis?.axisMinimum = 0f  // Limite minimo */
        //xAxis?.axisMaximum = mySensHandler.timestampAcc  // Limite massimo
        xAxis?.axisMaximum = mySensHandler.commonTimestamp // Limite massimo

        lineAccChart?.invalidate()

    }

    fun startPlotRealSensorGyro(mainActivity: MainActivity?) {

        var lineDataSetGyro1 = LineDataSet(mySensHandler.xGyroArrayListEntry, "Gyro1")
        lineDataSetColor(lineDataSetGyro1,Color.RED)
        var lineDataSetGyro2 = LineDataSet(mySensHandler.yGyroArrayListEntry, "Gyro2")
        lineDataSetColor(lineDataSetGyro2,Color.BLUE)
        var lineDataSetGyro3 = LineDataSet(mySensHandler.zGyroArrayListEntry, "Gyro3")
        lineDataSetColor(lineDataSetGyro3,Color.GREEN)

        val lineGyroData = LineData(lineDataSetGyro1)
        lineGyroData.addDataSet(lineDataSetGyro2)
        lineGyroData.addDataSet(lineDataSetGyro3)

        val lineGyroChart = mainActivity?.findViewById<LineChart>(R.id.lineGyroChart)
        lineGyroChart?.data = lineGyroData

        // Imposta i limiti dell'asse delle x
        val xAxis: XAxis? = lineGyroChart?.xAxis
        /* xAxis?.axisMinimum = 0f  // Limite minimo */
        //xAxis?.axisMaximum = mySensHandler.timestampGyro  // Limite massimo
        xAxis?.axisMaximum = mySensHandler.commonTimestamp  // Limite massimo

        lineGyroChart?.invalidate()

    }
}




