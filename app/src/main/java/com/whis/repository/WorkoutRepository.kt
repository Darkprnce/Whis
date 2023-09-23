package com.whis.repository

import com.whis.BuildConfig
import com.whis.Network.ApiServices
import com.whis.model.WorkoutAddBean
import com.whis.model.WorkoutListBean
import com.whis.model.WorkoutRemoveBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.HashMap
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    private val workoutClient:ApiServices
) {

    suspend fun getWorkoutList(data: HashMap<String?, Any?>): WorkoutListBean? =
        withContext(Dispatchers.IO) {
            data.put("username", "darkprnce")
            for (item in data.values) {
                if (item is String || item is Int || item is Double || item is Float) {
                    item.toString().trim()
                }
            }
            val response = workoutClient.workout_list(BuildConfig.API_KEY,data)
            response.body()
        }

    suspend fun addWorkout(data: HashMap<String?, Any?>): WorkoutAddBean? =
        withContext(Dispatchers.IO) {
            data.put("username", "darkprnce")
            for (item in data.values) {
                if (item is String || item is Int || item is Double || item is Float) {
                    item.toString().trim()
                }
            }
            val response = workoutClient.workout_add(BuildConfig.API_KEY,data)
            response.body()
        }
    suspend fun removeWorkout(data: HashMap<String?, Any?>): WorkoutRemoveBean? =
        withContext(Dispatchers.IO) {
            data.put("username", "darkprnce")
            for (item in data.values) {
                if (item is String || item is Int || item is Double || item is Float) {
                    item.toString().trim()
                }
            }
            val response = workoutClient.workout_remove(BuildConfig.API_KEY,data)
            response.body()
        }
}