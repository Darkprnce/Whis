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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.whis.ui.customComposables.CustomTextField
import com.whis.ui.customComposables.GifImage
import com.whis.ui.customComposables.MyToolbar
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
    snackBarState: SnackbarHostState,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val refresh by sharedViewModel.isRefresh.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        if (refresh) {
            exerciseListViewModel.getExercise()
        }
        exerciseListViewModel.apiState.collect { apiState ->
            when (apiState) {
                is ValidationState.Loading -> {
                }

                is ValidationState.Error -> {
                    snackBarState.showSnackbar(
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

    val exercises by exerciseListViewModel.exercisesSearch.collectAsStateWithLifecycle()
    val showListLoading by exerciseListViewModel.showLoading.collectAsStateWithLifecycle()
    val showRemoveWorkout by exerciseListViewModel.showRemoveExercise.collectAsStateWithLifecycle()
    val search by exerciseListViewModel.searchInputFlow.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
    ) {
        MyToolbar(
            title = "Exercise List",
            isbackAvailable = false,
            navHostController = navHostController,
            actions = {
                IconButton(onClick = {
                    sharedViewModel.setExercise(null)
                    navHostController.navigate(Screen.ExerciseEdit.route)
                }) {
                    Icon(
                        Icons.Filled.Add,
                        "add_icon",
                        tint = White
                    )
                }
            },
        )

        if (showRemoveWorkout != null) {
            CustomDialog(
                onDismissRequest = {
                    exerciseListViewModel.setshowRemoveExercise(null)
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
                            exerciseListViewModel.setshowRemoveExercise(null)
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.1f))
                    CustomButton(
                        value = stringResource(R.string.remove_btn),
                        bgcolor = Green, onClick = {
                            exerciseListViewModel.setshowRemoveExercise(null)
                            val data = HashMap<String?, Any?>()
                            data["id"] = showRemoveWorkout!!.id
                            exerciseListViewModel.removeWorkout(data)
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }
            }
        }

        CustomTextField(
            title = stringResource(R.string.search),
            value = search,
            onValueChange = {
                exerciseListViewModel.setSearch(it)
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
                .padding(top = 5.dp, start = 5.dp, end = 5.dp)
        )

        ShimmerListItem(
            isLoading = showListLoading,
            contentAfterLoading = {
                if (exercises.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            exercises,
                            key = { item -> "exercise_${item!!.id!!}" }) { item ->
                            ExerciseItem(
                                modifier = modifier,
                                item = item!!,
                                onClick = {
                                    sharedViewModel.setExercise(item)
                                    navHostController.navigate(Screen.ExerciseEdit.route)
                                }, onLongClick = {
                                    exerciseListViewModel.setshowRemoveExercise(item)
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
    }
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
                item.name!!, color = if (item.isshow.equals("false")) {
                    Red
                } else {
                    null
                }, modifier = modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .weight(1f)
            )
        }
    }
}