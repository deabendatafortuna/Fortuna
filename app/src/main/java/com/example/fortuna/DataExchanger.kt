package com.example.myfirstapp

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException

class DataExchanger(context: Context)
{
    //private val sensorData : List<SensorDataStructure>? = leggiDaJson(context, R.raw.opzioni_luca)

    private fun leggiDaJson(context: Context, resourceId: Int): List<SensorDataStructure>?
    {
        val jsonString =
            context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        return try
        {
            Json.decodeFromString<List<SensorDataStructure>>(jsonString)
        } catch (e: SerializationException)
        {
            e.printStackTrace()
            // Gestisci l'eccezione in base alle tue esigenze
            null
        }
    }
}