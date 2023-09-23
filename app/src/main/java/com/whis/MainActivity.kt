package com.whis

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whis.ui.screen.SharedViewModel
import com.whis.ui.screen.workoutadd.ui.WorkoutEditScreen
import com.whis.ui.screen.workoutadd.viewmodel.WorkoutAddViewModel
import com.whis.ui.screen.workoutlist.ui.WorkoutListScreen
import com.whis.ui.screen.workoutlist.viewmodel.WorkoutListViewModel
import com.whis.ui.theme.WhisTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedViewModel: SharedViewModel by viewModels()

        setContent {
            WhisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "workout_list") {
                        composable(
                            route = "workout_list",
                            content = {
                                WorkoutListScreen(
                                    sharedViewModel=sharedViewModel,
                                    navHostController = navController
                                )
                            })
                        composable(
                            route = "workout_edit",
                            content = {
                                WorkoutEditScreen(
                                    sharedViewModel=sharedViewModel,
                                    navHostController = navController
                                )
                            })
                    }
                }
            }
        }
    }
}