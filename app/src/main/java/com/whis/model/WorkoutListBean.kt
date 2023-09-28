package com.whis.model

data class WorkoutListBean(
    var `data`: List<Data>? = null,
    var msg: String? = null,
    var response_time: String? = null,
    var status: String? = null
) {
    data class Data(
        var calorie: String? = null,
        var exercises_id: List<ExercisesId?>? = null,
        var heartrate_max: String? = null,
        var heartrate_min: String? = null,
        var id: Int? = null,
        var image_url: String? = null,
        var music_url: String? = null,
        var spo2: String? = null,
        var stress: String? = null,
        var title: String? = null,
        var total_time: String? = null,
        var user_time: String? = null
    ) {
        data class ExercisesId(
            var bodypart: String? = null,
            var duration: String? = null,
            var equipment: String? = null,
            var gifurl: String? = null,
            var id: Int? = null,
            var name: String? = null,
            var reps: String? = null,
            var rest_after_completion: String? = null,
            var rests: String? = null,
            var sets: String? = null,
            var target: String? = null
        )
    }
}