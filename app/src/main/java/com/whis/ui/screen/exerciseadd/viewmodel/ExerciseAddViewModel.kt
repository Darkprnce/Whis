package com.whis.ui.screen.exerciseadd.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.Network.sealed.ApiResp
import com.whis.Network.sealed.ValidationState
import com.whis.model.ExerciseAddBean
import com.whis.model.ExerciseListBean
import com.whis.model.ExerciseRemoveBean
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

    private val _equipmentInputFlow = MutableStateFlow("")
    val equipmentInputFlow = _equipmentInputFlow.asStateFlow()

    private val _equipmentInputErrorFlow = MutableStateFlow(false)
    val equipmentInputErrorFlow = _equipmentInputErrorFlow.asStateFlow()

    private val _gifurlInputFlow = MutableStateFlow("")
    val gifurlInputFlow = _gifurlInputFlow.asStateFlow()

    private val _gifurlInputErrorFlow = MutableStateFlow(false)
    val gifurlInputErrorFlow = _gifurlInputErrorFlow.asStateFlow()

    private val _repsInputFlow = MutableStateFlow("")
    val repsInputFlow = _repsInputFlow.asStateFlow()

    private val _repsInputErrorFlow = MutableStateFlow(false)
    val repsInputErrorFlow = _repsInputErrorFlow.asStateFlow()

    private val _restAfterCompletionInputFlow = MutableStateFlow("")
    val restAfterCompletionInputFlow = _restAfterCompletionInputFlow.asStateFlow()

    private val _restAfterCompletionInputErrorFlow = MutableStateFlow(false)
    val restAfterCompletionInputErrorFlow = _restAfterCompletionInputErrorFlow.asStateFlow()

    private val _restsInputFlow = MutableStateFlow("")
    val restsInputFlow = _restsInputFlow.asStateFlow()

    private val _restsInputErrorFlow = MutableStateFlow(false)
    val restsInputErrorFlow = _restsInputErrorFlow.asStateFlow()

    private val _setsInputFlow = MutableStateFlow("")
    val setsInputFlow = _setsInputFlow.asStateFlow()

    private val _setsInputErrorFlow = MutableStateFlow(false)
    val setsInputErrorFlow = _setsInputErrorFlow.asStateFlow()

    private val _targetInputFlow = MutableStateFlow("")
    val targetInputFlow = _targetInputFlow.asStateFlow()

    private val _targetInputErrorFlow = MutableStateFlow(false)
    val targetInputErrorFlow = _targetInputErrorFlow.asStateFlow()

    private val _isShowFlow = MutableStateFlow(true)
    val isShowFlow = _isShowFlow.asStateFlow()

    private val _showRemoveExerciseFlow = MutableStateFlow(false)
    val showRemoveExerciseFlow = _showRemoveExerciseFlow.asStateFlow()

    private var _isvalidation = false

    fun setShowLoading(value: Boolean) {
        _showLoadingFlow.value = value
    }

    fun setName(value: String) {
        _nameInputFlow.value = value
        if (_isvalidation) {
            _nameInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setDuration(value: String) {
        _durationInputFlow.value = value
        if (_isvalidation) {
            _durationInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setBodyPart(value: String) {
        _bodypartInputFlow.value = value
        if (_isvalidation) {
            _bodypartInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setEquipment(value: String) {
        _equipmentInputFlow.value = value
        if (_isvalidation) {
            _equipmentInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setGifurl(value: String) {
        _gifurlInputFlow.value = value
        if (_isvalidation) {
            _gifurlInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setReps(value: String) {
        _repsInputFlow.value = value
        if (_isvalidation) {
            _repsInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setRestAfterCompletion(value: String) {
        _restAfterCompletionInputFlow.value = value
        if (_isvalidation) {
            _restAfterCompletionInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setRests(value: String) {
        _restsInputFlow.value = value
        if (_isvalidation) {
            _restsInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setSets(value: String) {
        _setsInputFlow.value = value
        if (_isvalidation) {
            _setsInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setTarget(value: String) {
        _targetInputFlow.value = value
        if (_isvalidation) {
            _targetInputErrorFlow.value = value.isEmpty()
        }
    }

    fun setisShow(value: Boolean) {
        _isShowFlow.value = value
    }
    fun setshowRemoveExercise(value: Boolean) {
        _showRemoveExerciseFlow.value = value
    }

    fun setExercise(exercise: ExerciseListBean.Data?) {
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
        if (checkString(_selectedExercise.value.name, isempty = true).isNotEmpty()) {
            setName(checkString(_selectedExercise.value.name, isempty = true))
        }

        if (checkString(_selectedExercise.value.duration, isempty = true).isNotEmpty()) {
            setDuration(checkString(_selectedExercise.value.duration, isempty = true))
        }

        if (checkString(_selectedExercise.value.bodypart, isempty = true).isNotEmpty()) {
            setBodyPart(checkString(_selectedExercise.value.bodypart, isempty = true))
        }

        if (checkString(_selectedExercise.value.equipment, isempty = true).isNotEmpty()) {
            setEquipment(checkString(_selectedExercise.value.equipment, isempty = true))
        }

        if (checkString(_selectedExercise.value.gifurl, isempty = true).isNotEmpty()) {
            setGifurl(checkString(_selectedExercise.value.gifurl, isempty = true))
        }

        if (checkString(_selectedExercise.value.reps, isempty = true).isNotEmpty()) {
            setReps(checkString(_selectedExercise.value.reps, isempty = true))
        }

        if (checkString(
                _selectedExercise.value.rest_after_completion,
                isempty = true
            ).isNotEmpty()
        ) {
            setRestAfterCompletion(
                checkString(
                    _selectedExercise.value.rest_after_completion,
                    isempty = true
                )
            )
        }

        if (checkString(_selectedExercise.value.rests, isempty = true).isNotEmpty()) {
            setRests(checkString(_selectedExercise.value.rests, isempty = true))
        }

        if (checkString(_selectedExercise.value.sets, isempty = true).isNotEmpty()) {
            setSets(checkString(_selectedExercise.value.sets, isempty = true))
        }

        if (checkString(_selectedExercise.value.target, isempty = true).isNotEmpty()) {
            setTarget(checkString(_selectedExercise.value.target, isempty = true))
        }

        if (checkString(_selectedExercise.value.isshow, isempty = true).isNotEmpty()) {
            if(_selectedExercise.value.isshow.equals("true")){
                setisShow(true)
            }else{
                setisShow(false)
            }
        }
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
            _equipmentInputFlow.value.isEmpty() ||
            _gifurlInputFlow.value.isEmpty() ||
            _repsInputFlow.value.isEmpty() ||
            _restAfterCompletionInputFlow.value.isEmpty() ||
            _restsInputFlow.value.isEmpty() ||
            _setsInputFlow.value.isEmpty() ||
            _targetInputFlow.value.isEmpty()
        ) {
            _isvalidation = true
            _nameInputErrorFlow.value = _nameInputFlow.value.isEmpty()
            _durationInputErrorFlow.value = _durationInputFlow.value.isEmpty()
            _bodypartInputErrorFlow.value = _bodypartInputFlow.value.isEmpty()
            _equipmentInputErrorFlow.value = _equipmentInputFlow.value.isEmpty()
            _gifurlInputErrorFlow.value = _gifurlInputFlow.value.isEmpty()
            _repsInputErrorFlow.value = _repsInputFlow.value.isEmpty()
            _restAfterCompletionInputErrorFlow.value = _restAfterCompletionInputFlow.value.isEmpty()
            _restsInputErrorFlow.value = _restsInputFlow.value.isEmpty()
            _setsInputErrorFlow.value = _setsInputFlow.value.isEmpty()
            _targetInputErrorFlow.value = _targetInputFlow.value.isEmpty()

            viewModelScope.launch(Dispatchers.Default) {
                _apiState.emit(ValidationState.Error("validation", "Please fill all the fields"))
            }
        } else {
            _isvalidation = false
            val data = HashMap<String?, Any?>()
            data["id"] = _selectedExercise.value.id
            data["name"] = _nameInputFlow.value
            data["duration"] = _durationInputFlow.value
            data["bodypart"] = _bodypartInputFlow.value
            data["target"] = _targetInputFlow.value
            data["equipment"] = _equipmentInputFlow.value
            data["gifurl"] = _gifurlInputFlow.value
            data["sets"] = _setsInputFlow.value
            data["reps"] = _repsInputFlow.value
            data["rests"] = _restsInputFlow.value
            data["rest_after_completion"] = _restAfterCompletionInputFlow.value
            data["isshow"] = "${_isShowFlow.value}"
            addExercise(data)
        }
    }
}

