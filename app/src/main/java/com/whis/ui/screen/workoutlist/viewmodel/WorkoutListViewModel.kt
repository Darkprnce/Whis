package com.whis.ui.screen.workoutlist.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.Network.sealed.ValidationState
import com.whis.model.WorkoutListBean
import com.whis.repository.WorkoutRepository
import com.whis.utils.SOME_ERROR_OCCURED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) :
    ViewModel() {

    private val _showLoadingFlow = MutableStateFlow(false)
    val showLoading = _showLoadingFlow.asStateFlow()

    private val _apiState = MutableStateFlow<ValidationState>(ValidationState.Ideal)
    val apiState = _apiState.asStateFlow()

    private val _workoutListFlow = MutableStateFlow<SnapshotStateList<WorkoutListBean.Data?>?>(
        mutableStateListOf()
    )
    val workoutList = _workoutListFlow.asStateFlow()

    private val _showRemoveWorkoutFlow = MutableStateFlow(false)
    val showRemoveWorkout = _showRemoveWorkoutFlow.asStateFlow()

    init {
        getWorkouts()
    }

    fun setShowLoading(value: Boolean) {
        _showLoadingFlow.value = value
    }

    fun setshowRemoveWorkout(value: Boolean) {
        _showRemoveWorkoutFlow.value = value
    }

    fun getWorkouts(id: String? = null) {
        val tag = "workout_list"
        if (_workoutListFlow.value!!.isEmpty()) {
            _apiState.value = ValidationState.Loading(tag, true)
        }

        val data = HashMap<String?, Any?>()
        data.put("id", id)
        viewModelScope.launch(Dispatchers.IO) {
            val bean = workoutRepository.getWorkoutList(data)
            if (bean != null) {

                if (_workoutListFlow.value!!.isEmpty()) {
                    _apiState.value = ValidationState.Loading(tag, false)
                    _workoutListFlow.value = bean.data!!.toMutableStateList()
                } else {
                    val addList = arrayListOf<WorkoutListBean.Data>()
                    for (item in bean.data!!) {
                        val findItem = _workoutListFlow.value!!.find { it!!.id == item!!.id }
                        if (findItem == null) {
                            addList.add(item!!)
                        }
                    }

                    val removeList = arrayListOf<WorkoutListBean.Data>()
                    for (item in _workoutListFlow.value!!) {
                        val findItem = bean.data!!.find { it!!.id == item!!.id }
                        if (findItem == null) {
                            removeList.add(item!!)
                        }
                    }
                    val changeList = arrayListOf<WorkoutListBean.Data>()
                    for (item in bean.data!!) {
                        val findItem = _workoutListFlow.value!!.find { it!!.id == item!!.id }
                        if (findItem != null) {
                            if (item!! != findItem) {
                                changeList.add(item)
                            }
                        }
                    }

                    _workoutListFlow.update {
                        it!!.removeAll(removeList)
                        it.addAll(addList)
                        if(changeList.size>0){
                            for (item in changeList){
                                val findItem = it.find { it!!.id == item.id }
                                it.set(it.indexOf(findItem),item)
                            }
                        }
                        it
                    }
                }
            } else {
                _apiState.value = ValidationState.Loading(tag, false)
                _workoutListFlow.value = mutableStateListOf()
            }
        }
    }

    fun removeWorkout(data: HashMap<String?, Any?>) {
        val tag = "remove_workout"
        // _apiState.value = ValidationState.Loading(tag,true)
        viewModelScope.launch(Dispatchers.IO) {
            val bean = workoutRepository.removeWorkout(data)
            if (bean != null) {
                // _apiState.value = ValidationState.Loading(tag,false)
                _apiState.value = ValidationState.Success(tag, bean.msg!!)
                getWorkouts()
            } else {
                //  _apiState.value = ValidationState.Loading(tag,false)
                _apiState.value = ValidationState.Error(tag, SOME_ERROR_OCCURED)
            }
        }
    }
}