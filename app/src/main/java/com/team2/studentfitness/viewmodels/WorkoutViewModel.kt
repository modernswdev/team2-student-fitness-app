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

    // --- State for Building Workout ---
    private val _newWorkoutExercises = MutableStateFlow<List<Exercises>>(emptyList())
    val newWorkoutExercises: StateFlow<List<Exercises>> = _newWorkoutExercises

    private val _categoryExercises = MutableStateFlow<List<Exercises>>(emptyList())
    val categoryExercises: StateFlow<List<Exercises>> = _categoryExercises

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
            val workExList = workExDao.getAll().filter { it.userID == workout.uid }.sortedBy { it.order }
            val pairs = workExList.mapNotNull { workEx ->
                val exercise = exercisesDao.getById(workEx.exerciseID)
                if (exercise != null) workEx to exercise else null
            }
            _selectedWorkoutExercises.value = pairs
        }
    }

    // --- Building Workout Methods ---

    fun loadExercisesByCategory(muscleGroup: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _categoryExercises.value = exercisesDao.getByMuscleGroup(muscleGroup)
            _isLoading.value = false
        }
    }

    fun addExerciseToNewWorkout(exercise: Exercises) {
        // Only add if not already present
        if (!_newWorkoutExercises.value.any { it.uid == exercise.uid }) {
            _newWorkoutExercises.value = _newWorkoutExercises.value + exercise
        }
    }

    fun removeExerciseFromNewWorkout(exercise: Exercises) {
        _newWorkoutExercises.value = _newWorkoutExercises.value.filter { it.uid != exercise.uid }
    }

    fun clearNewWorkout() {
        _newWorkoutExercises.value = emptyList()
    }

    fun saveCustomWorkout(name: String, onComplete: () -> Unit) {
        val exercises = _newWorkoutExercises.value
        if (exercises.isEmpty()) return

        viewModelScope.launch {
            // Calculate Difficulty (Average)
            val avgDifficulty = if (exercises.isNotEmpty()) {
                exercises.map { it.difficulty }.average().toInt().coerceIn(0, 2)
            } else 0
            
            // Determine Focus (Most frequent muscle group or "Mixed")
            val muscleGroups = exercises.map { it.muscleGroup }
            val focus = if (muscleGroups.distinct().size == 1) muscleGroups.first() else "Mixed"
            
            // Estimate Duration (e.g., 10 mins per exercise)
            val duration = exercises.size * 10

            // Create Workout
            val newWorkout = Workouts(
                uid = 0, // Auto-generate
                name = name,
                duration = duration,
                focus = focus,
                type = 2, // Default to Strength for custom
                difficulty = avgDifficulty,
                split = focus
            )

            // Insert Workout and get ID
            val newWorkoutId = workoutsDao.insert(newWorkout).toInt()
            
            // Insert WorkEx entries
            exercises.forEachIndexed { index, exercise ->
                val workEx = WorkEx(
                    uid = 0,
                    workoutName = name,
                    userID = newWorkoutId,
                    exerciseID = exercise.uid,
                    reps = 10, // Default values
                    sets = 3,
                    restTime = 60,
                    order = index
                )
                workExDao.insert(workEx)
            }
            
            clearNewWorkout()
            // Reload workouts to include the new one
            loadWorkoutsByDifficulty(avgDifficulty)
            onComplete()
        }
    }
}
