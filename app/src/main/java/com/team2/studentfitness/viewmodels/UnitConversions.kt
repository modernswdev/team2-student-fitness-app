package com.team2.studentfitness.viewmodels

class UnitConversions {
	data class FeetInches(
		val feet: Int,
		val inches: Double
	)

	fun kgToLb(kilograms: Double): Double {
		requireNonNegativeFinite(kilograms, "kilograms")
		return kilograms * KG_TO_LB
	}

	fun lbToKg(pounds: Double): Double {
		requireNonNegativeFinite(pounds, "pounds")
		return pounds * LB_TO_KG
	}

	fun cmToInches(centimeters: Double): Double {
		requireNonNegativeFinite(centimeters, "centimeters")
		return centimeters / CM_PER_INCH
	}

	fun inchesToCm(inches: Double): Double {
		requireNonNegativeFinite(inches, "inches")
		return inches * CM_PER_INCH
	}

	fun feetInchesToCm(feet: Int, inches: Double): Double {
		require(feet >= 0) { "feet must be greater than or equal to 0" }
		requireNonNegativeFinite(inches, "inches")

		val totalInches = (feet * INCHES_PER_FOOT) + inches
		return inchesToCm(totalInches)
	}

    fun inchesToFeetInches(inches: Double): FeetInches {
        requireNonNegativeFinite(inches, "inches")
        val feet = (inches / INCHES_PER_FOOT).toInt()
        val remainingInches = inches - (feet * INCHES_PER_FOOT)
        return FeetInches(feet = feet, inches = remainingInches)
    }

    fun feetInchesToInches(feet: Int, inches: Double): Double {
        require(feet >= 0) { "feet must be greater than or equal to 0" }
        requireNonNegativeFinite(inches, "inches")
        return (feet * INCHES_PER_FOOT) + inches
    }

	fun cmToFeetInches(centimeters: Double): FeetInches {
		requireNonNegativeFinite(centimeters, "centimeters")

		val totalInches = cmToInches(centimeters)
		val feet = (totalInches / INCHES_PER_FOOT).toInt()
		val inches = totalInches - (feet * INCHES_PER_FOOT)
		return FeetInches(feet = feet, inches = inches)
	}

	fun kmToMiles(kilometers: Double): Double {
		requireNonNegativeFinite(kilometers, "kilometers")
		return kilometers * KM_TO_MILES
	}

	fun milesToKm(miles: Double): Double {
		requireNonNegativeFinite(miles, "miles")
		return miles * MILES_TO_KM
	}

	fun mlToFluidOz(milliliters: Double): Double {
		requireNonNegativeFinite(milliliters, "milliliters")
		return milliliters / ML_PER_FL_OZ
	}

	fun fluidOzToMl(fluidOunces: Double): Double {
		requireNonNegativeFinite(fluidOunces, "fluidOunces")
		return fluidOunces * ML_PER_FL_OZ
	}

	fun celsiusToFahrenheit(celsius: Double): Double {
		requireFinite(celsius, "celsius")
		return (celsius * 9.0 / 5.0) + 32.0
	}

	fun fahrenheitToCelsius(fahrenheit: Double): Double {
		requireFinite(fahrenheit, "fahrenheit")
		return (fahrenheit - 32.0) * 5.0 / 9.0
	}

	fun kcalToKj(kcal: Double): Double {
		requireNonNegativeFinite(kcal, "kcal")
		return kcal * KCAL_TO_KJ
	}

	fun kjToKcal(kj: Double): Double {
		requireNonNegativeFinite(kj, "kj")
		return kj * KJ_TO_KCAL
	}

	fun mmHgToKPa(mmHg: Double): Double {
		requireNonNegativeFinite(mmHg, "mmHg")
		return mmHg * MMHG_TO_KPA
	}

	fun kPaToMmHg(kPa: Double): Double {
		requireNonNegativeFinite(kPa, "kPa")
		return kPa * KPA_TO_MMHG
	}

	fun glucoseMgDlToMmolL(mgDl: Double): Double {
		requireNonNegativeFinite(mgDl, "mgDl")
		return mgDl / GLUCOSE_MGDL_PER_MMOLL
	}

	fun glucoseMmolLToMgDl(mmolL: Double): Double {
		requireNonNegativeFinite(mmolL, "mmolL")
		return mmolL * GLUCOSE_MGDL_PER_MMOLL
	}

	fun paceMinPerKmToMinPerMile(minPerKm: Double): Double {
		requireNonNegativeFinite(minPerKm, "minPerKm")
		return minPerKm * MILES_TO_KM
	}

	fun paceMinPerMileToMinPerKm(minPerMile: Double): Double {
		requireNonNegativeFinite(minPerMile, "minPerMile")
		return minPerMile * KM_TO_MILES
	}

	private fun requireFinite(value: Double, name: String) {
		require(value.isFinite()) { "$name must be a finite number" }
	}

	private fun requireNonNegativeFinite(value: Double, name: String) {
		requireFinite(value, name)
		require(value >= 0.0) { "$name must be greater than or equal to 0" }
	}

	companion object {
		private const val KG_TO_LB = 2.2046226218
		private const val LB_TO_KG = 0.45359237
		private const val CM_PER_INCH = 2.54
		private const val INCHES_PER_FOOT = 12.0
		private const val KM_TO_MILES = 0.6213711922
		private const val MILES_TO_KM = 1.609344
		private const val ML_PER_FL_OZ = 29.5735295625
		private const val KCAL_TO_KJ = 4.184
		private const val KJ_TO_KCAL = 0.2390057361
		private const val MMHG_TO_KPA = 0.1333223684
		private const val KPA_TO_MMHG = 7.500616827
		private const val GLUCOSE_MGDL_PER_MMOLL = 18.0
	}
}