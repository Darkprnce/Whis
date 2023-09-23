package com.whis.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.whis.model.WorkoutListBean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {

    var selectedWorkout by mutableStateOf(WorkoutListBean.Data())
        private set

    fun setWorkout(workout: WorkoutListBean.Data?) {
        setRefresh(false)
        selectedWorkout = workout ?: WorkoutListBean.Data()
    }

    private val _isrefreshFlow = MutableStateFlow(false)
    val isrefresh = _isrefreshFlow.asStateFlow()
    fun setRefresh(value:Boolean=false) {
        _isrefreshFlow.value = value
    }
}