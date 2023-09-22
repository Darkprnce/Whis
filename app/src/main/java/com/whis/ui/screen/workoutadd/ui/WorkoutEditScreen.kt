package com.whis.ui.screen.workoutadd.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.whis.R
import com.whis.ui.customComposables.CustomButton
import com.whis.ui.customComposables.CustomDialog
import com.whis.ui.customComposables.CustomTextField
import com.whis.ui.customComposables.GifImage
import com.whis.model.ExerciseListBean
import com.whis.ui.customComposables.CustomText
import com.whis.ui.customComposables.MainLayout
import com.whis.ui.screen.SharedViewModel
import com.whis.ui.screen.workoutadd.viewmodel.ValidationState
import com.whis.ui.screen.workoutadd.viewmodel.WorkoutAddViewModel
import com.whis.ui.theme.Blue
import com.whis.ui.theme.Green
import com.whis.ui.theme.Red
import com.whis.ui.theme.White
import com.whis.utils.checkString
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import timber.log.Timber

@Composable
fun WorkoutEditScreen(
    viewModel: WorkoutAddViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.setWorkout(sharedViewModel.selectedWorkout)

        viewModel.apiState.collect { api_state ->
            when (api_state) {
                is ValidationState.Loading -> {

                }

                is ValidationState.Error -> {
                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar(
                                            message = "Please fill all the fields",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                }

                is ValidationState.Ideal -> {
                    Timber.e("Api Initiated")
                }

                is ValidationState.Success -> {
                    Timber.e("get")
                    Toast.makeText(
                        context,
                        (api_state).msg,
                        Toast.LENGTH_SHORT
                    ).show()
                    navHostController.navigateUp()
                }
            }
        }
    }
    val workout by viewModel.selectedWorkout.collectAsStateWithLifecycle()

    val state = rememberReorderableLazyListState(onMove = { from, to ->
        viewModel.swapExercises(from.index, to.index)
    })

    val title by viewModel.titleInputFlow.collectAsStateWithLifecycle()
    val totalTime by viewModel.totalTimeInputFlow.collectAsStateWithLifecycle()
    val userTime by viewModel.userTimeInputFlow.collectAsStateWithLifecycle()
    val heartrateMax by viewModel.heartrateMaxInputFlow.collectAsStateWithLifecycle()
    val heartrateMin by viewModel.heartrateMinInputFlow.collectAsStateWithLifecycle()
    val stress by viewModel.stressInputFlow.collectAsStateWithLifecycle()
    val spo2 by viewModel.spo2InputFlow.collectAsStateWithLifecycle()
    val calorie by viewModel.calorieInputFlow.collectAsStateWithLifecycle()
    val musicUrl by viewModel.musicurlInputFlow.collectAsStateWithLifecycle()
    val imageUrl by viewModel.imageurlInputFlow.collectAsStateWithLifecycle()
    val search by viewModel.searchInputFlow.collectAsStateWithLifecycle()
    val showRemoveWorkout by viewModel.showRemoveWorkout.collectAsStateWithLifecycle()
    val showAddExercise by viewModel.showAddExercise.collectAsStateWithLifecycle()
    val showExerciseMove by viewModel.showExerciseMove.collectAsStateWithLifecycle()
    val exersisesSearch by viewModel.exercisesSearch.collectAsStateWithLifecycle()
    val workoutExercises by viewModel.workoutExercises.collectAsStateWithLifecycle()


    MainLayout(title = if (checkString(workout.title, isempty = true).isEmpty()) {
        "Add Workout"
    } else {
        "Edit Workout"
    }, navHostController = navHostController, actions = {
        if (checkString(workout.title, isempty = true).isNotEmpty()) {
            IconButton(onClick = {
                viewModel.setshowRemoveWorkout(true)
            }) {
                Icon(
                    Icons.Filled.Delete, "remove_icon", tint = White
                )
            }
        }
    }, content = { snackBarHostState ->

        if (showRemoveWorkout) {
            CustomDialog(onDismissRequest = {
                viewModel.setshowRemoveWorkout(false)
            }) {
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
                        value = "Cancel", bgcolor = Red, onClick = {
                            viewModel.setshowRemoveWorkout(false)
                        }, modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.1f))
                    CustomButton(
                        value = "Remove", bgcolor = Green, onClick = {
                            val data = HashMap<String?, Any?>()
                            data.put("id", workout.id)
                            viewModel.removeWorkout(data)
                        }, modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }

            }
        }

        if (showAddExercise) {
            CustomDialog(
                onDismissRequest = {
                    viewModel.setshowAddExercise(false)
                }, isSmall = false
            ) {
                CustomTextField(title = "Search", value = search, onValueChange = {
                    viewModel.setSearch(it)
                }, starticon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                        )
                    }
                }, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                )
                LazyColumn(
                    modifier = modifier.height(450.dp)
                ) {
                    items(exersisesSearch, key = { item -> "search_${item!!.id!!}" }) { item ->
                        ExerciseSearchItem(
                            item = item!!, viewModel, modifier = modifier
                        )
                    }
                }
                CustomButton(
                    value = "Save", onClick = {
                        viewModel.setshowAddExercise(false)
                    }, modifier = modifier.fillMaxWidth()
                )
            }
        }

        if (showExerciseMove) {
            CustomDialog(
                onDismissRequest = {
                    viewModel.setShowExerciseMove(false)
                }, isSmall = false
            ) {
                CustomText(
                    value = "Exercises",
                    isheading = true,
                    txtsize = 18.sp,
                    modifier = modifier.align(Alignment.Start)
                )
                LazyColumn(
                    state = state.listState,
                    modifier = modifier
                        .height(500.dp)
                        .reorderable(state)
                        .detectReorderAfterLongPress(state)
                ) {
                    items(workoutExercises, key = { item -> "move ${item.id!!}" }) { item ->
                        ReorderableItem(state, key = item.id!!) { isDragging ->
                            ExerciseMoveItem(item = item, isDragging, modifier = modifier)
                        }
                    }
                }
                CustomButton(
                    value = "Save", onClick = {
                        viewModel.setShowExerciseMove(false)
                    }, modifier = modifier.fillMaxWidth()
                )
            }
        }

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(10.dp)
        ) {

            CustomTextField(
                title = "Title", value = title, onValueChange = {
                    viewModel.setTitle(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            CustomTextField(
                title = "Total Time", value = totalTime, onValueChange = {
                    viewModel.setTotalTime(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            CustomTextField(
                title = "User Time", value = userTime, onValueChange = {
                    viewModel.setUserTime(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            CustomTextField(
                title = "Heartrate Max.", value = heartrateMax, onValueChange = {
                    viewModel.setHeartrateMax(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            CustomTextField(
                title = "Heartrate Min.", value = heartrateMin, onValueChange = {
                    viewModel.setHeartrateMin(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            CustomTextField(
                title = "Stress", value = stress, onValueChange = {
                    viewModel.setStress(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            CustomTextField(
                title = "Spo2", value = spo2, onValueChange = {
                    viewModel.setSpo2(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            CustomTextField(
                title = "Calorie", value = calorie, onValueChange = {
                    viewModel.setCalorie(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            CustomTextField(
                title = "Music Url", value = musicUrl, onValueChange = {
                    viewModel.setMusicUrl(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            CustomTextField(
                title = "Image Url", value = imageUrl, isLast = true, onValueChange = {
                    viewModel.setImageUrl(it)
                }, isvalidate = true, modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            )
            GifImage(
                imageUrl = imageUrl,
                modifier = modifier
                    .padding(top = 5.dp)
                    .height(100.dp)
                    .width(200.dp)
            )


            CustomButton(
                value = if (checkString(workout.title, isempty = true).isEmpty()) {
                    "Add Workout"
                } else {
                    "Edit Workout"
                }, onClick = {
                    viewModel.validateForm()
                }, modifier = modifier.fillMaxWidth()
            )

            Row {
                CustomButton(
                    value = "View Exercises", bgcolor = Blue, onClick = {
                        viewModel.setShowExerciseMove(true)
                    }, modifier = modifier
                        .fillMaxWidth()
                        .weight(1.0f)
                )
                Spacer(modifier = Modifier.weight(0.1f))
                CustomButton(
                    value = "Add Exercise", bgcolor = Green, onClick = {
                        viewModel.setSearch("")
                        viewModel.setshowAddExercise(true)
                    }, modifier = modifier
                        .fillMaxWidth()
                        .weight(1.0f)
                )
            }
            /*if (apiSuccess!!.isNotEmpty()) {
                //Toast.makeText(context, apiSuccess!!, Toast.LENGTH_SHORT).show()
                navHostController.navigateUp()
            }*/
        }
    })

}

@Composable
fun ExerciseMoveItem(item: ExerciseListBean.Data, isDragging: Boolean, modifier: Modifier) {
    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "exercise_move")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.rearrange_icon),
            contentDescription = "rearranege ${item.name!!}",
            modifier = modifier.height(20.dp)
        )
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(elevation.value),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GifImage(
                    imageUrl = item.gifurl!!, modifier = modifier
                        .height(80.dp)
                        .width(80.dp)
                )
                CustomText(
                    item.name!!, modifier = modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun ExerciseSearchItem(
    item: ExerciseListBean.Data, viewModel: WorkoutAddViewModel, modifier: Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = item.selected,
            onClick = { viewModel.addExercise(item) })
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable { viewModel.addExercise(item) },
        ) {
            GifImage(
                imageUrl = item.gifurl!!, modifier = modifier
                    .height(50.dp)
                    .width(50.dp)
            )
            CustomText(
                item.name!!, modifier = modifier.padding(10.dp)
            )
        }
    }
}