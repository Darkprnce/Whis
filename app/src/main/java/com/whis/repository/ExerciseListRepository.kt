package com.whis.repository

import com.whis.BuildConfig
import com.whis.Network.ApiServices
import com.whis.model.ExerciseListBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.HashMap
import javax.inject.Inject

class ExerciseListRepository @Inject constructor(
    private val workoutClient:ApiServices
) {

    suspend fun getExerciseList(data: HashMap<String?, Any?>): ExerciseListBean? =
        withContext(Dispatchers.IO) {
            data.put("username", "darkprnce")
            for (item in data.values) {
                if (item is String || item is Int || item is Double || item is Float) {
                    item.toString().trim()
                }
            }
            val response = workoutClient.exercise_list(BuildConfig.API_KEY,data)
            response.body()
        }
}