package com.whis.ui.screen.workoutadd.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.Network.sealed.ApiResp
import com.whis.Network.sealed.ValidationState
import com.whis.model.ExerciseListBean
import com.whis.model.WorkoutAddBean
import com.whis.model.WorkoutListBean
import com.whis.model.WorkoutRemoveBean
import com.whis.repository.ExerciseListRepository
import com.whis.repository.WorkoutRepository
import com.whis.utils.SOME_ERROR_OCCURED
import com.whis.utils.checkString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkoutAddViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseListRepository: ExerciseListRepository
) : ViewModel() {

    private val _showLoadingFlow = MutableStateFlow(false)
    val showLoading = _showLoadingFlow.asStateFlow()

    private var exerciseList: List<ExerciseListBean.Data?> = arrayListOf()

    private val _exercisesSearchFlow =
        MutableStateFlow<SnapshotStateList<ExerciseListBean.Data?>>(mutableStateListOf())
    val exercisesSearch = _exercisesSearchFlow.asStateFlow()

    private val _workoutExercisesFlow = MutableStateFlow<SnapshotStateList<ExerciseListBean.Data>>(
        mutableStateListOf()
    )
    val workoutExercises = _workoutExercisesFlow.asStateFlow()

    private val _apiState = MutableSharedFlow<ValidationState>()
    val apiState = _apiState.asSharedFlow()

    private val _selectedWorkout = MutableStateFlow(WorkoutListBean.Data())
    val selectedWorkout = _selectedWorkout.asStateFlow()

    private val _titleInputFlow = MutableStateFlow("")
    val titleInputFlow = _titleInputFlow.asStateFlow()

    private val _titleInputErrorFlow = MutableStateFlow(false)
    val titleInputErrorFlow = _titleInputErrorFlow.asStateFlow()

    private val _totalTimeInputFlow = MutableStateFlow("")
    val totalTimeInputFlow = _totalTimeInputFlow.asStateFlow()

    private val _totalTimeInputErrorFlow = MutableStateFlow(false)
    val totalTimeInputErrorFlow = _totalTimeInputErrorFlow.asStateFlow()

    private val _userTimeInputFlow = MutableStateFlow("")
    val userTimeInputFlow = _userTimeInputFlow.asStateFlow()

    private val _userTimeInputErrorFlow = MutableStateFlow(false)
    val userTimeInputErrorFlow = _userTimeInputErrorFlow.asStateFlow()

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

    private val _imageurlInputErrorFlow = MutableStateFlow(false)
    val imageurlInputErrorFlow = _imageurlInputErrorFlow.asStateFlow()

    private val _isShowFlow = MutableStateFlow(true)
    val isShowFlow = _isShowFlow.asStateFlow()

    private val _searchInputFlow = MutableStateFlow("")
    val searchInputFlow = _searchInputFlow.asStateFlow()

    private val _showRemoveWorkoutFlow = MutableStateFlow(false)
    val showRemoveWorkout = _showRemoveWorkoutFlow.asStateFlow()

    private val _showAddExerciseFlow = MutableStateFlow(false)
    val showAddExercise = _showAddExerciseFlow.asStateFlow()

    private val _showExerciseMoveFlow = MutableStateFlow(false)
    val showExerciseMove = _showExerciseMoveFlow.asStateFlow()


    private var _isvalidation = false

    fun setShowLoading(value: Boolean) {
        _showLoadingFlow.value = value
    }

    fun setTitle(value: String) {
        _titleInputFlow.value = value
        if (_isvalidation) {
            _titleInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setTotalTime(value: String) {
        _totalTimeInputFlow.value = value
        if (_isvalidation) {
            _totalTimeInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setUserTime(value: String) {
        _userTimeInputFlow.value = value
        if (_isvalidation) {
            _userTimeInputErrorFlow.value = value.isEmpty()
        }
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
        if (_isvalidation) {
            _imageurlInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setisShow(value: Boolean) {
        _isShowFlow.value = value
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
        viewModelScope.launch(Dispatchers.IO) {
            _apiState.emit(ValidationState.Ideal)
            if (workout != null) {
                _selectedWorkout.value = workout
                setData()
            } else {
                _selectedWorkout.value = WorkoutListBean.Data()
            }
            getExercises()
        }
    }

    private fun setData() {
        if (checkString(_selectedWorkout.value.title, isempty = true).isNotEmpty()) {
            setTitle(checkString(_selectedWorkout.value.title, isempty = true))
        }

        if (checkString(_selectedWorkout.value.total_time, isempty = true).isNotEmpty()) {
            setTotalTime(checkString(_selectedWorkout.value.total_time, isempty = true))
        }

        if (checkString(_selectedWorkout.value.user_time, isempty = true).isNotEmpty()) {
            setUserTime(checkString(_selectedWorkout.value.user_time, isempty = true))
        }

        if (checkString(_selectedWorkout.value.heartrate_max, isempty = true).isNotEmpty()) {
            setHeartrateMax(checkString(_selectedWorkout.value.heartrate_max, isempty = true))
        }

        if (checkString(_selectedWorkout.value.heartrate_min, isempty = true).isNotEmpty()) {
            setHeartrateMin(checkString(_selectedWorkout.value.heartrate_min, isempty = true))
        }

        if (checkString(_selectedWorkout.value.stress, isempty = true).isNotEmpty()) {
            setStress(checkString(_selectedWorkout.value.stress, isempty = true))
        }

        if (checkString(_selectedWorkout.value.spo2, isempty = true).isNotEmpty()) {
            setSpo2(checkString(_selectedWorkout.value.spo2, isempty = true))
        }

        if (checkString(_selectedWorkout.value.calorie, isempty = true).isNotEmpty()) {
            setCalorie(checkString(_selectedWorkout.value.calorie, isempty = true))
        }

        if (checkString(_selectedWorkout.value.music_url, isempty = true).isNotEmpty()) {
            setMusicUrl(checkString(_selectedWorkout.value.music_url, isempty = true))
        }

        if (checkString(_selectedWorkout.value.image_url, isempty = true).isNotEmpty()) {
            setImageUrl(checkString(_selectedWorkout.value.image_url, isempty = true))
        }

        if (checkString(_selectedWorkout.value.isshow, isempty = true).isNotEmpty()) {
            if(_selectedWorkout.value.isshow.equals("true")){
                setisShow(true)
            }else{
                setisShow(false)
            }
        }
    }


    private fun getExercises() {
        val tag = "exercise_list"
        viewModelScope.launch {
            val data = HashMap<String?, Any?>()
            exerciseListRepository.getExerciseList(data).collect { resp ->
                when (resp) {
                    is ApiResp.Loading -> {
                        _apiState.emit(ValidationState.Loading(tag, true))
                    }

                    is ApiResp.Error -> {
                        exerciseList = arrayListOf()
                        _exercisesSearchFlow.value = mutableStateListOf()
                        _workoutExercisesFlow.value = mutableStateListOf()
                        _apiState.emit(ValidationState.Loading(tag, false))
                    }

                    is ApiResp.Success -> {
                        val respData = resp.item as ExerciseListBean
                        exerciseList = respData.data!!
                        _exercisesSearchFlow.value = respData.data!!.toMutableStateList()
                        _workoutExercisesFlow.value = mutableStateListOf()
                        if (_selectedWorkout.value.exercises_id != null) {
                            for (item in _selectedWorkout.value.exercises_id!!) {
                                val findItem = exerciseList.find { it!!.id == item!!.id }
                                if(findItem !=null){
                                    exerciseList.find { it!!.id == item!!.id }!!.selected = true
                                    _exercisesSearchFlow.value.find { it!!.id == item!!.id }!!.selected =
                                        true
                                    _workoutExercisesFlow.value.add(exerciseList.find { it!!.id == item!!.id }!!)
                                }
                            }
                        }
                        _apiState.emit(ValidationState.Loading(tag, false))
                    }
                }
            }

        }
    }

    fun swapExercises(from: Int, to: Int) {
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
    }

    fun addWorkout(data: HashMap<String?, Any?>) {
        viewModelScope.launch {
            val tag = "add_workout"
            workoutRepository.addWorkout(data).collect { resp ->
                when (resp) {
                    is ApiResp.Loading -> {
                        _apiState.emit(ValidationState.Loading(tag, true))
                    }

                    is ApiResp.Error -> {
                        _apiState.emit(ValidationState.Loading(tag, false))
                        _apiState.emit(ValidationState.Error(tag, resp.message))
                    }

                    is ApiResp.Success -> {
                        val respData = resp.item as WorkoutAddBean
                        _apiState.emit(ValidationState.Loading(tag, false))
                        _apiState.emit(ValidationState.Success(tag, respData.msg!!))
                    }
                }
            }
        }
    }

    fun removeWorkout(data: HashMap<String?, Any?>) {
        viewModelScope.launch {
            val tag = "remove_workout"
            workoutRepository.removeWorkout(data).collect { resp ->
                when (resp) {
                    is ApiResp.Loading -> {
                        _apiState.emit(ValidationState.Loading(tag, true))
                    }

                    is ApiResp.Error -> {
                        _apiState.emit(ValidationState.Loading(tag, false))
                        _apiState.emit(ValidationState.Error(tag, resp.message))
                    }

                    is ApiResp.Success -> {
                        val respData = resp.item as WorkoutRemoveBean
                        _apiState.emit(ValidationState.Loading(tag, false))
                        _apiState.emit(ValidationState.Success(tag, respData.msg!!))
                    }
                }
            }
        }
    }

    fun validateForm() {
        if (_titleInputFlow.value.isEmpty() ||
            _totalTimeInputFlow.value.isEmpty() ||
            _userTimeInputFlow.value.isEmpty() ||
            _imageurlInputFlow.value.isEmpty()
        ) {
            _isvalidation = true
            _titleInputErrorFlow.value = _titleInputFlow.value.isEmpty()
            _totalTimeInputErrorFlow.value = _totalTimeInputFlow.value.isEmpty()
            _userTimeInputErrorFlow.value = _userTimeInputFlow.value.isEmpty()
            _imageurlInputErrorFlow.value = _imageurlInputFlow.value.isEmpty()

            viewModelScope.launch(Dispatchers.Default) {
                _apiState.emit(ValidationState.Error("validation", "Please fill all the fields"))
            }
        } else {
            _isvalidation = false
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
            data["isshow"] = "${_isShowFlow.value}"
            addWorkout(data)
        }
    }
}

