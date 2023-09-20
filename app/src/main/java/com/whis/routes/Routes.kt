package com.whis.routes

sealed class Screen(val route:String){
    object WorkoutList : Screen(route = "workout_list")
    object WorkoutEdit : Screen(route = "workout_edit")


    fun withArgs(vararg args:String) : String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}