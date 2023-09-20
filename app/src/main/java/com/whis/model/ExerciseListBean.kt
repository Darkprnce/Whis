package com.whis.model

data class ExerciseListBean(
    var `data`: List<Data?>? = null,
    var msg: String? = null,
    var response_time: String? = null,
    var status: String? = null
) {
    data class Data(
        var bodypart: String? = null,
        var duration: Int? = null,
        var equipment: String? = null,
        var gifurl: String? = null,
        var id: Int? = null,
        var name: String? = null,
        var reps: Int? = null,
        var rest_after_completion: Int? = null,
        var rests: Int? = null,
        var sets: Int? = null,
        var target: String? = null,
        var selected: Boolean = false
    )
}