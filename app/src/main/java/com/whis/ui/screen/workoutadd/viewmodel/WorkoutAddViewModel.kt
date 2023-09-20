package com.whis.ui.screen.workoutadd.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.model.ExerciseListBean
import com.whis.model.WorkoutListBean
import com.whis.repository.ExerciseListRepository
import com.whis.repository.WorkoutAddRepository
import com.whis.repository.WorkoutRemoveRepository
import com.whis.utils.SOME_ERROR_OCCURED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutAddViewModel @Inject constructor(
    private val workoutAddRepository: WorkoutAddRepository,
    private val workoutRemoveRepository: WorkoutRemoveRepository,
    private val exerciseListRepository: ExerciseListRepository
) : ViewModel() {

    private var exerciseList: List<ExerciseListBean.Data?> = arrayListOf()
    var exercises_search = mutableStateListOf<ExerciseListBean.Data?>()
    val add_status: MutableLiveData<String> = MutableLiveData("")
    var workout_exercises = mutableStateListOf<ExerciseListBean.Data>()
    var selectedWorkout: WorkoutListBean.Data = WorkoutListBean.Data()

    init {
        getExercises()
    }

    fun setWorkout(workout: WorkoutListBean.Data?) {
        Log.e("TAG", "setWorkout: Edit")
        if (workout != null) {
            selectedWorkout = workout
            if (exerciseList.isNotEmpty()) {
                workout_exercises= mutableStateListOf()
                for (item in selectedWorkout.exercises_id!!) {
                    workout_exercises.add(exerciseList.find { it!!.id == item!!.id }!!)
                }
            }
        } else {
            selectedWorkout = WorkoutListBean.Data()
            workout_exercises = mutableStateListOf()
        }
    }


    private fun getExercises() {
        val data = HashMap<String?, Any?>()
        data.put("username", "darkprnce")
        viewModelScope.launch(Dispatchers.IO) {
            val bean = exerciseListRepository.getExerciseList(data)
            if (bean != null) {
                exerciseList = bean.data!!
                exercises_search = bean.data!!.toMutableStateList()
            } else {
                exerciseList=arrayListOf()
                exercises_search = mutableStateListOf()
            }
        }
    }

    fun swapExercises(from: Int, to: Int) {
        workout_exercises = workout_exercises.apply {
            add(to, removeAt(from))
        }
    }

    fun searchExercise(it: String) {
        if (it.isNotEmpty()) {
            exercises_search = exerciseList.filter { item ->
                item!!.name!!.contains(it)
                        || item.bodypart!!.contains(it)
                        || item.equipment!!.contains(it)
                        || item.target!!.contains(it)
            }.toMutableStateList()
        } else {
            exercises_search = exerciseList.toMutableStateList()
        }
    }

    fun addExercise(item: ExerciseListBean.Data) {
        val find_item = workout_exercises.find { it.id == item.id }
        if (find_item != null) {
            workout_exercises.remove(find_item)
        } else {
            workout_exercises.add(item)
        }
    }

    fun exerciseExist(item: ExerciseListBean.Data): Boolean {
        val find_item = workout_exercises.find { it.id == item.id }
        return find_item != null
    }

    fun addWorkout(data: HashMap<String?, Any?>) {
        data.put("username", "darkprnce")
        for (item in data.values) {
            if (item is String || item is Int || item is Double || item is Float) {
                item.toString().trim()
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val bean = workoutAddRepository.addWorkout(data)
            if (bean != null) {
                add_status.postValue(bean.msg!!)
            } else {
                add_status.postValue(SOME_ERROR_OCCURED)
            }
        }
    }

    fun removeWorkout(data: HashMap<String?, Any?>) {
        data.put("username", "darkprnce")
        for (item in data.values) {
            if (item is String || item is Int || item is Double || item is Float) {
                item.toString().trim()
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val bean = workoutRemoveRepository.removeWorkout(data)
            if (bean != null) {
                add_status.postValue(bean.msg!!)
            } else {
                add_status.postValue(SOME_ERROR_OCCURED)
            }
        }
    }


}