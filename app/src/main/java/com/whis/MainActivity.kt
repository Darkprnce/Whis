package com.whis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whis.model.BottomNavigationItem
import com.whis.routes.Screen
import com.whis.ui.customComposables.CustomText
import com.whis.ui.customComposables.MyScaffold
import com.whis.ui.screen.SharedViewModel
import com.whis.ui.screen.exerciselist.ui.ExerciseListScreen
import com.whis.ui.screen.workoutadd.ui.WorkoutAddScreen
import com.whis.ui.screen.workoutlist.ui.WorkoutListScreen
import com.whis.ui.theme.WhisTheme
import com.whis.ui.theme.White
import com.whis.ui.theme.WhiteSmoke
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
                    val navHostController = rememberNavController()
                    var navigationSelectedItem by remember { mutableStateOf(0) }
                    MyScaffold(
                        isbackAvailable = false,
                        navHostController = navHostController,
                        content = { _ ->
                            NavHost(
                                navController = navHostController,
                                startDestination = Screen.WorkoutList.route
                            ) {
                                composable(
                                    route = Screen.WorkoutList.route,
                                    content = {
                                        WorkoutListScreen(
                                            sharedViewModel = sharedViewModel,
                                            navHostController = navHostController
                                        )
                                    })
                                composable(
                                    route = Screen.WorkoutEdit.route,
                                    content = {
                                        WorkoutAddScreen(
                                            sharedViewModel = sharedViewModel,
                                            navHostController = navHostController
                                        )
                                    })
                                composable(
                                    route = Screen.ExerciseList.route,
                                    content = {
                                        ExerciseListScreen(
                                            sharedViewModel = sharedViewModel,
                                            navHostController = navHostController
                                        )
                                    })
                            }
                        }, bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                            ) {
                                BottomNavigationItem().bottomNavigationItems()
                                    .forEachIndexed { index, navigationItem ->
                                        NavigationBarItem(
                                            selected = index == navigationSelectedItem,
                                            label = {
                                                CustomText(
                                                    navigationItem.label, color =
                                                    if (index == navigationSelectedItem) {
                                                        White
                                                    } else {
                                                        WhiteSmoke
                                                    }
                                                )
                                            },
                                            icon = {
                                                Icon(
                                                    navigationItem.icon,
                                                    contentDescription = navigationItem.label
                                                )
                                            },
                                            onClick = {
                                                navigationSelectedItem = index
                                                navHostController.navigate(navigationItem.route) {
                                                    popUpTo(navHostController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                            }
                        })
                }
            }
        }
    }
}