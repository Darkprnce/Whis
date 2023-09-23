package com.whis.ui.screen.workoutadd.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.whis.Network.ValidationState
import com.whis.R
import com.whis.ui.customComposables.CustomButton
import com.whis.ui.customComposables.CustomDialog
import com.whis.ui.customComposables.CustomTextField
import com.whis.ui.customComposables.GifImage
import com.whis.model.ExerciseListBean
import com.whis.ui.customComposables.CustomText
import com.whis.ui.customComposables.LoadingDialog
import com.whis.ui.customComposables.MyScaffold
import com.whis.ui.screen.SharedViewModel
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
    modifier: Modifier = Modifier,
    workoutAddViewModel: WorkoutAddViewModel = hiltViewModel(),
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        workoutAddViewModel.setWorkout(sharedViewModel.selectedWorkout)

        workoutAddViewModel.apiState.collect { api_state ->
            when (api_state) {
                is ValidationState.Loading -> {
                    if (api_state.isLoading) {
                        workoutAddViewModel.setShowLoading(true)
                    } else {
                        workoutAddViewModel.setShowLoading(false)
                    }
                }

                is ValidationState.Error -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = api_state.errorMsg,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is ValidationState.Ideal -> {
                    Timber.e("Api Initiated")
                }

                is ValidationState.Success -> {
                    Toast.makeText(
                        context,
                        api_state.data as String,
                        Toast.LENGTH_SHORT
                    ).show()
                    //navHostController.getNavResultCallback<Unit>()?.invoke(Unit)
                    if(api_state.tag.equals("add_workout")||api_state.tag.equals("remove_workout")){
                        sharedViewModel.setRefresh(true)
                        navHostController.navigateUp()
                    }
                }
            }
        }
    }
    val workout by workoutAddViewModel.selectedWorkout.collectAsStateWithLifecycle()

    val state = rememberReorderableLazyListState(onMove = { from, to ->
        workoutAddViewModel.swapExercises(from.index, to.index)
    })

    val showLoading by workoutAddViewModel.showLoading.collectAsStateWithLifecycle()
    val title by workoutAddViewModel.titleInputFlow.collectAsStateWithLifecycle()
    val totalTime by workoutAddViewModel.totalTimeInputFlow.collectAsStateWithLifecycle()
    val userTime by workoutAddViewModel.userTimeInputFlow.collectAsStateWithLifecycle()
    val heartrateMax by workoutAddViewModel.heartrateMaxInputFlow.collectAsStateWithLifecycle()
    val heartrateMin by workoutAddViewModel.heartrateMinInputFlow.collectAsStateWithLifecycle()
    val stress by workoutAddViewModel.stressInputFlow.collectAsStateWithLifecycle()
    val spo2 by workoutAddViewModel.spo2InputFlow.collectAsStateWithLifecycle()
    val calorie by workoutAddViewModel.calorieInputFlow.collectAsStateWithLifecycle()
    val musicUrl by workoutAddViewModel.musicurlInputFlow.collectAsStateWithLifecycle()
    val imageUrl by workoutAddViewModel.imageurlInputFlow.collectAsStateWithLifecycle()
    val search by workoutAddViewModel.searchInputFlow.collectAsStateWithLifecycle()
    val showRemoveWorkout by workoutAddViewModel.showRemoveWorkout.collectAsStateWithLifecycle()
    val showAddExercise by workoutAddViewModel.showAddExercise.collectAsStateWithLifecycle()
    val showExerciseMove by workoutAddViewModel.showExerciseMove.collectAsStateWithLifecycle()
    val exersisesSearch by workoutAddViewModel.exercisesSearch.collectAsStateWithLifecycle()
    val workoutExercises by workoutAddViewModel.workoutExercises.collectAsStateWithLifecycle()


    MyScaffold(title = if (checkString(workout.title, isempty = true).isEmpty()) {
        "Add Workout"
    } else {
        "Edit Workout"
    }, navHostController = navHostController,
        snackBarState = snackBarHostState,
        actions = {
            if (checkString(workout.title, isempty = true).isNotEmpty()) {
                IconButton(onClick = {
                    workoutAddViewModel.setshowRemoveWorkout(true)
                }) {
                    Icon(
                        Icons.Filled.Delete, "remove_icon", tint = White
                    )
                }
            }
        }, content = { _ ->
            if (showLoading) {
                LoadingDialog()
            }

            if (showRemoveWorkout) {
                CustomDialog(onDismissRequest = {
                    workoutAddViewModel.setshowRemoveWorkout(false)
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
                                workoutAddViewModel.setshowRemoveWorkout(false)
                            }, modifier = modifier
                                .fillMaxWidth()
                                .weight(1.0f)
                        )
                        Spacer(modifier = Modifier.weight(0.1f))
                        CustomButton(
                            value = "Remove", bgcolor = Green, onClick = {
                                val data = HashMap<String?, Any?>()
                                data.put("id", workout.id)
                                workoutAddViewModel.removeWorkout(data)
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
                        workoutAddViewModel.setshowAddExercise(false)
                    }, isSmall = false
                ) {
                    CustomTextField(title = "Search", value = search, onValueChange = {
                        workoutAddViewModel.setSearch(it)
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
                                item = item!!, workoutAddViewModel, modifier = modifier
                            )
                        }
                    }
                    CustomButton(
                        value = "Save", onClick = {
                            workoutAddViewModel.setshowAddExercise(false)
                        }, modifier = modifier.fillMaxWidth()
                    )
                }
            }

            if (showExerciseMove) {
                CustomDialog(
                    onDismissRequest = {
                        workoutAddViewModel.setShowExerciseMove(false)
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
                            workoutAddViewModel.setShowExerciseMove(false)
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
                        workoutAddViewModel.setTitle(it)
                    }, isvalidate = true, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Total Time", value = totalTime, onValueChange = {
                        workoutAddViewModel.setTotalTime(it)
                    }, isvalidate = true, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "User Time", value = userTime, onValueChange = {
                        workoutAddViewModel.setUserTime(it)
                    }, isvalidate = true, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Heartrate Max.", value = heartrateMax, onValueChange = {
                        workoutAddViewModel.setHeartrateMax(it)
                    }, isvalidate = true, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Heartrate Min.", value = heartrateMin, onValueChange = {
                        workoutAddViewModel.setHeartrateMin(it)
                    }, isvalidate = true, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Stress", value = stress, onValueChange = {
                        workoutAddViewModel.setStress(it)
                    }, isvalidate = true, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Spo2", value = spo2, onValueChange = {
                        workoutAddViewModel.setSpo2(it)
                    }, isvalidate = true, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Calorie", value = calorie, onValueChange = {
                        workoutAddViewModel.setCalorie(it)
                    }, isvalidate = true, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Music Url", value = musicUrl, onValueChange = {
                        workoutAddViewModel.setMusicUrl(it)
                    }, isvalidate = true, modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                CustomTextField(
                    title = "Image Url", value = imageUrl, isLast = true, onValueChange = {
                        workoutAddViewModel.setImageUrl(it)
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
                        workoutAddViewModel.validateForm()
                    }, modifier = modifier.fillMaxWidth()
                )

                Row {
                    CustomButton(
                        value = "View Exercises", bgcolor = Blue, onClick = {
                            workoutAddViewModel.setShowExerciseMove(true)
                        }, modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.1f))
                    CustomButton(
                        value = "Add Exercise", bgcolor = Green, onClick = {
                            workoutAddViewModel.setSearch("")
                            workoutAddViewModel.setshowAddExercise(true)
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
    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "${item.name}")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.rearrange_icon),
            contentDescription = "rearranege ${item.name!!}",
            modifier = modifier.height(20.dp)
        )
        Card(
            modifier = Modifier
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