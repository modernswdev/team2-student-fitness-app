package com.example.healthapitest

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.Duration

/**
 * Health Connect Manager implementation
 * Provides health data for the app using the real Health Connect API
 */
class HealthConnectManager(private val context: Context) {

    suspend fun readAllHealthData(): HealthDataResult {
        val result = HealthDataResult()

        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val endTime = Instant.now()
            val startTime = endTime.minus(Duration.ofDays(90)) // Extended to 90 days

            // Read Steps
            result.steps = readSteps(client, startTime, endTime)
            // Read Heart Rate
            result.heartRate = readHeartRate(client, startTime, endTime)
            // Read Distance
            result.distance = readDistance(client, startTime, endTime)
            // Read Calories Burned
            result.caloriesBurned = readCaloriesBurned(client, startTime, endTime)
            // Read Body Temperature
            result.bodyTemperature = readBodyTemperature(client, startTime, endTime)
            // Read Blood Pressure
            result.bloodPressure = readBloodPressure(client, startTime, endTime)
            // Read Blood Glucose
            result.bloodGlucose = readBloodGlucose(client, startTime, endTime)
            // Read Oxygen Saturation
            result.oxygenSaturation = readOxygenSaturation(client, startTime, endTime)
            // Read Sleep
            result.sleep = readSleep(client, startTime, endTime)
            // Read Weight
            result.weight = readWeight(client, startTime, endTime)
            // Read Height
            result.height = readHeight(client, startTime, endTime)

            result.isSuccess = true
            result
        } catch (e: Exception) {
            result.apply {
                isSuccess = false
                errorMessage = "Error reading health data: ${e.message}"
            }
        }
    }

    private suspend fun readSteps(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                "Steps: ${record.count} steps at ${record.startTime}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun readHeartRate(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            Log.d("HC", "Heart Rate: Found ${response.records.size} records")
            if (response.records.isEmpty()) {
                Log.d("HC", "Heart Rate: No records found in range $startTime to $endTime")
            }
            response.records.map { record ->
                val samples = record.samples.map { it.beatsPerMinute }.joinToString(", ")
                "Heart Rate: $samples BPM at ${record.startTime}"
            }
        } catch (e: Exception) {
            Log.d("HC", "Heart Rate Error: ${e.javaClass.simpleName} - ${e.message}")
            emptyList()
        }
    }

    private suspend fun readDistance(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    DistanceRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                val distanceInKm = record.distance.inKilometers
                "Distance: $distanceInKm km at ${record.startTime}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun readCaloriesBurned(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.mapNotNull { record ->
                record.title?.let { title ->
                    "Exercise: $title from ${record.startTime} to ${record.endTime}"
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun readBodyTemperature(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    BodyTemperatureRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                val tempInCelsius = record.temperature.inCelsius
                "Body Temperature: $tempInCelsius °C at ${record.time}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun readBloodPressure(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    BloodPressureRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                val systolic = record.systolic.inMillimetersOfMercury
                val diastolic = record.diastolic.inMillimetersOfMercury
                "BP: $systolic/$diastolic mmHg at ${record.time}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun readBloodGlucose(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    BloodGlucoseRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                val glucoseValue = record.level.inMillimolesPerLiter
                val relationToMeal = record.relationToMeal.toString()
                "Blood Glucose: $glucoseValue mmol/L at ${record.time} ($relationToMeal)"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun readOxygenSaturation(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                val spO2 = record.percentage.value
                "SpO2: $spO2% at ${record.time}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun readSleep(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                val duration = Duration.between(record.startTime, record.endTime)
                val minutes = duration.toMinutes()
                val hours = minutes / 60
                "Sleep: $minutes minutes ($hours hours) from ${record.startTime}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun readWeight(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    WeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                val weightInKg = record.weight.inKilograms
                "Weight: $weightInKg kg at ${record.time}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun readHeight(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<String> {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    HeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                val heightInMeters = record.height.inMeters
                "Height: $heightInMeters m at ${record.time}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }


    data class HealthDataResult(
        var isSuccess: Boolean = false,
        var errorMessage: String = "",
        var steps: List<String> = emptyList(),
        var heartRate: List<String> = emptyList(),
        var distance: List<String> = emptyList(),
        var caloriesBurned: List<String> = emptyList(),
        var bodyTemperature: List<String> = emptyList(),
        var bloodPressure: List<String> = emptyList(),
        var bloodGlucose: List<String> = emptyList(),
        var oxygenSaturation: List<String> = emptyList(),
        var sleep: List<String> = emptyList(),
        var weight: List<String> = emptyList(),
        var height: List<String> = emptyList()
    )
}
