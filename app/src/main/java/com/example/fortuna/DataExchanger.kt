package com.example.myfirstapp

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.fortuna.R
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException

class DataExchanger(context: Context)
{
    private val _context = context

    public fun leggiDaJson(resourceId: Int): List<SensorDataStructure>?
    {
        val jsonString =
            _context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
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