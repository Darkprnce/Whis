package com.whis.ui.screen.workoutlist.ui

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.whis.R
import com.whis.model.WorkoutListBean
import com.whis.ui.customComposables.CustomButton
import com.whis.ui.customComposables.CustomDialog
import com.whis.ui.customComposables.CustomText
import com.whis.ui.customComposables.GifImage
import com.whis.ui.customComposables.MainLayout
import com.whis.ui.customComposables.ShimmerListItem
import com.whis.ui.screen.SharedViewModel
import com.whis.ui.screen.workoutlist.viewmodel.WorkoutListViewModel
import com.whis.ui.theme.Green
import com.whis.ui.theme.Red
import com.whis.ui.theme.White

@Composable
fun WorkoutListScreen(
    workoutListViewModel: WorkoutListViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
) {
    val workouts by workoutListViewModel.workoutList.observeAsState()
    val isLoading by workoutListViewModel.isLoading.observeAsState()

    MainLayout(
        title = "Workout List",
        isbackAvailable = false,
        navHostController = navHostController,
        actions = {
            IconButton(onClick = {
                sharedViewModel.setWorkout(null)
                workoutListViewModel.setWorkout(null)
                navHostController.navigate("workout_edit")
            }) {
                Icon(
                    Icons.Filled.Add,
                    "add_icon",
                    tint = White
                )
            }
        },
        content = { snackBarHostState ->
            ShimmerListItem(
                isLoading = isLoading!!,
                contentAfterLoading = {
                    if(workouts!!.isNotEmpty()){
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                workouts!!,
                                key = { item -> "workout_${item!!.id!!}" }) { item ->
                                WorkoutItem(
                                    modifier = modifier,
                                    workout = item!!,
                                    workoutListViewModel = workoutListViewModel,
                                    sharedViewModel = sharedViewModel,
                                    navHostController = navHostController
                                )
                            }
                        }
                    }else{
                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            GifImage(imageUrl = R.drawable.no_record_icon, modifier = modifier.height(250.dp))
                            CustomText(value = "No Workouts",modifier = modifier.padding(top = 10.dp))
                        }
                    }
                })
        })
}

@Composable
fun WorkoutItem(
    workout: WorkoutListBean.Data,
    workoutListViewModel: WorkoutListViewModel,
    navHostController: NavHostController,
    modifier: Modifier,
    sharedViewModel: SharedViewModel
) {
    var showRemoveWorkout by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    Log.e("TAG", "WorkoutItem: ${workout.title}",)
                    sharedViewModel.setWorkout(workout)
                    workoutListViewModel.setWorkout(workout)
                    navHostController.navigate("workout_edit")
                }, onLongPress = {
                    showRemoveWorkout = true
                })
            }
    ) {
        Box(contentAlignment = Alignment.BottomStart) {
            GifImage(
                imageUrl = workout.image_url!!,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .drawWithCache {
                        val gradient = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = size.height / 3,
                            endY = size.height
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(gradient, blendMode = BlendMode.Multiply)
                        }
                    }
            )
            CustomText(
                workout.title!!,
                color = White,
                isbold = true,
                modifier = Modifier.padding(10.dp)
            )
        }
    }

    if (showRemoveWorkout) {
        CustomDialog(
            onDismissRequest = {
                showRemoveWorkout = false
            }
        ) {
            CustomText(
                value = "Remove ${workout.title}",
                isheading = true,
                txtsize = 18.sp,
                modifier = modifier.align(Alignment.Start)
            )
            CustomText(
                value = "Do you want to remove this workout ?",
                modifier = modifier.align(Alignment.Start)
            )
            Row {
                CustomButton(
                    value = "Cancel",
                    bgcolor = Red,
                    onClick = {
                        showRemoveWorkout = false
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f)
                )
                Spacer(modifier = Modifier.weight(0.1f))
                CustomButton(
                    value = "Remove", bgcolor = Green, onClick = {
                        val data = HashMap<String?, Any?>()
                        data.put("id", workout.id)
                        workoutListViewModel.removeWorkout(data)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f)
                )
            }

        }
    }
}