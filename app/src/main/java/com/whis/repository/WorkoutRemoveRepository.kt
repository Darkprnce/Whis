package com.whis.repository

import com.whis.BuildConfig
import com.whis.Network.ApiServices
import com.whis.model.WorkoutRemoveBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.HashMap
import javax.inject.Inject

class WorkoutRemoveRepository @Inject constructor(
    private val workoutClient:ApiServices
) {

    suspend fun removeWorkout(data: HashMap<String?, Any?>): WorkoutRemoveBean? =
        withContext(Dispatchers.IO) {
            val response = workoutClient.workout_remove(BuildConfig.API_KEY,data)
            response.body()
        }
}