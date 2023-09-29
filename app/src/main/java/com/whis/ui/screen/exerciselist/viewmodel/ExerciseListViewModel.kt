package com.whis.ui.screen.exerciselist.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.Network.sealed.ApiResp
import com.whis.Network.sealed.ValidationState
import com.whis.model.ExerciseListBean
import com.whis.model.ExerciseRemoveBean
import com.whis.repository.ExerciseListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val exerciseListRepository: ExerciseListRepository
) : ViewModel() {

    private val _showLoadingFlow = MutableStateFlow(true)
    val showLoading = _showLoadingFlow.asStateFlow()

    private val _apiState = MutableSharedFlow<ValidationState>()
    val apiState = _apiState.asSharedFlow()

    private val _exerciseListFlow =
        MutableStateFlow<SnapshotStateList<ExerciseListBean.Data>>(mutableStateListOf())
    val exerciseList = _exerciseListFlow.asStateFlow()

    private val _showRemoveExerciseFlow = MutableStateFlow<ExerciseListBean.Data?>(null)
    val showRemoveExercise = _showRemoveExerciseFlow.asStateFlow()

    init {
        getExercise()
    }

    fun setshowRemoveWorkout(value: ExerciseListBean.Data?) {
        _showRemoveExerciseFlow.value = value
    }

    fun getExercise(id: String? = null) {
        viewModelScope.launch {
            try {
                val tag = "exercise_list"
                val data = HashMap<String?, Any?>()
                data["id"] = id
                exerciseListRepository.getExerciseList(data).collect { resp ->
                    when (resp) {
                        is ApiResp.Loading -> {
                            if (_exerciseListFlow.value.isEmpty()) {
                                _showLoadingFlow.value = true
                                _apiState.emit(ValidationState.Loading(tag, true))
                            }
                        }

                        is ApiResp.Error -> {
                            _showLoadingFlow.value = false
                            _apiState.emit(ValidationState.Loading(tag, false))
                            _apiState.emit(ValidationState.Error(tag, resp.message))
                            _exerciseListFlow.value = mutableStateListOf()
                        }

                        is ApiResp.Success -> {
                            val respData = resp.item as ExerciseListBean
                            if (_exerciseListFlow.value.isEmpty()) {
                                _showLoadingFlow.value = false
                                _apiState.emit(ValidationState.Loading(tag, false))
                                _exerciseListFlow.value = respData.data!!.toMutableStateList()
                            } else {
                                val addList = arrayListOf<ExerciseListBean.Data>()
                                for (item in respData.data!!) {
                                    val findItem =
                                        _exerciseListFlow.value.find { it.id == item.id }
                                    if (findItem == null) {
                                        addList.add(item)
                                    }
                                }

                                val removeList = arrayListOf<ExerciseListBean.Data>()
                                for (item in _exerciseListFlow.value) {
                                    val findItem =
                                        respData.data!!.find { it.id == item.id }
                                    if (findItem == null) {
                                        removeList.add(item)
                                    }
                                }
                                val changeList = arrayListOf<ExerciseListBean.Data>()
                                for (item in respData.data!!) {
                                    val findItem =
                                        _exerciseListFlow.value.find { it.id == item.id }
                                    if (findItem != null) {
                                        if (item != findItem) {
                                            changeList.add(item)
                                        }
                                    }
                                }

                                _exerciseListFlow.update {
                                    it.removeAll(removeList)
                                    it.addAll(addList)
                                    if (changeList.size > 0) {
                                        for (item in changeList) {
                                            val findItem = it.find { it.id == item.id }
                                            it.set(it.indexOf(findItem), item)
                                        }
                                    }
                                    it
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeWorkout(data: HashMap<String?, Any?>) {
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
                        if (respData.status.equals("success")) {
                            _apiState.emit(ValidationState.Success(tag, respData.msg!!))
                            getExercise()
                        } else {
                            _apiState.emit(ValidationState.Error(tag, respData.msg!!))
                        }
                    }
                }
            }
        }
    }
}