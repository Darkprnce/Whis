package com.whis.repository

import com.whis.BuildConfig
import com.whis.Network.ApiServices
import com.whis.model.WorkoutListBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.HashMap
import javax.inject.Inject

class WorkoutListRepository @Inject constructor(
    private val workoutClient:ApiServices
) {

    suspend fun getWorkoutList(data: HashMap<String?, Any?>): WorkoutListBean? =
        withContext(Dispatchers.IO) {
            val response = workoutClient.workout_list(BuildConfig.API_KEY,data)
            response.body()
        }
}