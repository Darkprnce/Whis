package com.whis.ui.screen.workoutlist.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.model.ExerciseListBean
import com.whis.model.WorkoutListBean
import com.whis.repository.ExerciseListRepository
import com.whis.repository.WorkoutAddRepository
import com.whis.repository.WorkoutListRepository
import com.whis.repository.WorkoutRemoveRepository
import com.whis.ui.customComposables.LazyAnimatedColumnAdapter
import com.whis.utils.SOME_ERROR_OCCURED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val workoutListRepository: WorkoutListRepository,
    private val exerciseListRepository: ExerciseListRepository,
    private val workoutAddRepository: WorkoutAddRepository,
    private val workoutRemoveRepository: WorkoutRemoveRepository,
) :
    ViewModel() {

    val workoutList: MutableLiveData<List<WorkoutListBean.Data?>?> =
        MutableLiveData(arrayListOf())

    var selectedWorkout = mutableStateOf(WorkoutListBean.Data())

    private var exerciseList: List<ExerciseListBean.Data?> = arrayListOf()
    var exercises_search = mutableStateOf<List<ExerciseListBean.Data?>>(listOf())
    val api_status: MutableLiveData<String> = MutableLiveData("")
    var workout_exercises = mutableStateListOf<ExerciseListBean.Data>()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        getWorkouts()
    }

    private fun getWorkouts(id: String? = null) {
        isLoading.postValue(true)
        val data = HashMap<String?, Any?>()
        data.put("username", "darkprnce")
        data.put("id", id)
        viewModelScope.launch(Dispatchers.IO) {
            val bean = workoutListRepository.getWorkoutList(data)
            if (bean != null) {
                if (workoutList.value!!.isEmpty()) {
                    getExercises()
                    workoutList.postValue(bean.data)
                } /*else {
                    for (item in bean.data!!) {
                        val find_item = workoutList.value!!.find { it!!.id == item!!.id }
                        if (find_item == null) {
                            addItem(item!!)
                        }
                    }

                    for (item in workoutList.value!!) {
                        val find_item = bean.data!!.find { it!!.id == item!!.id }
                        if (find_item == null) {
                            removeItem(workoutList.value!!.indexOf(item))
                        }
                    }
                }*/
                workoutList.postValue(bean.data)
                isLoading.postValue(false)
            } else {
                workoutList.postValue(arrayListOf())
                isLoading.postValue(false)
            }
        }
    }

    private fun getExercises() {
        val data = HashMap<String?, Any?>()
        data.put("username", "darkprnce")
        viewModelScope.launch(Dispatchers.IO) {
            val bean = exerciseListRepository.getExerciseList(data)
            if (bean != null) {
                exerciseList = bean.data!!
                exercises_search.value = bean.data!!
            } else {
                exerciseList = arrayListOf()
                exercises_search.value = mutableStateListOf()
            }
        }
    }

    fun setWorkout(workout: WorkoutListBean.Data?) {
        if (workout != null) {
            selectedWorkout.value = workout
            viewModelScope.launch(Dispatchers.Default) {
                if (exerciseList.isNotEmpty()) {
                    workout_exercises = mutableStateListOf()
                    for (item in workout.exercises_id!!) {
                        workout_exercises.add(exerciseList.find { it!!.id == item!!.id }!!)
                    }
                }
            }
        } else {
            selectedWorkout.value = WorkoutListBean.Data()
            workout_exercises = mutableStateListOf()
        }
        api_status.postValue("")
    }

    fun swapExercises(from: Int, to: Int) {
        workout_exercises = workout_exercises.apply {
            add(to, removeAt(from))
        }
    }

    fun searchExercise(it: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (it.isNotEmpty()) {
                exercises_search.value = exerciseList.filter { item ->
                    item!!.name!!.trim().contains(it,ignoreCase = true)
                            || item.bodypart!!.trim().contains(it,ignoreCase = true)
                            || item.equipment!!.trim().contains(it,ignoreCase = true)
                            || item.target!!.trim().contains(it,ignoreCase = true)
                }
            } else {
                exercises_search.value = exerciseList
            }
        }
    }

    fun addExercise(item: ExerciseListBean.Data) {
        viewModelScope.launch(Dispatchers.Default) {
            val find_item = workout_exercises.find { it.id == item.id }
            if (find_item != null) {
                workout_exercises.remove(find_item)
            } else {
                workout_exercises.add(item)
            }
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
                api_status.postValue(bean.msg!!)
                getWorkouts()
            } else {
                api_status.postValue(SOME_ERROR_OCCURED)
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
                api_status.postValue(bean.msg!!)
                getWorkouts()
            } else {
                api_status.postValue(SOME_ERROR_OCCURED)
            }
        }
    }
}