package com.team2.studentfitness.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitConversionsTest {

	private val unitConversions = UnitConversions()

	// ==================== WEIGHT CONVERSIONS ====================
	@Test
	fun kgToLb_returnExpectedValue() {
		assertEquals(154.3236, unitConversions.kgToLb(70.0), 0.0001)
	}

	@Test
	fun lbToKg_returnExpectedValue() {
		assertEquals(31.7515, unitConversions.lbToKg(70.0), 0.0001)
	}

	@Test
	fun kgAndLb_roundTrip_isConsistent() {
		val pounds = unitConversions.kgToLb(70.0)
		val kilograms = unitConversions.lbToKg(pounds)

		assertEquals(70.0, kilograms, 0.0001)
	}

	@Test
	fun weight_zero_returnZero() {
		assertEquals(0.0, unitConversions.kgToLb(0.0), 0.0001)
		assertEquals(0.0, unitConversions.lbToKg(0.0), 0.0001)
	}

	// ==================== HEIGHT CONVERSIONS (cm/inches) ====================
	@Test
	fun cmToInches_returnExpectedValue() {
		assertEquals(70.8661, unitConversions.cmToInches(180.0), 0.0001)
	}

	@Test
	fun inchesToCm_returnExpectedValue() {
		assertEquals(177.8, unitConversions.inchesToCm(70.0), 0.0001)
	}

	@Test
	fun cmAndInches_roundTrip_isConsistent() {
		val inches = unitConversions.cmToInches(180.0)
		val centimeters = unitConversions.inchesToCm(inches)

		assertEquals(180.0, centimeters, 0.0001)
	}

	@Test
	fun height_zero_returnZero() {
		assertEquals(0.0, unitConversions.cmToInches(0.0), 0.0001)
		assertEquals(0.0, unitConversions.inchesToCm(0.0), 0.0001)
	}

	// ==================== HEIGHT CONVERSIONS (feet/inches to cm) ====================
	@Test
	fun feetInchesToCm_returnExpectedValue() {
		assertEquals(177.8, unitConversions.feetInchesToCm(feet = 5, inches = 10.0), 0.0001)
	}

	@Test
	fun feetInchesToCm_zeroFeetAndInches_returnZero() {
		assertEquals(0.0, unitConversions.feetInchesToCm(feet = 0, inches = 0.0), 0.0001)
	}

	@Test
	fun feetInchesToCm_onlyFeet_returnExpectedValue() {
		assertEquals(182.88, unitConversions.feetInchesToCm(feet = 6, inches = 0.0), 0.0001)
	}

	@Test
	fun feetInchesToCm_onlyInches_returnExpectedValue() {
		assertEquals(25.4, unitConversions.feetInchesToCm(feet = 0, inches = 10.0), 0.0001)
	}

	// ==================== HEIGHT CONVERSIONS (cm to feet/inches) ====================
	@Test
	fun cmToFeetInches_returnExpectedValues() {
		val result = unitConversions.cmToFeetInches(177.8)
		assertEquals(5, result.feet)
		assertEquals(10.0, result.inches, 0.0001)
	}

	@Test
	fun cmToFeetInches_zeroHeight_returnZero() {
		val result = unitConversions.cmToFeetInches(0.0)
		assertEquals(0, result.feet)
		assertEquals(0.0, result.inches, 0.0001)
	}

	@Test
	fun feetInchesToCm_andBack_roundTrip() {
		val centimeters = unitConversions.feetInchesToCm(feet = 5, inches = 10.0)
		val converted = unitConversions.cmToFeetInches(centimeters)

		assertEquals(5, converted.feet)
		assertEquals(10.0, converted.inches, 0.0001)
	}

	// ==================== HEIGHT CONVERSIONS (inches to feet/inches) ====================
	@Test
	fun inchesToFeetInches_returnExpectedValues() {
		val result = unitConversions.inchesToFeetInches(70.0)
		assertEquals(5, result.feet)
		assertEquals(10.0, result.inches, 0.0001)
	}

	@Test
	fun inchesToFeetInches_zeroInches_returnZero() {
		val result = unitConversions.inchesToFeetInches(0.0)
		assertEquals(0, result.feet)
		assertEquals(0.0, result.inches, 0.0001)
	}

	@Test
	fun feetInchesToInches_returnExpectedValue() {
		assertEquals(70.0, unitConversions.feetInchesToInches(feet = 5, inches = 10.0), 0.0001)
	}

	@Test
	fun feetInchesToInches_zeroFeetAndInches_returnZero() {
		assertEquals(0.0, unitConversions.feetInchesToInches(feet = 0, inches = 0.0), 0.0001)
	}

	@Test
	fun inchesToFeetInches_andBack_roundTrip() {
		val inches = unitConversions.feetInchesToInches(feet = 5, inches = 10.0)
		val converted = unitConversions.inchesToFeetInches(inches)

		assertEquals(5, converted.feet)
		assertEquals(10.0, converted.inches, 0.0001)
	}

	// ==================== DISTANCE CONVERSIONS ====================
	@Test
	fun kmToMiles_returnExpectedValue() {
		assertEquals(3.1069, unitConversions.kmToMiles(5.0), 0.0001)
	}

	@Test
	fun milesToKm_returnExpectedValue() {
		assertEquals(8.0467, unitConversions.milesToKm(5.0), 0.0001)
	}

	@Test
	fun distance_roundTrip_isConsistent() {
		val miles = unitConversions.kmToMiles(5.0)
		val kilometers = unitConversions.milesToKm(miles)

		assertEquals(5.0, kilometers, 0.0001)
	}

	@Test
	fun distance_zero_returnZero() {
		assertEquals(0.0, unitConversions.kmToMiles(0.0), 0.0001)
		assertEquals(0.0, unitConversions.milesToKm(0.0), 0.0001)
	}

	// ==================== HYDRATION CONVERSIONS ====================
	@Test
	fun mlToFluidOz_returnExpectedValue() {
		assertEquals(16.9070, unitConversions.mlToFluidOz(500.0), 0.0001)
	}

	@Test
	fun fluidOzToMl_returnExpectedValue() {
		assertEquals(473.1765, unitConversions.fluidOzToMl(16.0), 0.0001)
	}

	@Test
	fun hydration_roundTrip_isConsistent() {
		val fluidOz = unitConversions.mlToFluidOz(500.0)
		val milliliters = unitConversions.fluidOzToMl(fluidOz)

		assertEquals(500.0, milliliters, 0.0001)
	}

	@Test
	fun hydration_zero_returnZero() {
		assertEquals(0.0, unitConversions.mlToFluidOz(0.0), 0.0001)
		assertEquals(0.0, unitConversions.fluidOzToMl(0.0), 0.0001)
	}

	// ==================== TEMPERATURE CONVERSIONS ====================
	@Test
	fun celsiusToFahrenheit_returnExpectedValue() {
		assertEquals(98.6, unitConversions.celsiusToFahrenheit(37.0), 0.0001)
	}

	@Test
	fun fahrenheitToCelsius_returnExpectedValue() {
		assertEquals(37.0, unitConversions.fahrenheitToCelsius(98.6), 0.0001)
	}

	@Test
	fun temperature_roundTrip_isConsistent() {
		val fahrenheit = unitConversions.celsiusToFahrenheit(37.0)
		val celsius = unitConversions.fahrenheitToCelsius(fahrenheit)

		assertEquals(37.0, celsius, 0.0001)
	}

	@Test
	fun temperature_zeroC_returnExpectedF() {
		assertEquals(32.0, unitConversions.celsiusToFahrenheit(0.0), 0.0001)
	}

	@Test
	fun temperature_freezing_freezing() {
		assertEquals(0.0, unitConversions.fahrenheitToCelsius(32.0), 0.0001)
	}

	@Test
	fun temperature_negative_celsius_returnExpectedF() {
		assertEquals(-40.0, unitConversions.celsiusToFahrenheit(-40.0), 0.0001)
	}

	// ==================== ENERGY CONVERSIONS ====================
	@Test
	fun kcalToKj_returnExpectedValue() {
		assertEquals(836.8, unitConversions.kcalToKj(200.0), 0.01)
	}

	@Test
	fun kjToKcal_returnExpectedValue() {
		assertEquals(200.24, unitConversions.kjToKcal(837.8), 0.01)
	}

	@Test
	fun energy_roundTrip_isConsistent() {
		val kj = unitConversions.kcalToKj(500.0)
		val kcal = unitConversions.kjToKcal(kj)

		assertEquals(500.0, kcal, 0.01)
	}

	@Test
	fun energy_zero_returnZero() {
		assertEquals(0.0, unitConversions.kcalToKj(0.0), 0.0001)
		assertEquals(0.0, unitConversions.kjToKcal(0.0), 0.0001)
	}

	// ==================== BLOOD PRESSURE CONVERSIONS ====================
	@Test
	fun mmHgToKPa_returnExpectedValue() {
		assertEquals(15.9987, unitConversions.mmHgToKPa(120.0), 0.0001)
	}

	@Test
	fun kPaToMmHg_returnExpectedValue() {
		assertEquals(120.0, unitConversions.kPaToMmHg(15.9987), 0.01)
	}

	@Test
	fun pressure_roundTrip_isConsistent() {
		val kPa = unitConversions.mmHgToKPa(120.0)
		val mmHg = unitConversions.kPaToMmHg(kPa)

		assertEquals(120.0, mmHg, 0.01)
	}

	@Test
	fun pressure_zero_returnZero() {
		assertEquals(0.0, unitConversions.mmHgToKPa(0.0), 0.0001)
		assertEquals(0.0, unitConversions.kPaToMmHg(0.0), 0.0001)
	}

	// ==================== GLUCOSE CONVERSIONS ====================
	@Test
	fun glucoseMgDlToMmolL_returnExpectedValue() {
		assertEquals(5.0, unitConversions.glucoseMgDlToMmolL(90.0), 0.0001)
	}

	@Test
	fun glucoseMmolLToMgDl_returnExpectedValue() {
		assertEquals(90.0, unitConversions.glucoseMmolLToMgDl(5.0), 0.0001)
	}

	@Test
	fun glucose_roundTrip_isConsistent() {
		val mmolL = unitConversions.glucoseMgDlToMmolL(180.0)
		val mgDl = unitConversions.glucoseMmolLToMgDl(mmolL)

		assertEquals(180.0, mgDl, 0.0001)
	}

	@Test
	fun glucose_zero_returnZero() {
		assertEquals(0.0, unitConversions.glucoseMgDlToMmolL(0.0), 0.0001)
		assertEquals(0.0, unitConversions.glucoseMmolLToMgDl(0.0), 0.0001)
	}

	// ==================== PACE CONVERSIONS ====================
	@Test
	fun paceMinPerKmToMinPerMile_returnExpectedValue() {
		assertEquals(8.0467, unitConversions.paceMinPerKmToMinPerMile(5.0), 0.0001)
	}

	@Test
	fun paceMinPerMileToMinPerKm_returnExpectedValue() {
		assertEquals(5.0, unitConversions.paceMinPerMileToMinPerKm(8.0467), 0.0001)
	}

	@Test
	fun pace_roundTrip_isConsistent() {
		val perMile = unitConversions.paceMinPerKmToMinPerMile(5.0)
		val perKm = unitConversions.paceMinPerMileToMinPerKm(perMile)

		assertEquals(5.0, perKm, 0.0001)
	}

	@Test
	fun pace_zero_returnZero() {
		assertEquals(0.0, unitConversions.paceMinPerKmToMinPerMile(0.0), 0.0001)
		assertEquals(0.0, unitConversions.paceMinPerMileToMinPerKm(0.0), 0.0001)
	}

	// ==================== VALIDATION TESTS ====================
	@Test
	fun negativeWeight_throws() {
		val thrown = runCatching { unitConversions.kgToLb(-1.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}

	@Test
	fun negativeHeight_throws() {
		val thrown = runCatching { unitConversions.cmToInches(-10.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}

	@Test
	fun negativeDistance_throws() {
		val thrown = runCatching { unitConversions.kmToMiles(-1.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}

	@Test
	fun negativeHydration_throws() {
		val thrown = runCatching { unitConversions.mlToFluidOz(-100.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}

	@Test
	fun negativeEnergy_throws() {
		val thrown = runCatching { unitConversions.kcalToKj(-10.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}

	@Test
	fun negativePressure_throws() {
		val thrown = runCatching { unitConversions.mmHgToKPa(-1.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}

	@Test
	fun negativeGlucose_throws() {
		val thrown = runCatching { unitConversions.glucoseMgDlToMmolL(-50.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}

	@Test
	fun negativePace_throws() {
		val thrown = runCatching { unitConversions.paceMinPerKmToMinPerMile(-5.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}

	@Test
	fun nanInput_throwsForAllFunctions() {
		val nan = Double.NaN
		assertTrue(runCatching { unitConversions.kgToLb(nan) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.cmToInches(nan) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.kmToMiles(nan) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.mlToFluidOz(nan) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.kcalToKj(nan) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.mmHgToKPa(nan) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.glucoseMgDlToMmolL(nan) }.exceptionOrNull() is IllegalArgumentException)
	}

	@Test
	fun infinityInput_throwsForAllFunctions() {
		val infinity = Double.POSITIVE_INFINITY
		assertTrue(runCatching { unitConversions.kgToLb(infinity) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.cmToInches(infinity) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.kmToMiles(infinity) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.mlToFluidOz(infinity) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.kcalToKj(infinity) }.exceptionOrNull() is IllegalArgumentException)
		assertTrue(runCatching { unitConversions.mmHgToKPa(infinity) }.exceptionOrNull() is IllegalArgumentException)
	}

	@Test
	fun temperature_allowsNegativeValues() {
		val result = unitConversions.celsiusToFahrenheit(-273.15)
		assertEquals(-459.67, result, 0.01)
	}

	@Test
	fun negativeFeetInches_throwsForFeet() {
		val thrown = runCatching { unitConversions.feetInchesToCm(feet = -1, inches = 5.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}

	@Test
	fun negativeFeetInches_throwsForInches() {
		val thrown = runCatching { unitConversions.feetInchesToCm(feet = 5, inches = -2.0) }.exceptionOrNull()
		assertTrue(thrown is IllegalArgumentException)
	}
}

