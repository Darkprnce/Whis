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
import kotlinx.coroutines.Dispatchers
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

    private var exerciseList: List<ExerciseListBean.Data?> = arrayListOf()

    private val _exercisesSearchFlow =
        MutableStateFlow<SnapshotStateList<ExerciseListBean.Data?>>(mutableStateListOf())
    val exercisesSearch = _exercisesSearchFlow.asStateFlow()

    private val _showRemoveExerciseFlow = MutableStateFlow<ExerciseListBean.Data?>(null)
    val showRemoveExercise = _showRemoveExerciseFlow.asStateFlow()

    private val _searchInputFlow = MutableStateFlow("")
    val searchInputFlow = _searchInputFlow.asStateFlow()

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
                            if (exerciseList.isEmpty()) {
                                _showLoadingFlow.value = true
                                _apiState.emit(ValidationState.Loading(tag, true))
                            }
                        }

                        is ApiResp.Error -> {
                            _showLoadingFlow.value = false
                            _apiState.emit(ValidationState.Loading(tag, false))
                            _apiState.emit(ValidationState.Error(tag, resp.message))
                            exerciseList = arrayListOf()
                            _exercisesSearchFlow.value = mutableStateListOf()
                        }

                        is ApiResp.Success -> {
                            val respData = resp.item as ExerciseListBean
                            if (exerciseList.isEmpty()) {
                                _showLoadingFlow.value = false
                                _apiState.emit(ValidationState.Loading(tag, false))
                                _exercisesSearchFlow.value = respData.data!!.toMutableStateList()
                                exerciseList = respData.data!!
                            } else {
                                val addList = arrayListOf<ExerciseListBean.Data>()
                                for (item in respData.data!!) {
                                    val findItem =
                                        exerciseList.find { it!!.id == item.id }
                                    if (findItem == null) {
                                        addList.add(item)
                                    }
                                }

                                val removeList = arrayListOf<ExerciseListBean.Data>()
                                for (item in exerciseList) {
                                    val findItem =
                                        respData.data!!.find { it.id == item!!.id }
                                    if (findItem == null) {
                                        removeList.add(item!!)
                                    }
                                }
                                val changeList = arrayListOf<ExerciseListBean.Data>()
                                for (item in respData.data!!) {
                                    val findItem =
                                        exerciseList.find { it!!.id == item.id }
                                    if (findItem != null) {
                                        if (item != findItem) {
                                            changeList.add(item)
                                        }
                                    }
                                }
                                if(_searchInputFlow.value.isEmpty()){
                                    _exercisesSearchFlow.update {
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
                                }else{
                                    setSearch(_searchInputFlow.value)
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

    fun setSearch(it: String) {
        _searchInputFlow.value = it
        //viewModelScope.launch(Dispatchers.Default) {
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
        //}
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