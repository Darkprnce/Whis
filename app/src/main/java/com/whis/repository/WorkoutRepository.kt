package com.whis.repository

import com.google.gson.Gson
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import com.whis.BuildConfig
import com.whis.Network.ApiServices
import com.whis.Network.sealed.ApiResp
import com.whis.model.WorkoutAddBean
import com.whis.model.WorkoutListBean
import com.whis.model.WorkoutRemoveBean
import com.whis.utils.NO_INTERNET_CONNECTION
import com.whis.utils.SERVER_ERROR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.HashMap
import java.util.concurrent.Flow
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    private val workoutClient: ApiServices
) {

    suspend fun getWorkoutList(data: HashMap<String?, Any?>) =
        flow {
            val tag = "workout_list"
            emit(ApiResp.Loading(tag))
            data["username"] = "darkprnce"
            for (item in data.values) {
                if (item is String || item is Int || item is Double || item is Float) {
                    item.toString().trim()
                }
            }

            val response = workoutClient.workout_list(BuildConfig.API_KEY, data)
            response.suspendOnSuccess {
                emit(ApiResp.Success(tag, this.data))
            }.suspendOnError {
                emit(ApiResp.Error(tag, SERVER_ERROR,null))
            }.suspendOnException {
                emit(ApiResp.Error(tag, NO_INTERNET_CONNECTION,null))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun addWorkout(data: HashMap<String?, Any?>): WorkoutAddBean? =
        withContext(Dispatchers.IO) {
            data.put("username", "darkprnce")
            for (item in data.values) {
                if (item is String || item is Int || item is Double || item is Float) {
                    item.toString().trim()
                }
            }
            val response = workoutClient.workout_add(BuildConfig.API_KEY, data)
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
            val response = workoutClient.workout_remove(BuildConfig.API_KEY, data)
            response.body()
        }
}