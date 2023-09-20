package com.whis.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whis.model.ExerciseListBean
import com.whis.model.WorkoutListBean
import com.whis.repository.ExerciseListRepository
import com.whis.repository.WorkoutAddRepository
import com.whis.repository.WorkoutRemoveRepository
import com.whis.utils.SOME_ERROR_OCCURED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedViewModel : ViewModel() {

    var selectedWorkout by mutableStateOf(WorkoutListBean.Data())
        private set

    fun setWorkout(workout: WorkoutListBean.Data?) {
        Log.e("TAG", "setWorkout: ", )
        if (workout != null) {
            selectedWorkout = workout
        } else {
            selectedWorkout = WorkoutListBean.Data()
        }
    }
}