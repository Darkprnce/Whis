package com.whis.ui.screen.workoutadd.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.navigation.NavHostController
import com.whis.R
import com.whis.ui.customComposables.CustomButton
import com.whis.ui.customComposables.CustomDialog
import com.whis.ui.customComposables.CustomTextField
import com.whis.ui.customComposables.GifImage
import com.whis.model.ExerciseListBean
import com.whis.ui.customComposables.CustomText
import com.whis.ui.customComposables.MainLayout
import com.whis.ui.screen.workoutlist.viewmodel.WorkoutListViewModel
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

@Composable
fun WorkoutEditScreen(
    workoutListViewModel: WorkoutListViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    var showRemoveWorkout by remember { mutableStateOf(false) }
    var showAddExercise by remember { mutableStateOf(false) }
    var showExerciseMove by remember { mutableStateOf(false) }

    val state = rememberReorderableLazyListState(onMove = { from, to ->
        workoutListViewModel.swapExercises(from.index, to.index)
    })

    var title by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.title,
                isempty = true
            )
        )
    }
    var totalTime by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.total_time,
                isempty = true
            )
        )
    }
    var userTime by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.user_time,
                isempty = true
            )
        )
    }
    var heartrateMax by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.heartrate_max,
                isempty = true
            )
        )
    }
    var heartrateMin by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.heartrate_min,
                isempty = true
            )
        )
    }
    var stress by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.stress,
                isempty = true
            )
        )
    }
    var spo2 by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.spo2,
                isempty = true
            )
        )
    }
    var calorie by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.calorie,
                isempty = true
            )
        )
    }
    var music_url by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.music_url,
                isempty = true
            )
        )
    }
    var image_url by remember {
        mutableStateOf(
            checkString(
                workoutListViewModel.selectedWorkout.value.image_url,
                isempty = true
            )
        )
    }

    var search by remember { mutableStateOf("") }

    val apiSuccess by workoutListViewModel.api_status.observeAsState()

    val coroutineScope = rememberCoroutineScope()
    MainLayout(
        title =
        if (checkString(workoutListViewModel.selectedWorkout.value.title, isempty = true).isEmpty()) {
            "Add Workout"
        } else {
            "Edit Workout"
        },
        navHostController = navHostController,
        actions = {
            if (checkString(workoutListViewModel.selectedWorkout.value.title, isempty = true).isNotEmpty()) {
                IconButton(onClick = {
                    showRemoveWorkout = true
                }) {
                    Icon(
                        Icons.Filled.Delete,
                        "remove_icon",
                        tint = White
                    )
                }
            }
        },
        content = { snackBarHostState ->
            if (showRemoveWorkout) {
                CustomDialog(
                    onDismissRequest = {
                        showRemoveWorkout = false
                    }
                ) {
                    CustomText(
                        value = "Remove ${workoutListViewModel.selectedWorkout.value.title}",
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
                            }, modifier = modifier
                                .fillMaxWidth()
                                .weight(1.0f)
                        )
                        Spacer(modifier = Modifier.weight(0.1f))
                        CustomButton(
                            value = "Remove", bgcolor = Green, onClick = {
                                val data = HashMap<String?, Any?>()
                                data.put("id", workoutListViewModel.selectedWorkout.value.id)
                                workoutListViewModel.removeWorkout(data)
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
                        showAddExercise = false
                    },
                    isSmall = false
                ) {
                    CustomTextField(
                        title = "Search",
                        value = search,
                        onValueChange = {
                            search = it
                            workoutListViewModel.searchExercise(it)
                        },
                        starticon = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Search",
                                )
                            }
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp)
                    )
                    LazyColumn(
                        modifier = modifier.height(450.dp)
                    ) {
                        items(
                            workoutListViewModel.exercises_search,
                            key = { item -> "search_${item!!.id!!}" }) { item ->
                            ExerciseSearchItem(
                                item = item!!,
                                workoutListViewModel,
                                modifier = modifier
                            )
                        }
                    }
                    CustomButton(
                        value = "Save",
                        onClick = {
                            showAddExercise = false
                        }, modifier = modifier
                            .fillMaxWidth()
                    )
                }
            }

            if (showExerciseMove) {
                CustomDialog(
                    onDismissRequest = {
                        showExerciseMove = false
                    },
                    isSmall = false
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
                        items(
                            workoutListViewModel.workout_exercises,
                            key = { item -> item.id!! }) { item ->
                            ReorderableItem(state, key = item.id!!) { isDragging ->
                                ExerciseMoveItem(item = item, isDragging, modifier = modifier)
                            }
                        }
                    }
                    CustomButton(
                        value = "Save",
                        onClick = {
                            showExerciseMove = false
                        }, modifier = modifier
                            .fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {

                CustomTextField(
                    title = "Title",
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Total Time",
                    value = totalTime,
                    onValueChange = {
                        totalTime = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "User Time",
                    value = userTime,
                    onValueChange = {
                        userTime = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Heartrate Max.",
                    value = heartrateMax,
                    onValueChange = {
                        heartrateMax = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Heartrate Min.",
                    value = heartrateMin,
                    onValueChange = {
                        heartrateMin = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Stress",
                    value = stress,
                    onValueChange = {
                        stress = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Spo2",
                    value = spo2,
                    onValueChange = {
                        spo2 = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Calorie",
                    value = calorie,
                    onValueChange = {
                        calorie = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Music Url",
                    value = music_url,
                    onValueChange = {
                        music_url = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Image Url",
                    value = image_url,
                    onValueChange = {
                        image_url = it
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                GifImage(
                    image_url = image_url, modifier = modifier
                        .padding(top = 5.dp)
                        .height(100.dp)
                        .width(200.dp)
                )


                CustomButton(value = if (checkString(
                        workoutListViewModel.selectedWorkout.value.title,
                        isempty = true
                    ).isEmpty()
                ) {
                    "Add Workout"
                } else {
                    "Edit Workout"
                }, onClick = {
                    if (title.isEmpty() || totalTime.isEmpty() || userTime.isEmpty() || image_url.isEmpty()) {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = "Please fill all the fields",
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        val data = HashMap<String?, Any?>()
                        data.put("id", workoutListViewModel.selectedWorkout.value.id)
                        data.put("title", title)
                        data.put("total_time", totalTime)
                        data.put("user_time", userTime)
                        data.put("heartrate_max", heartrateMax)
                        data.put("heartrate_min", heartrateMin)
                        data.put("stress", stress)
                        data.put("spo2", spo2)
                        data.put("calorie", calorie)
                        data.put("music_url", music_url)
                        data.put("image_url", image_url)
                        val exercise_list = ArrayList<String>()
                        for (item in workoutListViewModel.workout_exercises) {
                            exercise_list.add(item.id.toString())
                        }
                        data.put("exercises_id", exercise_list)
                        workoutListViewModel.addWorkout(data)
                    }
                }, modifier = modifier.fillMaxWidth()
                )

                Row {
                    CustomButton(
                        value = "View Exercises", bgcolor = Blue, onClick = {
                            showExerciseMove = true
                        }, modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.1f))
                    CustomButton(
                        value = "Add Exercise", bgcolor = Green, onClick = {
                            showAddExercise = true
                            workoutListViewModel.searchExercise("")
                        }, modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }
                if (apiSuccess!!.isNotEmpty()) {
                    //Toast.makeText(context, apiSuccess!!, Toast.LENGTH_SHORT).show()
                    navHostController.navigateUp()
                }
            }
        })

}

@Composable
fun ExerciseMoveItem(item: ExerciseListBean.Data, isDragging: Boolean, modifier: Modifier) {
    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
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
                .shadow(elevation.value)
                .clickable { /* Handle item click */ },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GifImage(
                    image_url = item.gifurl!!, modifier = modifier
                        .height(80.dp)
                        .width(80.dp)
                )
                CustomText(
                    item.name!!,
                    modifier = modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun ExerciseSearchItem(
    item: ExerciseListBean.Data,
    workoutListViewModel: WorkoutListViewModel,
    modifier: Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = workoutListViewModel.exerciseExist(item),
            onClick = { workoutListViewModel.addExercise(item) }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable { workoutListViewModel.addExercise(item) },
        ) {
            GifImage(
                image_url = item.gifurl!!, modifier = modifier
                    .height(50.dp)
                    .width(50.dp)
            )
            CustomText(
                item.name!!,
                modifier = modifier.padding(10.dp)
            )
        }
    }
}