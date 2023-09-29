package com.whis.ui.screen.exerciselist.ui


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.whis.Network.sealed.ValidationState
import com.whis.R
import com.whis.model.ExerciseListBean
import com.whis.routes.Screen
import com.whis.ui.customComposables.CustomButton
import com.whis.ui.customComposables.CustomDialog
import com.whis.ui.customComposables.CustomText
import com.whis.ui.customComposables.GifImage
import com.whis.ui.customComposables.MyScaffold
import com.whis.ui.customComposables.ShimmerListItem
import com.whis.ui.screen.SharedViewModel
import com.whis.ui.screen.exerciselist.viewmodel.ExerciseListViewModel
import com.whis.ui.theme.Green
import com.whis.ui.theme.Red
import com.whis.ui.theme.White

@Composable
fun ExerciseListScreen(
    modifier: Modifier = Modifier,
    exerciseListViewModel: ExerciseListViewModel = hiltViewModel(),
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val refresh by sharedViewModel.isrefresh.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        if (refresh) {
            exerciseListViewModel.getExercise()
        }
        exerciseListViewModel.apiState.collect { apiState ->
            when (apiState) {
                is ValidationState.Loading -> {
                }

                is ValidationState.Error -> {
                    snackBarHostState.showSnackbar(
                        message = apiState.errorMsg,
                        duration = SnackbarDuration.Short
                    )
                }

                is ValidationState.Ideal -> {
                }

                is ValidationState.Success -> {

                }
            }
        }
    }

    val workouts by exerciseListViewModel.exerciseList.collectAsStateWithLifecycle()
    val showListLoading by exerciseListViewModel.showLoading.collectAsStateWithLifecycle()
    val showRemoveWorkout by exerciseListViewModel.showRemoveExercise.collectAsStateWithLifecycle()

    MyScaffold(
        title = "Exercise List",
        isbackAvailable = false,
        navHostController = navHostController,
        snackBarState = snackBarHostState,
        actions = {
            IconButton(onClick = {
                sharedViewModel.setWorkout(null)
                navHostController.navigate(Screen.ExerciseEdit.route)
            }) {
                Icon(
                    Icons.Filled.Add,
                    "add_icon",
                    tint = White
                )
            }
        },
        content = { _ ->
            if (showRemoveWorkout != null) {
                CustomDialog(
                    onDismissRequest = {
                        exerciseListViewModel.setshowRemoveWorkout(null)
                    }
                ) {
                    CustomText(
                        value = "${stringResource(id = R.string.remove_btn)} ${showRemoveWorkout!!.name}",
                        isheading = true,
                        txtsize = 18.sp,
                        modifier = modifier.align(Alignment.Start)
                    )
                    CustomText(
                        value = stringResource(R.string.do_you_want_to_remove_this_exercise),
                        modifier = modifier.align(Alignment.Start)
                    )
                    Row {
                        CustomButton(
                            value = stringResource(R.string.cancel_btn),
                            bgcolor = Red,
                            onClick = {
                                exerciseListViewModel.setshowRemoveWorkout(null)
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1.0f)
                        )
                        Spacer(modifier = Modifier.weight(0.1f))
                        CustomButton(
                            value = stringResource(R.string.remove_btn),
                            bgcolor = Green, onClick = {
                                exerciseListViewModel.setshowRemoveWorkout(null)
                                val data = HashMap<String?, Any?>()
                                data.put("id", showRemoveWorkout!!.id)
                                exerciseListViewModel.removeWorkout(data)
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1.0f)
                        )
                    }
                }
            }

            ShimmerListItem(
                isLoading = showListLoading,
                contentAfterLoading = {
                    if (workouts.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                workouts,
                                key = { item -> "exercise_${item.id!!}" }) { item ->
                                ExerciseItem(
                                    modifier = modifier,
                                    item = item,
                                    onClick = {
                                        sharedViewModel.setExercise(item)
                                        navHostController.navigate(Screen.ExerciseEdit.route)
                                    }, onLongClick = {
                                        exerciseListViewModel.setshowRemoveWorkout(item)
                                    }
                                )
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GifImage(
                                imageUrl = R.drawable.no_record_icon,
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
                })
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseItem(
    item: ExerciseListBean.Data,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
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
        }
    }
}