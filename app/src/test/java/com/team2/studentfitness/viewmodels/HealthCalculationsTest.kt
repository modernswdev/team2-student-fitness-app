package com.team2.studentfitness.viewmodels

import com.team2.studentfitness.viewmodels.HealthCalculations.ActivityLevel
import com.team2.studentfitness.viewmodels.HealthCalculations.DriActivityLevel
import com.team2.studentfitness.viewmodels.HealthCalculations.ProteinGoal
import com.team2.studentfitness.viewmodels.HealthCalculations.Sex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthCalculationsTest {

    private val healthCalculations = HealthCalculations()

    @Test
    fun bmi_returnsExpectedValue() {
        val bmi = healthCalculations.calculateBmi(weightKg = 70.0, heightCm = 175.0)

        assertEquals(22.86, bmi, 0.01)
        assertEquals("Normal", healthCalculations.classifyBmi(bmi))
    }

    @Test
    fun bmr_and_tdee_returnExpectedValues() {
        val bmr = healthCalculations.calculateBmrMifflinStJeor(
            weightKg = 70.0,
            heightCm = 175.0,
            ageYears = 25,
            sex = Sex.MALE
        )
        val tdee = healthCalculations.calculateTdee(bmr, ActivityLevel.MODERATE)

        assertEquals(1673.75, bmr, 0.01)
        assertEquals(2594.31, tdee, 0.01)
    }

    @Test
    fun dri_estimatedEnergy_returnsExpectedValue() {
        val calories = healthCalculations.calculateDriEstimatedEnergy(
            ageYears = 25,
            sex = Sex.FEMALE,
            weightKg = 60.0,
            heightCm = 165.0,
            activityLevel = DriActivityLevel.ACTIVE
        )

        assertEquals(2415.81, calories, 0.01)
    }

    @Test
    fun protein_water_and_ratio_returnExpectedValues() {
        val protein = healthCalculations.calculateDailyProteinGrams(70.0, ProteinGoal.MUSCLE_GAIN)
        val waterMl = healthCalculations.calculateDailyWaterMl(70.0)
        val whtr = healthCalculations.calculateWaistToHeightRatio(80.0, 175.0)

        assertEquals(112.0, protein, 0.001)
        assertEquals(2450, waterMl)
        assertEquals(0.4571, whtr, 0.0001)
    }

    @Test
    fun targetHeartRateZone_returnsExpectedRange() {
        val zone = healthCalculations.calculateTargetHeartRateZone(ageYears = 20)

        assertEquals(100, zone.minBpm)
        assertEquals(170, zone.maxBpm)
    }

    @Test(expected = IllegalArgumentException::class)
    fun bmi_throwsForInvalidWeight() {
        healthCalculations.calculateBmi(weightKg = 0.0, heightCm = 175.0)
    }

    @Test
    fun invalidHeartRateIntensity_throws() {
        val thrown = runCatching {
            healthCalculations.calculateTargetHeartRateZone(
                ageYears = 30,
                lowerIntensity = 0.9,
                upperIntensity = 0.8
            )
        }.exceptionOrNull()

        assertTrue(thrown is IllegalArgumentException)
    }
}

