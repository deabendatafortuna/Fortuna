package com.example.fortuna

import android.graphics.Color
import androidx.core.content.ContentProviderCompat.requireContext
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class GraphicLibrary {

    fun start(mainActivity: MainActivity) {

        mainActivity.setContentView(R.layout.activity_graphic_library)
        //val fakeData: FakeData = FakeData()
        val fakeData = JsonFakeData(mainActivity)
        fakeData.start()

        val lineAccChart = mainActivity.findViewById<LineChart>(R.id.lineAccChart)

        lineAccChart.setBackgroundColor(Color.BLACK)

        val lineDataSetAcc1 = LineDataSet(fakeData.a1ArrayListEntry, "acc1")
        lineDataSetAcc1.color = Color.RED
        lineDataSetAcc1.valueTextColor = Color.RED
        lineDataSetAcc1.setDrawValues(false)
        lineDataSetAcc1.setCircleColor(Color.RED)
        lineDataSetAcc1.circleRadius = 1f
        lineDataSetAcc1.valueTextColor = Color.WHITE

        val lineDataSetAcc2 = LineDataSet(fakeData.a2ArrayListEntry, "acc2")
        lineDataSetAcc2.color = Color.BLUE
        lineDataSetAcc2.valueTextColor = Color.BLUE
        lineDataSetAcc2.setDrawValues(false)
        lineDataSetAcc2.setCircleColor(Color.BLUE)
        lineDataSetAcc2.circleRadius = 1f
        lineDataSetAcc2.valueTextColor = Color.WHITE

        val lineDataSetAcc3 = LineDataSet(fakeData.a3ArrayListEntry, "acc3")
        lineDataSetAcc3.color = Color.GREEN
        lineDataSetAcc3.valueTextColor = Color.GREEN
        lineDataSetAcc3.setDrawValues(false)
        lineDataSetAcc3.setCircleColor(Color.GREEN)
        lineDataSetAcc3.circleRadius = 1f
        lineDataSetAcc3.valueTextColor = Color.WHITE

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

        val lineDataSetGyro1 = LineDataSet(fakeData.g1ArrayListEntry, "gyro1")
        lineDataSetGyro1.color = Color.RED
        lineDataSetGyro1.valueTextColor = Color.RED
        lineDataSetGyro1.setDrawValues(false)
        lineDataSetGyro1.setCircleColor(Color.RED)
        lineDataSetGyro1.circleRadius = 1f
        lineDataSetGyro1.valueTextColor = Color.WHITE

        val lineDataSetGyro2 = LineDataSet(fakeData.g2ArrayListEntry, "gyro2")
        lineDataSetGyro2.color = Color.BLUE
        lineDataSetGyro2.valueTextColor = Color.BLUE
        lineDataSetGyro2.setDrawValues(false)
        lineDataSetGyro2.setCircleColor(Color.BLUE)
        lineDataSetGyro2.circleRadius = 1f
        lineDataSetGyro2.valueTextColor = Color.WHITE

        val lineDataSetGyro3 = LineDataSet(fakeData.g3ArrayListEntry, "gyro3")
        lineDataSetGyro3.color = Color.GREEN
        lineDataSetGyro3.valueTextColor = Color.GREEN
        lineDataSetGyro3.setDrawValues(false)
        lineDataSetGyro3.setCircleColor(Color.GREEN)
        lineDataSetGyro3.circleRadius = 1f
        lineDataSetGyro3.valueTextColor = Color.WHITE

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
}




