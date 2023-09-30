package com.whis.ui.screen.workoutlist.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.Network.sealed.ApiResp
import com.whis.Network.sealed.ValidationState
import com.whis.model.WorkoutListBean
import com.whis.model.WorkoutRemoveBean
import com.whis.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _showLoadingFlow = MutableStateFlow(true)
    val showLoading = _showLoadingFlow.asStateFlow()

    private val _apiState = MutableSharedFlow<ValidationState>()
    val apiState = _apiState.asSharedFlow()

    private val _workoutListFlow =
        MutableStateFlow<SnapshotStateList<WorkoutListBean.Data>>(mutableStateListOf())
    val workoutList = _workoutListFlow.asStateFlow()

    private val _showRemoveWorkoutFlow = MutableStateFlow<WorkoutListBean.Data?>(null)
    val showRemoveWorkout = _showRemoveWorkoutFlow.asStateFlow()

    init {
        getWorkouts()
    }

    fun setshowRemoveWorkout(value: WorkoutListBean.Data?) {
        _showRemoveWorkoutFlow.value = value
    }

    fun getWorkouts(id: String? = null) {
        viewModelScope.launch {
            try {
                val tag = "workout_list"
                val data = HashMap<String?, Any?>()
                data["id"] = id
                workoutRepository.getWorkoutList(data).collect { resp ->
                    when (resp) {
                        is ApiResp.Loading -> {
                            if (_workoutListFlow.value.isEmpty()) {
                                _showLoadingFlow.value = true
                                _apiState.emit(ValidationState.Loading(tag, true))
                            }
                        }

                        is ApiResp.Error -> {
                            _showLoadingFlow.value = false
                            _apiState.emit(ValidationState.Loading(tag, false))
                            _apiState.emit(ValidationState.Error(tag, resp.message))
                            _workoutListFlow.value = mutableStateListOf()
                        }

                        is ApiResp.Success -> {
                            val respData = resp.item as WorkoutListBean
                            if (_workoutListFlow.value.isEmpty()) {
                                _showLoadingFlow.value = false
                                _apiState.emit(ValidationState.Loading(tag, false))
                                _workoutListFlow.value = respData.data!!.toMutableStateList()
                            } else {
                                val addList = arrayListOf<WorkoutListBean.Data>()
                                for (item in respData.data!!) {
                                    val findItem =
                                        _workoutListFlow.value.find { it.id == item.id }
                                    if (findItem == null) {
                                        addList.add(item)
                                    }
                                }

                                val removeList = arrayListOf<WorkoutListBean.Data>()
                                for (item in _workoutListFlow.value) {
                                    val findItem =
                                        respData.data!!.find { it.id == item.id }
                                    if (findItem == null) {
                                        removeList.add(item)
                                    }
                                }
                                val changeList = arrayListOf<WorkoutListBean.Data>()
                                for (item in respData.data!!) {
                                    val findItem =
                                        _workoutListFlow.value.find { it.id == item.id }
                                    if (findItem != null) {
                                        if (item != findItem) {
                                            changeList.add(item)
                                        }
                                    }
                                }

                                _workoutListFlow.update {
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
                        getWorkouts()
                    }
                }
            }
        }
    }
}