package com.whis.ui.screen.workoutadd.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.Network.ValidationState
import com.whis.model.ExerciseListBean
import com.whis.model.WorkoutListBean
import com.whis.repository.ExerciseListRepository
import com.whis.repository.WorkoutRepository
import com.whis.utils.SOME_ERROR_OCCURED
import com.whis.utils.checkString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutAddViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseListRepository: ExerciseListRepository
) : ViewModel() {

    private val _showLoadingFlow = MutableStateFlow(false)
    val showLoading = _showLoadingFlow.asStateFlow()

    private var exerciseList: List<ExerciseListBean.Data?> = arrayListOf()

    //var exercisesSearch = mutableStateOf<List<ExerciseListBean.Data?>>(listOf())
    private val _exercisesSearchFlow = MutableStateFlow<SnapshotStateList<ExerciseListBean.Data?>>(mutableStateListOf())
    val exercisesSearch = _exercisesSearchFlow.asStateFlow()

    //var workoutExercises = mutableStateListOf<ExerciseListBean.Data>()
//    private val _workoutExercisesFlow = MutableStateFlow<List<ExerciseListBean.Data>>(listOf())
//    val workoutExercises = _workoutExercisesFlow.asStateFlow()
    private val _workoutExercisesFlow = MutableStateFlow<SnapshotStateList<ExerciseListBean.Data>>(
        mutableStateListOf()
    )
    val workoutExercises = _workoutExercisesFlow.asStateFlow()

    private val _apiState = MutableStateFlow<ValidationState>(ValidationState.Ideal)
    val apiState = _apiState.asStateFlow()

    //var selectedWorkout = mutableStateOf(WorkoutListBean.Data())

    private val _selectedWorkout = MutableStateFlow(WorkoutListBean.Data())
    val selectedWorkout = _selectedWorkout.asStateFlow()

    private val _titleInputFlow = MutableStateFlow("")
    val titleInputFlow = _titleInputFlow.asStateFlow()

    private val _totalTimeInputFlow = MutableStateFlow("")

    //val totalTimeInputFlow: StateFlow<String> get() = _totalTimeInputFlow
    val totalTimeInputFlow = _totalTimeInputFlow.asStateFlow()

    private val _userTimeInputFlow = MutableStateFlow("")
    val userTimeInputFlow = _userTimeInputFlow.asStateFlow()

    private val _heartrateMaxInputFlow = MutableStateFlow("")
    val heartrateMaxInputFlow = _heartrateMaxInputFlow.asStateFlow()

    private val _heartrateMinInputFlow = MutableStateFlow("")
    val heartrateMinInputFlow = _heartrateMinInputFlow.asStateFlow()

    private val _stressInputFlow = MutableStateFlow("")
    val stressInputFlow = _stressInputFlow.asStateFlow()

    private val _spo2InputFlow = MutableStateFlow("")
    val spo2InputFlow = _spo2InputFlow.asStateFlow()

    private val _calorieInputFlow = MutableStateFlow("")
    val calorieInputFlow = _calorieInputFlow.asStateFlow()

    private val _musicurlInputFlow = MutableStateFlow("")
    val musicurlInputFlow = _musicurlInputFlow.asStateFlow()

    private val _imageurlInputFlow = MutableStateFlow("")
    val imageurlInputFlow = _imageurlInputFlow.asStateFlow()

    private val _searchInputFlow = MutableStateFlow("")
    val searchInputFlow = _searchInputFlow.asStateFlow()

    private val _showRemoveWorkoutFlow = MutableStateFlow(false)
    val showRemoveWorkout = _showRemoveWorkoutFlow.asStateFlow()

    private val _showAddExerciseFlow = MutableStateFlow(false)
    val showAddExercise = _showAddExerciseFlow.asStateFlow()

    private val _showExerciseMoveFlow = MutableStateFlow(false)
    val showExerciseMove = _showExerciseMoveFlow.asStateFlow()


    fun setShowLoading(value: Boolean) {
        _showLoadingFlow.value = value
    }
    fun setTitle(value: String) {
        _titleInputFlow.value = value
    }

    fun setTotalTime(value: String) {
        _totalTimeInputFlow.value = value
    }

    fun setUserTime(value: String) {
        _userTimeInputFlow.value = value
    }

    fun setHeartrateMax(value: String) {
        _heartrateMaxInputFlow.value = value
    }

    fun setHeartrateMin(value: String) {
        _heartrateMinInputFlow.value = value
    }

    fun setStress(value: String) {
        _stressInputFlow.value = value
    }

    fun setSpo2(value: String) {
        _spo2InputFlow.value = value
    }

    fun setCalorie(value: String) {
        _calorieInputFlow.value = value
    }

    fun setMusicUrl(value: String) {
        _musicurlInputFlow.value = value
    }

    fun setImageUrl(value: String) {
        _imageurlInputFlow.value = value
    }

    fun setshowRemoveWorkout(value: Boolean) {
        _showRemoveWorkoutFlow.value = value
    }

    fun setshowAddExercise(value: Boolean) {
        _showAddExerciseFlow.value = value
    }

    fun setShowExerciseMove(value: Boolean) {
        _showExerciseMoveFlow.value = value
    }

    fun setWorkout(workout: WorkoutListBean.Data?) {
        _apiState.value = ValidationState.Ideal
        if (workout != null) {
            _selectedWorkout.value = workout
            setData()
        } else {
            _selectedWorkout.value = WorkoutListBean.Data()
        }
        getExercises()
    }

    private fun setData() {
        setTitle(checkString(_selectedWorkout.value.title, isempty = true))
        setTotalTime(checkString(_selectedWorkout.value.total_time, isempty = true))
        setUserTime(checkString(_selectedWorkout.value.user_time, isempty = true))
        setHeartrateMax(checkString(_selectedWorkout.value.heartrate_max, isempty = true))
        setHeartrateMin(checkString(_selectedWorkout.value.heartrate_min, isempty = true))
        setStress(checkString(_selectedWorkout.value.stress, isempty = true))
        setSpo2(checkString(_selectedWorkout.value.spo2, isempty = true))
        setCalorie(checkString(_selectedWorkout.value.calorie, isempty = true))
        setMusicUrl(checkString(_selectedWorkout.value.music_url, isempty = true))
        setImageUrl(checkString(_selectedWorkout.value.image_url, isempty = true))
    }


    private fun getExercises() {
        val tag = "exercise_list"
        _apiState.value = ValidationState.Loading(tag,true)
        val data = HashMap<String?, Any?>()
        data["username"] = "darkprnce"
        viewModelScope.launch(Dispatchers.IO) {
            val bean = exerciseListRepository.getExerciseList(data)
            if (bean != null) {
                exerciseList = bean.data!!
                _exercisesSearchFlow.value = bean.data!!.toMutableStateList()
                if (_selectedWorkout.value.exercises_id != null) {
                    _workoutExercisesFlow.value = mutableStateListOf()
                    for (item in _selectedWorkout.value.exercises_id!!) {
                        _workoutExercisesFlow.value.add(exerciseList.find { it!!.id == item!!.id }!!)
                        exerciseList.find { it!!.id == item!!.id }!!.selected = true
                        _exercisesSearchFlow.value.find { it!!.id == item!!.id }!!.selected = true
                    }
                }
                _apiState.value = ValidationState.Loading(tag,false)
            } else {
                exerciseList = arrayListOf()
                _exercisesSearchFlow.value = mutableStateListOf()
                _workoutExercisesFlow.value = mutableStateListOf()
                _apiState.value = ValidationState.Loading(tag,false)
            }
        }
    }

    fun swapExercises(from: Int, to: Int) {
//        _workoutExercisesFlow.value = _workoutExercisesFlow.value.apply {
//            add(to, removeAt(from))
//        }
        _workoutExercisesFlow.update {
            it.add(to, it.removeAt(from))
            it
        }
    }

    fun setSearch(it: String) {
        _searchInputFlow.value = it
        viewModelScope.launch(Dispatchers.Default) {
            if (it.isNotEmpty()) {
                _exercisesSearchFlow.value = exerciseList.filter { item ->
                    item!!.name!!.trim().contains(it, ignoreCase = true)
                            || item.bodypart!!.trim().contains(it, ignoreCase = true)
                            || item.equipment!!.trim().contains(it, ignoreCase = true)
                            || item.target!!.trim().contains(it, ignoreCase = true)
                }.toMutableStateList()
            } else {
                _exercisesSearchFlow.value = exerciseList.toMutableStateList()
            }
        }
    }

    fun addExercise(item: ExerciseListBean.Data) {
        //viewModelScope.launch(Dispatchers.Default) {
            val find_item = _workoutExercisesFlow.value.find { it.id == item.id }
            if (find_item != null) {
                _workoutExercisesFlow.value.remove(find_item)
                _exercisesSearchFlow.update {
                    val item_v = it.find { it!!.id == item.id }!!
                    it[it.indexOf(item_v)] = it[it.indexOf(item_v)]!!.copy(selected = false)
                    it
                }
                exerciseList.find { it!!.id == item.id }!!.selected = false
            } else {
                _workoutExercisesFlow.value.add(item)
                _exercisesSearchFlow.update {
                    val item_v = it.find { it!!.id == item.id }!!
                    it[it.indexOf(item_v)] = it[it.indexOf(item_v)]!!.copy(selected = true)
                    it
                }
                exerciseList.find { it!!.id == item.id }!!.selected = true
            }
        //}
    }

    fun exerciseExist(item: ExerciseListBean.Data): Boolean {
        val find_item = _workoutExercisesFlow.value.find { it.id == item.id }
        return find_item != null
    }

    fun addWorkout(data: HashMap<String?, Any?>) {
        val tag = "add_workout"
        _apiState.value = ValidationState.Loading(tag,true)
        data.put("username", "darkprnce")
        for (item in data.values) {
            if (item is String || item is Int || item is Double || item is Float) {
                item.toString().trim()
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val bean = workoutRepository.addWorkout(data)
            if (bean != null) {
                _apiState.value = ValidationState.Loading(tag,false)
                if (bean.status.equals("success",ignoreCase = true)) {
                    _apiState.value = ValidationState.Success(tag, bean.msg!!)
                } else {
                    _apiState.value = ValidationState.Error(tag, bean.msg!!)
                }
            } else {
                _apiState.value = ValidationState.Loading(tag,false)
                _apiState.value = ValidationState.Error(tag, SOME_ERROR_OCCURED)
            }
        }
    }

    fun removeWorkout(data: HashMap<String?, Any?>) {
        val tag = "remove_workout"
        _apiState.value = ValidationState.Loading(tag,true)
        data.put("username", "darkprnce")
        for (item in data.values) {
            if (item is String || item is Int || item is Double || item is Float) {
                item.toString().trim()
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val bean = workoutRepository.removeWorkout(data)
            if (bean != null) {
                _apiState.value = ValidationState.Loading(tag,false)
                if (bean.status.equals("success",ignoreCase = true)) {
                    _apiState.value = ValidationState.Success(tag, bean.msg!!)
                } else {
                    _apiState.value = ValidationState.Error(tag, bean.msg!!)
                }
            } else {
                _apiState.value = ValidationState.Loading(tag,false)
                _apiState.value = ValidationState.Error(tag, SOME_ERROR_OCCURED)
            }
        }
    }

    fun validateForm() {
        if (_titleInputFlow.value.isEmpty() || _totalTimeInputFlow.value.isEmpty() || _userTimeInputFlow.value.isEmpty() || _imageurlInputFlow.value.isEmpty()) {
            _apiState.value = ValidationState.Error("validation", "Please fill all the fields")
        } else {
            val data = HashMap<String?, Any?>()
            data["id"] = _selectedWorkout.value.id
            data["title"] = _titleInputFlow.value
            data["total_time"] = _totalTimeInputFlow.value
            data["user_time"] = _userTimeInputFlow.value
            data["heartrate_max"] = _heartrateMaxInputFlow.value
            data["heartrate_min"] = _heartrateMinInputFlow.value
            data["stress"] = _stressInputFlow.value
            data["spo2"] = _spo2InputFlow.value
            data["calorie"] = _calorieInputFlow.value
            data["music_url"] = _musicurlInputFlow.value
            data["image_url"] = _imageurlInputFlow.value
            val exerciseList = ArrayList<String>()
            for (item in _workoutExercisesFlow.value) {
                exerciseList.add(item.id.toString())
            }
            data["exercises_id"] = exerciseList
            addWorkout(data)
        }
    }
}

