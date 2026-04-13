package com.team2.studentfitness.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.team2.studentfitness.DatabaseCreation
import com.team2.studentfitness.database.Exercises
import com.team2.studentfitness.database.WorkEx
import com.team2.studentfitness.database.Workouts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as DatabaseCreation).database
    private val workoutsDao = database.workoutsDao()
    private val exercisesDao = database.exercisesDao()
    private val workExDao = database.workExDao()

    private val _workouts = MutableStateFlow<List<Workouts>>(emptyList())
    val workouts: StateFlow<List<Workouts>> = _workouts

    private val _selectedWorkoutExercises = MutableStateFlow<List<Pair<WorkEx, Exercises>>>(emptyList())
    val selectedWorkoutExercises: StateFlow<List<Pair<WorkEx, Exercises>>> = _selectedWorkoutExercises

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadWorkoutsByDifficulty(difficulty: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _workouts.value = workoutsDao.getByDifficulty(difficulty)
            _isLoading.value = false
        }
    }

    fun loadWorkoutsByMuscleGroup(muscleGroup: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Find exercises with this muscle group
            val exercises = exercisesDao.getByMuscleGroup(muscleGroup)
            val exerciseIds = exercises.map { it.uid }.toSet()
            
            // Find all WorkEx that use these exercises
            val allWorkEx = workExDao.getAll()
            val workoutIds = allWorkEx.filter { it.exerciseID in exerciseIds }.map { it.userID }.toSet()
            
            // Get the actual workouts
            val allWorkouts = workoutsDao.getAll()
            _workouts.value = allWorkouts.filter { it.uid in workoutIds }
            _isLoading.value = false
        }
    }

    fun loadExercisesForWorkout(workout: Workouts) {
        viewModelScope.launch {
            // In WorkEx, workoutID is stored in the 'userID' field based on the Entity definition
            val workExList = workExDao.getAll().filter { it.userID == workout.uid }.sortedBy { it.order }
            val pairs = workExList.mapNotNull { workEx ->
                val exercise = exercisesDao.getById(workEx.exerciseID)
                if (exercise != null) workEx to exercise else null
            }
            _selectedWorkoutExercises.value = pairs
        }
    }
}
