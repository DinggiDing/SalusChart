package com.hdil.saluschart.data.model

sealed class HealthData {
    data class StepCount(val count: Int) : HealthData()

    data class HeartRate(val bpm: Int) : HealthData()

    data class BloodPressure(val systolic: Int, val diastolic: Int) : HealthData()

    data class SleepDuration(val hours: Double) : HealthData()

    data class Weight(val kg: Double) : HealthData()

    data class GlucoseLevel(val mgDl: Double) : HealthData()

    data class OxygenSaturation(val percentage: Double) : HealthData()
}