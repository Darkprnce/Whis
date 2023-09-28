package com.whis.ui.screen.workoutlist.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.whis.Network.sealed.ApiResp
import com.whis.Network.sealed.ValidationState
import com.whis.model.WorkoutListBean
import com.whis.repository.WorkoutRepository
import com.whis.utils.SOME_ERROR_OCCURED
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
class WorkoutListViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _showLoadingFlow = MutableStateFlow(false)
    val showLoading = _showLoadingFlow.asStateFlow()

    private val _apiState = MutableSharedFlow<ValidationState>()
    val apiState = _apiState.asSharedFlow()

    private val _workoutListFlow =
        MutableStateFlow<ArrayList<WorkoutListBean.Data>>(arrayListOf())
    val workoutList = _workoutListFlow.asStateFlow()

    private val _showRemoveWorkoutFlow = MutableStateFlow(false)
    val showRemoveWorkout = _showRemoveWorkoutFlow.asStateFlow()

    init {
        getWorkouts()
    }

    fun refreshWorkout() {
        viewModelScope.launch(Dispatchers.IO) {
            workoutRepository.getWorkoutList(HashMap())
        }
    }

    fun setShowLoading(value: Boolean) {
        _showLoadingFlow.value = value
    }

    fun setshowRemoveWorkout(value: Boolean) {
        _showRemoveWorkoutFlow.value = value
    }


    fun getWorkouts(id: String? = null) {
        Timber.e("APi Started")
        viewModelScope.launch(Dispatchers.IO) {
            val tag = "workout_list"
            val data = HashMap<String?, Any?>()
            data["id"] = id
            workoutRepository.getWorkoutList(data).collect { resp ->
                when (resp) {
                    is ApiResp.Loading -> {
                        Timber.e("Loading=>${Gson().toJson(_workoutListFlow)}")
                        if (_workoutListFlow.value.isEmpty()) {
                            _apiState.emit(ValidationState.Loading(tag, true))
                        }
                    }

                    is ApiResp.Error -> {
                        Timber.e("Error=>${resp.message}")
                        _apiState.emit(ValidationState.Error(tag,resp.message))
                        _workoutListFlow.value = arrayListOf()
                    }

                    is ApiResp.None -> {
                        Timber.e("None")
                    }

                    is ApiResp.Success -> {
                        Timber.e("Success")
                        if (resp.item!!.status.equals("success")) {
                            if (_workoutListFlow.value.isEmpty()) {
                                _apiState.emit(ValidationState.Loading(tag, false))
                                _workoutListFlow.value = ArrayList(resp.item.data!!)
                            } else {
                                val addList = arrayListOf<WorkoutListBean.Data>()
                                for (item in resp.item.data!!) {
                                    val findItem =
                                        _workoutListFlow.value.find { it.id == item.id }
                                    if (findItem == null) {
                                        addList.add(item)
                                    }
                                }

                                val removeList = arrayListOf<WorkoutListBean.Data>()
                                for (item in _workoutListFlow.value) {
                                    val findItem =
                                        resp.item.data!!.find { it.id == item.id }
                                    if (findItem == null) {
                                        removeList.add(item)
                                    }
                                }
                                val changeList = arrayListOf<WorkoutListBean.Data>()
                                for (item in resp.item.data!!) {
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
                                            val findItem = it.find { it!!.id == item.id }
                                            it.set(it.indexOf(findItem), item)
                                        }
                                    }
                                    it
                                }
                            }
                        } else {
                            _apiState.emit(ValidationState.Loading(tag, false))
                            _apiState.emit(ValidationState.Error(tag,resp.item.msg!!))
                            _workoutListFlow.value = arrayListOf()
                        }
                    }
                }

            }
            /*  val bean = workoutRepository.getWorkoutList(data)
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
              }*/
        }
    }

    fun removeWorkout(data: HashMap<String?, Any?>) {
        val tag = "remove_workout"
        // _apiState.value = ValidationState.Loading(tag,true)
        viewModelScope.launch(Dispatchers.IO) {
            val bean = workoutRepository.removeWorkout(data)
            if (bean != null) {
                // _apiState.value = ValidationState.Loading(tag,false)
                _apiState.emit(ValidationState.Success(tag, bean.msg!!))
                getWorkouts()
            } else {
                //  _apiState.value = ValidationState.Loading(tag,false)
                _apiState.emit(ValidationState.Error(tag, SOME_ERROR_OCCURED))
            }
        }
    }
}