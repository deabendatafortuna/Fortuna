package com.example.myfirstapp

import kotlinx.serialization.Serializable

@Serializable
data class SensorDataStructure(
    val acc0: Int,
    val acc1: Int,
    val acc2: Int,
    val gyro0: Int,
    val gyro1: Int,
    val gyro2: Int,
    val id: Int,
    val timestamp: Int
    )
