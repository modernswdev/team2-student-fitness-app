package com.team2.studentfitness.viewmodels

import kotlin.math.roundToInt

class HealthCalculations {

	enum class Sex {
		MALE,
		FEMALE
	}

	enum class ActivityLevel(val tdeeMultiplier: Double) {
		SEDENTARY(1.2),
		LIGHT(1.375),
		MODERATE(1.55),
		VERY_ACTIVE(1.725),
		EXTRA_ACTIVE(1.9)
	}

	enum class DriActivityLevel(private val maleFactor: Double, private val femaleFactor: Double) {
		SEDENTARY(1.0, 1.0),
		LOW_ACTIVE(1.11, 1.12),
		ACTIVE(1.25, 1.27),
		VERY_ACTIVE(1.48, 1.45);

		fun factorFor(sex: Sex): Double {
			return if (sex == Sex.MALE) maleFactor else femaleFactor
		}
	}

	enum class ProteinGoal(val gramsPerKg: Double) {
		GENERAL_HEALTH(0.8),
		FITNESS(1.2),
		MUSCLE_GAIN(1.6),
		FAT_LOSS(2.0)
	}

	data class HeartRateZone(
		val minBpm: Int,
		val maxBpm: Int
	)

	fun calculateBmi(weightKg: Double, heightCm: Double): Double {
		requirePositive(weightKg, "weightKg")
		requirePositive(heightCm, "heightCm")

		val heightMeters = heightCm / 100.0
		return weightKg / (heightMeters * heightMeters)
	}

	fun classifyBmi(bmi: Double): String {
		requirePositive(bmi, "bmi")

		return when {
			bmi < 18.5 -> "Underweight"
			bmi < 25.0 -> "Normal"
			bmi < 30.0 -> "Overweight"
			else -> "Obese"
		}
	}

	fun calculateBmrMifflinStJeor(
		weightKg: Double,
		heightCm: Double,
		ageYears: Int,
		sex: Sex
	): Double {
		requirePositive(weightKg, "weightKg")
		requirePositive(heightCm, "heightCm")
		require(ageYears > 0) { "ageYears must be greater than 0" }

		val base = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * ageYears)
		return if (sex == Sex.MALE) base + 5.0 else base - 161.0
	}

	fun calculateTdee(bmrCalories: Double, activityLevel: ActivityLevel): Double {
		requirePositive(bmrCalories, "bmrCalories")
		return bmrCalories * activityLevel.tdeeMultiplier
	}

	fun calculateDriEstimatedEnergy(
		ageYears: Int,
		sex: Sex,
		weightKg: Double,
		heightCm: Double,
		activityLevel: DriActivityLevel
	): Double {
		require(ageYears > 0) { "ageYears must be greater than 0" }
		requirePositive(weightKg, "weightKg")
		requirePositive(heightCm, "heightCm")

		val heightMeters = heightCm / 100.0
		val activityFactor = activityLevel.factorFor(sex)

		return if (sex == Sex.MALE) {
			662.0 - (9.53 * ageYears) + activityFactor * ((15.91 * weightKg) + (539.6 * heightMeters))
		} else {
			354.0 - (6.91 * ageYears) + activityFactor * ((9.36 * weightKg) + (726.0 * heightMeters))
		}
	}

	fun calculateDailyProteinGrams(weightKg: Double, goal: ProteinGoal): Double {
		requirePositive(weightKg, "weightKg")
		return weightKg * goal.gramsPerKg
	}

	fun calculateDailyWaterMl(weightKg: Double): Int {
		requirePositive(weightKg, "weightKg")
		return (weightKg * 35.0).roundToInt()
	}

	fun calculateTargetHeartRateZone(
		ageYears: Int,
		lowerIntensity: Double = 0.5,
		upperIntensity: Double = 0.85
	): HeartRateZone {
		require(ageYears > 0) { "ageYears must be greater than 0" }
		require(lowerIntensity > 0.0 && lowerIntensity < 1.0) {
			"lowerIntensity must be between 0 and 1"
		}
		require(upperIntensity > 0.0 && upperIntensity <= 1.0) {
			"upperIntensity must be between 0 and 1"
		}
		require(lowerIntensity < upperIntensity) { "lowerIntensity must be less than upperIntensity" }

		val maxHeartRate = 220 - ageYears
		return HeartRateZone(
			minBpm = (maxHeartRate * lowerIntensity).roundToInt(),
			maxBpm = (maxHeartRate * upperIntensity).roundToInt()
		)
	}

	fun calculateWaistToHeightRatio(waistCm: Double, heightCm: Double): Double {
		requirePositive(waistCm, "waistCm")
		requirePositive(heightCm, "heightCm")
		return waistCm / heightCm
	}

	private fun requirePositive(value: Double, name: String) {
		require(value > 0.0) { "$name must be greater than 0" }
	}
}