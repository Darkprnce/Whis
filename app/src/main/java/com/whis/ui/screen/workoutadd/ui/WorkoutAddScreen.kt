package com.whis.ui.screen.workoutadd.ui

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.whis.Network.sealed.ValidationState
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
fun WorkoutAddScreen(
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

        workoutAddViewModel.apiState.collect { apiState ->
            when (apiState) {
                is ValidationState.Loading -> {
                    if (apiState.isLoading) {
                        workoutAddViewModel.setShowLoading(true)
                    } else {
                        workoutAddViewModel.setShowLoading(false)
                    }
                }

                is ValidationState.Error -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = apiState.errorMsg,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is ValidationState.Ideal -> {

                }

                is ValidationState.Success -> {
                    Toast.makeText(
                        context,
                        apiState.data as String,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (apiState.tag.equals("add_workout") || apiState.tag.equals("remove_workout")) {
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
    val titleError by workoutAddViewModel.titleInputErrorFlow.collectAsStateWithLifecycle()
    val totalTime by workoutAddViewModel.totalTimeInputFlow.collectAsStateWithLifecycle()
    val totalTimeError by workoutAddViewModel.totalTimeInputErrorFlow.collectAsStateWithLifecycle()
    val userTime by workoutAddViewModel.userTimeInputFlow.collectAsStateWithLifecycle()
    val userTimeError by workoutAddViewModel.userTimeInputErrorFlow.collectAsStateWithLifecycle()
    val heartrateMax by workoutAddViewModel.heartrateMaxInputFlow.collectAsStateWithLifecycle()
    val heartrateMin by workoutAddViewModel.heartrateMinInputFlow.collectAsStateWithLifecycle()
    val stress by workoutAddViewModel.stressInputFlow.collectAsStateWithLifecycle()
    val spo2 by workoutAddViewModel.spo2InputFlow.collectAsStateWithLifecycle()
    val calorie by workoutAddViewModel.calorieInputFlow.collectAsStateWithLifecycle()
    val musicUrl by workoutAddViewModel.musicurlInputFlow.collectAsStateWithLifecycle()
    val imageUrl by workoutAddViewModel.imageurlInputFlow.collectAsStateWithLifecycle()
    val imageUrlError by workoutAddViewModel.imageurlInputErrorFlow.collectAsStateWithLifecycle()
    val search by workoutAddViewModel.searchInputFlow.collectAsStateWithLifecycle()
    val showRemoveWorkout by workoutAddViewModel.showRemoveWorkout.collectAsStateWithLifecycle()
    val showAddExercise by workoutAddViewModel.showAddExercise.collectAsStateWithLifecycle()
    val showExerciseMove by workoutAddViewModel.showExerciseMove.collectAsStateWithLifecycle()
    val exersisesSearch by workoutAddViewModel.exercisesSearch.collectAsStateWithLifecycle()
    val workoutExercises by workoutAddViewModel.workoutExercises.collectAsStateWithLifecycle()


    MyScaffold(title = if (checkString(workout.title, isempty = true).isEmpty()) {
        stringResource(R.string.add_workout)
    } else {
        stringResource(R.string.edit_workout)
    }, navHostController = navHostController,
        snackBarState = snackBarHostState,
        actions = {
            if (checkString(workout.title, isempty = true).isNotEmpty()) {
                IconButton(onClick = {
                    workoutAddViewModel.setshowRemoveWorkout(true)
                }) {
                    Icon(
                        Icons.Filled.Delete, stringResource(R.string.remove_icon), tint = White
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
                        value = "${stringResource(R.string.remove_btn)} ${workout.title}",
                        isheading = true,
                        txtsize = 18.sp,
                        modifier = modifier.align(Alignment.Start)
                    )
                    CustomText(
                        value = stringResource(R.string.do_you_want_to_remove_this_workout),
                        modifier = modifier.align(Alignment.Start)
                    )
                    Row {
                        CustomButton(
                            value = stringResource(R.string.cancel_btn), bgcolor = Red, onClick = {
                                workoutAddViewModel.setshowRemoveWorkout(false)
                            }, modifier = modifier
                                .fillMaxWidth()
                                .weight(1.0f)
                        )
                        Spacer(modifier = Modifier.weight(0.1f))
                        CustomButton(
                            value = stringResource(R.string.remove_btn),
                            bgcolor = Green,
                            onClick = {
                                workoutAddViewModel.setshowRemoveWorkout(false)
                                val data = HashMap<String?, Any?>()
                                data.put("id", workout.id)
                                workoutAddViewModel.removeWorkout(data)
                            },
                            modifier = modifier
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
                    CustomTextField(
                        title = stringResource(R.string.search),
                        value = search,
                        onValueChange = {
                            workoutAddViewModel.setSearch(it)
                        },
                        starticon = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = stringResource(R.string.search),
                                )
                            }
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp)
                    )
                    LazyColumn(
                        modifier = modifier.height(430.dp)
                    ) {
                        items(exersisesSearch, key = { item -> "search_${item!!.id!!}" }) { item ->
                            ExerciseSearchItem(
                                item = item!!, workoutAddViewModel, modifier = modifier
                            )
                        }
                    }
                    CustomButton(
                        value = stringResource(R.string.save_btn), onClick = {
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
                        value = stringResource(R.string.exercises),
                        isheading = true,
                        txtsize = 18.sp,
                        modifier = modifier.align(Alignment.Start)
                    )
                    if (workoutExercises.isNotEmpty()) {
                        LazyColumn(
                            state = state.listState,
                            modifier = modifier
                                .height(500.dp)
                                .reorderable(state)
                                .detectReorderAfterLongPress(state)
                        ) {
                            items(workoutExercises, key = { item -> "move ${item.id!!}" }) { item ->
                                ReorderableItem(state, key = item.id!!) { isDragging ->
                                    ExerciseMoveItem(
                                        item = item,
                                        viewModel = workoutAddViewModel,
                                        isDragging = isDragging,
                                        modifier = modifier
                                    )
                                }
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GifImage(
                                imageUrl = R.drawable.no_record_icon,
                                placeholder = R.drawable.loading_small,
                                modifier = modifier.height(250.dp)
                            )
                            CustomText(
                                value = stringResource(R.string.no_exercises),
                                isheading = true,
                                txtsize = 18.sp,
                                modifier = modifier.padding(top = 10.dp)
                            )
                        }
                    }
                    CustomButton(
                        value = stringResource(R.string.save_btn), onClick = {
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
                    title = stringResource(R.string.title), value = title, iserror = titleError,
                    onValueChange = {
                        workoutAddViewModel.setTitle(it)
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                )
                Row {
                    CustomTextField(
                        title = stringResource(R.string.total_time),
                        value = totalTime,
                        iserror = totalTimeError,
                        isnumber = true,
                        onValueChange = {
                            workoutAddViewModel.setTotalTime(it)
                        },
                        isvalidate = true,
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    CustomTextField(
                        title = stringResource(R.string.user_time),
                        value = userTime,
                        iserror = userTimeError,
                        isnumber = true,
                        onValueChange = {
                            workoutAddViewModel.setUserTime(it)
                        },
                        isvalidate = true,
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }
                Row {
                    CustomTextField(
                        title = stringResource(R.string.heartrate_max),
                        value = heartrateMax,
                        isnumber = true,
                        onValueChange = {
                            workoutAddViewModel.setHeartrateMax(it)
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    CustomTextField(
                        title = stringResource(R.string.heartrate_min),
                        value = heartrateMin,
                        isnumber = true,
                        onValueChange = {
                            workoutAddViewModel.setHeartrateMin(it)
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }
                Row {
                    CustomTextField(
                        title = stringResource(R.string.stress),
                        value = stress,
                        isnumber = true,
                        onValueChange = {
                            workoutAddViewModel.setStress(it)
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    CustomTextField(
                        title = stringResource(R.string.spo2),
                        value = spo2,
                        isnumber = true,
                        onValueChange = {
                            workoutAddViewModel.setSpo2(it)
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }
                CustomTextField(
                    title = stringResource(R.string.calorie),
                    value = calorie,
                    isnumber = true,
                    onValueChange = {
                        workoutAddViewModel.setCalorie(it)
                    },
                    modifier = modifier
                        .fillMaxWidth()

                )
                CustomTextField(
                    title = stringResource(R.string.music_url), value = musicUrl, onValueChange = {
                        workoutAddViewModel.setMusicUrl(it)
                    }, modifier = modifier
                        .fillMaxWidth()

                )
                CustomTextField(
                    title = stringResource(R.string.image_url),
                    value = imageUrl,
                    iserror = imageUrlError,
                    isLast = true,
                    onValueChange = {
                        workoutAddViewModel.setImageUrl(it)
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()

                )
                GifImage(
                    imageUrl = if (checkString(imageUrl, isempty = true).isEmpty()) {
                        R.drawable.placeholder
                    } else {
                        imageUrl
                    },
                    tint = if (checkString(imageUrl, isempty = true).isEmpty()) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        null
                    },
                    modifier = modifier
                        .padding(top = 5.dp)
                        .height(100.dp)
                        .width(200.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    CustomText(
                        value = "Exercises (${workoutExercises.size})",
                        isheading = true,
                        txtsize = 16.sp
                    )
                    Row {
                        IconButton(
                            onClick = { workoutAddViewModel.setShowExerciseMove(true) },
                            modifier = Modifier
                        ) {
                            GifImage(
                                R.drawable.file_icon,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        IconButton(
                            onClick = {
                                workoutAddViewModel.setSearch("")
                                workoutAddViewModel.setshowAddExercise(true)
                            },
                            modifier = Modifier
                        ) {
                            GifImage(
                                R.drawable.add_icon,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }


                CustomButton(
                    value = if (checkString(workout.title, isempty = true).isEmpty()) {
                        stringResource(R.string.add_workout)
                    } else {
                        stringResource(R.string.edit_workout)
                    }, onClick = {
                        workoutAddViewModel.validateForm()
                    }, modifier = modifier.fillMaxWidth()
                )

            }
        })

}

@Composable
fun ExerciseMoveItem(
    item: ExerciseListBean.Data,
    viewModel: WorkoutAddViewModel,
    isDragging: Boolean,
    modifier: Modifier
) {
    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "${item.name}")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.rearrange_icon),
            contentDescription = "rearranege ${item.name!!}",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
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
                    imageUrl = item.gifurl!!,
                    modifier = modifier
                        .height(80.dp)
                        .width(80.dp)
                )
                CustomText(
                    item.name!!, modifier = modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .weight(1f)
                )
                IconButton(
                    onClick = { viewModel.addExercise(item) },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = Color.Red
                    )
                }

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
                imageUrl = item.gifurl!!,
                modifier = modifier
                    .height(50.dp)
                    .width(50.dp)
            )
            CustomText(
                item.name!!, modifier = modifier
                    .padding(10.dp)
            )
        }
    }
}