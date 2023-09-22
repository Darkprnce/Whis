package com.whis.repository

import com.whis.BuildConfig
import com.whis.Network.ApiServices
import com.whis.model.WorkoutAddBean
import com.whis.model.WorkoutListBean
import com.whis.utils.API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.HashMap
import javax.inject.Inject

class WorkoutAddRepository @Inject constructor(
    private val workoutClient:ApiServices
) {

    suspend fun addWorkout(data: HashMap<String?, Any?>): WorkoutAddBean? =
        withContext(Dispatchers.IO) {
            val response = workoutClient.workout_add(BuildConfig.API_KEY,data)
            response.body()
        }
}