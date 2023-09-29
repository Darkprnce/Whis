package com.whis.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.whis.routes.Screen

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Build,
    val route : String = ""
) {

    //function to get the list of bottomNavigationItems
    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Workout",
                icon = Icons.Filled.Build,
                route = Screen.WorkoutList.route
            ),
            BottomNavigationItem(
                label = "Exercise",
                icon = Icons.Filled.List,
                route = Screen.ExerciseList.route
            ),
        )
    }
}