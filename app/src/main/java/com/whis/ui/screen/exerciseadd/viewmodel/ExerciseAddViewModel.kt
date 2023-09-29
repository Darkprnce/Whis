package com.whis.ui.screen.exerciseadd.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.Network.sealed.ApiResp
import com.whis.Network.sealed.ValidationState
import com.whis.model.ExerciseAddBean
import com.whis.model.ExerciseListBean
import com.whis.model.ExerciseRemoveBean
import com.whis.model.WorkoutAddBean
import com.whis.model.WorkoutRemoveBean
import com.whis.repository.ExerciseListRepository
import com.whis.utils.checkString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseAddViewModel @Inject constructor(
    private val exerciseListRepository: ExerciseListRepository
) : ViewModel() {

    private val _showLoadingFlow = MutableStateFlow(false)
    val showLoading = _showLoadingFlow.asStateFlow()

    private var exerciseList: List<ExerciseListBean.Data?> = arrayListOf()

    private val _exercisesSearchFlow =
        MutableStateFlow<SnapshotStateList<ExerciseListBean.Data?>>(mutableStateListOf())
    val exercisesSearch = _exercisesSearchFlow.asStateFlow()

    private val _apiState = MutableSharedFlow<ValidationState>()
    val apiState = _apiState.asSharedFlow()

    private val _selectedExercise = MutableStateFlow(ExerciseListBean.Data())
    val selectedExercise = _selectedExercise.asStateFlow()

    private val _nameInputFlow = MutableStateFlow("")
    val nameInputFlow = _nameInputFlow.asStateFlow()

    private val _nameInputErrorFlow = MutableStateFlow(false)
    val nameInputErrorFlow = _nameInputErrorFlow.asStateFlow()

    private val _durationInputFlow = MutableStateFlow("")
    val durationInputFlow = _durationInputFlow.asStateFlow()

    private val _durationInputErrorFlow = MutableStateFlow(false)
    val durationInputErrorFlow = _durationInputErrorFlow.asStateFlow()

    private val _bodypartInputFlow = MutableStateFlow("")
    val bodypartInputFlow = _bodypartInputFlow.asStateFlow()

    private val _bodypartInputErrorFlow = MutableStateFlow(false)
    val bodypartInputErrorFlow = _bodypartInputErrorFlow.asStateFlow()

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
        _nameInputFlow.value = value
        if (_isvalidation) {
            _nameInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setTotalTime(value: String) {
        _durationInputFlow.value = value
        if (_isvalidation) {
            _durationInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setUserTime(value: String) {
        _bodypartInputFlow.value = value
        if (_isvalidation) {
            _bodypartInputErrorFlow.value = value.isEmpty()
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

    fun setshowRemoveWorkout(value: Boolean) {
        _showRemoveWorkoutFlow.value = value
    }

    fun setshowAddExercise(value: Boolean) {
        _showAddExerciseFlow.value = value
    }

    fun setShowExerciseMove(value: Boolean) {
        _showExerciseMoveFlow.value = value
    }

    fun setWorkout(exercise: ExerciseListBean.Data?) {
        viewModelScope.launch(Dispatchers.IO) {
            _apiState.emit(ValidationState.Ideal)
            if (exercise != null) {
                _selectedExercise.value = exercise
                setData()
            } else {
                _selectedExercise.value = ExerciseListBean.Data()
            }
        }
    }

    private fun setData() {
      /*  if (checkString(_selectedExercise.value.title, isempty = true).isNotEmpty()) {
            setTitle(checkString(_selectedExercise.value.title, isempty = true))
        }

        if (checkString(_selectedExercise.value.total_time, isempty = true).isNotEmpty()) {
            setTotalTime(checkString(_selectedExercise.value.total_time, isempty = true))
        }

        if (checkString(_selectedExercise.value.user_time, isempty = true).isNotEmpty()) {
            setUserTime(checkString(_selectedExercise.value.user_time, isempty = true))
        }

        if (checkString(_selectedExercise.value.heartrate_max, isempty = true).isNotEmpty()) {
            setHeartrateMax(checkString(_selectedExercise.value.heartrate_max, isempty = true))
        }

        if (checkString(_selectedExercise.value.heartrate_min, isempty = true).isNotEmpty()) {
            setHeartrateMin(checkString(_selectedExercise.value.heartrate_min, isempty = true))
        }

        if (checkString(_selectedExercise.value.stress, isempty = true).isNotEmpty()) {
            setStress(checkString(_selectedExercise.value.stress, isempty = true))
        }

        if (checkString(_selectedExercise.value.spo2, isempty = true).isNotEmpty()) {
            setSpo2(checkString(_selectedExercise.value.spo2, isempty = true))
        }

        if (checkString(_selectedExercise.value.calorie, isempty = true).isNotEmpty()) {
            setCalorie(checkString(_selectedExercise.value.calorie, isempty = true))
        }

        if (checkString(_selectedExercise.value.music_url, isempty = true).isNotEmpty()) {
            setMusicUrl(checkString(_selectedExercise.value.music_url, isempty = true))
        }

        if (checkString(_selectedExercise.value.image_url, isempty = true).isNotEmpty()) {
            setImageUrl(checkString(_selectedExercise.value.image_url, isempty = true))
        }*/
    }


    fun addExercise(data: HashMap<String?, Any?>) {
        viewModelScope.launch {
            val tag = "add_exercise"
            exerciseListRepository.addExercise(data).collect { resp ->
                when (resp) {
                    is ApiResp.Loading -> {
                        _apiState.emit(ValidationState.Loading(tag, true))
                    }

                    is ApiResp.Error -> {
                        _apiState.emit(ValidationState.Loading(tag, false))
                        _apiState.emit(ValidationState.Error(tag, resp.message))
                    }

                    is ApiResp.Success -> {
                        val respData = resp.item as ExerciseAddBean
                        _apiState.emit(ValidationState.Loading(tag, false))
                        _apiState.emit(ValidationState.Success(tag, respData.msg!!))
                    }
                }
            }
        }
    }

    fun removeExercise(data: HashMap<String?, Any?>) {
        viewModelScope.launch {
            val tag = "remove_exercise"
            exerciseListRepository.removeExercise(data).collect { resp ->
                when (resp) {
                    is ApiResp.Loading -> {
                        _apiState.emit(ValidationState.Loading(tag, true))
                    }

                    is ApiResp.Error -> {
                        _apiState.emit(ValidationState.Loading(tag, false))
                        _apiState.emit(ValidationState.Error(tag, resp.message))
                    }

                    is ApiResp.Success -> {
                        val respData = resp.item as ExerciseRemoveBean
                        _apiState.emit(ValidationState.Loading(tag, false))
                        _apiState.emit(ValidationState.Success(tag, respData.msg!!))
                    }
                }
            }
        }
    }

    fun validateForm() {
        if (_nameInputFlow.value.isEmpty() ||
            _durationInputFlow.value.isEmpty() ||
            _bodypartInputFlow.value.isEmpty() ||
            _imageurlInputFlow.value.isEmpty()
        ) {
            _isvalidation = true
            _nameInputErrorFlow.value = _nameInputFlow.value.isEmpty()
            _durationInputErrorFlow.value = _durationInputFlow.value.isEmpty()
            _bodypartInputErrorFlow.value = _bodypartInputFlow.value.isEmpty()
            _imageurlInputErrorFlow.value = _imageurlInputFlow.value.isEmpty()

            viewModelScope.launch(Dispatchers.Default) {
                _apiState.emit(ValidationState.Error("validation", "Please fill all the fields"))
            }
        } else {
            _isvalidation = false
            val data = HashMap<String?, Any?>()
            data["id"] = _selectedExercise.value.id
            data["title"] = _nameInputFlow.value
            data["total_time"] = _durationInputFlow.value
            data["user_time"] = _bodypartInputFlow.value
            data["heartrate_max"] = _heartrateMaxInputFlow.value
            data["heartrate_min"] = _heartrateMinInputFlow.value
            data["stress"] = _stressInputFlow.value
            data["spo2"] = _spo2InputFlow.value
            data["calorie"] = _calorieInputFlow.value
            data["music_url"] = _musicurlInputFlow.value
            data["image_url"] = _imageurlInputFlow.value
            addExercise(data)
        }
    }
}

