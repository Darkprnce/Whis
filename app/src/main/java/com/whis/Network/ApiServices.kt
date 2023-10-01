package com.whis.Network

import com.skydoves.sandwich.ApiResponse
import com.whis.model.ExerciseAddBean
import com.whis.model.ExerciseListBean
import com.whis.model.ExerciseRemoveBean
import com.whis.model.WorkoutAddBean
import com.whis.model.WorkoutListBean
import com.whis.model.WorkoutRemoveBean
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServices {

    @POST("admin_workout_list")
    suspend fun workout_list(
        @Header("api_key") api_key: String,
        @Body body: HashMap<String?, Any?>?
    ): ApiResponse<WorkoutListBean?>

    @POST("admin_add_workout")
    suspend fun workout_add(
        @Header("api_key") api_key: String,
        @Body body: HashMap<String?, Any?>?
    ): ApiResponse<WorkoutAddBean?>

    @POST("admin_remove_workout")
    suspend fun workout_remove(
        @Header("api_key") api_key: String,
        @Body body: HashMap<String?, Any?>?
    ): ApiResponse<WorkoutRemoveBean?>

    @POST("admin_exercise_list")
    suspend fun exercise_list(
        @Header("api_key") api_key: String,
        @Body body: HashMap<String?, Any?>?
    ): ApiResponse<ExerciseListBean?>

    @POST("admin_add_exercise")
    suspend fun exercise_add(
        @Header("api_key") api_key: String,
        @Body body: HashMap<String?, Any?>?
    ): ApiResponse<ExerciseAddBean?>

    @POST("admin_remove_exercise")
    suspend fun exercise_remove(
        @Header("api_key") api_key: String,
        @Body body: HashMap<String?, Any?>?
    ): ApiResponse<ExerciseRemoveBean?>
}