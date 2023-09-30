package com.whis.ui.screen.exerciseadd.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.whis.ui.customComposables.CustomButton
import com.whis.ui.customComposables.CustomDialog
import com.whis.ui.customComposables.CustomText
import com.whis.ui.customComposables.CustomTextField
import com.whis.ui.customComposables.GifImage
import com.whis.ui.customComposables.LoadingDialog
import com.whis.ui.customComposables.MyScaffold
import com.whis.ui.screen.SharedViewModel
import com.whis.ui.screen.exerciseadd.viewmodel.ExerciseAddViewModel
import com.whis.ui.theme.Green
import com.whis.ui.theme.Red
import com.whis.ui.theme.White
import com.whis.utils.checkString
import kotlinx.coroutines.launch

@Composable
fun ExerciseAddScreen(
    modifier: Modifier = Modifier,
    exerciseAddViewModel: ExerciseAddViewModel = hiltViewModel(),
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        exerciseAddViewModel.setExercise(sharedViewModel.selectedExercise)

        exerciseAddViewModel.apiState.collect { apiState ->
            when (apiState) {
                is ValidationState.Loading -> {
                    if (apiState.isLoading) {
                        exerciseAddViewModel.setShowLoading(true)
                    } else {
                        exerciseAddViewModel.setShowLoading(false)
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
                    if (apiState.tag.equals("add_exercise") || apiState.tag.equals("remove_exercise")) {
                        sharedViewModel.setRefresh(true)
                        navHostController.navigateUp()
                    }
                }
            }
        }
    }
    val exercise by exerciseAddViewModel.selectedExercise.collectAsStateWithLifecycle()

    val showLoading by exerciseAddViewModel.showLoading.collectAsStateWithLifecycle()
    val name by exerciseAddViewModel.nameInputFlow.collectAsStateWithLifecycle()
    val nameError by exerciseAddViewModel.nameInputErrorFlow.collectAsStateWithLifecycle()
    val duration by exerciseAddViewModel.durationInputFlow.collectAsStateWithLifecycle()
    val durationError by exerciseAddViewModel.durationInputErrorFlow.collectAsStateWithLifecycle()
    val bodypart by exerciseAddViewModel.bodypartInputFlow.collectAsStateWithLifecycle()
    val bodypartError by exerciseAddViewModel.bodypartInputErrorFlow.collectAsStateWithLifecycle()
    val equipment by exerciseAddViewModel.equipmentInputFlow.collectAsStateWithLifecycle()
    val equipmentError by exerciseAddViewModel.equipmentInputErrorFlow.collectAsStateWithLifecycle()
    val gifurl by exerciseAddViewModel.gifurlInputFlow.collectAsStateWithLifecycle()
    val gifurlError by exerciseAddViewModel.gifurlInputErrorFlow.collectAsStateWithLifecycle()
    val reps by exerciseAddViewModel.repsInputFlow.collectAsStateWithLifecycle()
    val repsError by exerciseAddViewModel.repsInputErrorFlow.collectAsStateWithLifecycle()
    val restAfterCompletion by exerciseAddViewModel.restAfterCompletionInputFlow.collectAsStateWithLifecycle()
    val restAfterCompletionError by exerciseAddViewModel.restAfterCompletionInputErrorFlow.collectAsStateWithLifecycle()
    val rests by exerciseAddViewModel.restsInputFlow.collectAsStateWithLifecycle()
    val restsError by exerciseAddViewModel.restsInputErrorFlow.collectAsStateWithLifecycle()
    val sets by exerciseAddViewModel.setsInputFlow.collectAsStateWithLifecycle()
    val setsError by exerciseAddViewModel.setsInputErrorFlow.collectAsStateWithLifecycle()
    val target by exerciseAddViewModel.targetInputFlow.collectAsStateWithLifecycle()
    val targetError by exerciseAddViewModel.targetInputErrorFlow.collectAsStateWithLifecycle()
    val showRemoveExercise by exerciseAddViewModel.showRemoveExerciseFlow.collectAsStateWithLifecycle()

    MyScaffold(title = if (checkString(exercise.name, isempty = true).isEmpty()) {
        stringResource(R.string.add_exercise)
    } else {
        stringResource(R.string.edit_exercise)
    }, navHostController = navHostController,
        snackBarState = snackBarHostState,
        actions = {
            if (checkString(exercise.name, isempty = true).isNotEmpty()) {
                IconButton(onClick = {
                    exerciseAddViewModel.setshowRemoveExercise(true)
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

            if (showRemoveExercise) {
                CustomDialog(onDismissRequest = {
                    exerciseAddViewModel.setshowRemoveExercise(false)
                }) {
                    CustomText(
                        value = "${stringResource(R.string.remove_btn)} ${exercise.name}",
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
                            value = stringResource(R.string.cancel_btn), bgcolor = Red, onClick = {
                                exerciseAddViewModel.setshowRemoveExercise(false)
                            }, modifier = modifier
                                .fillMaxWidth()
                                .weight(1.0f)
                        )
                        Spacer(modifier = Modifier.weight(0.1f))
                        CustomButton(
                            value = stringResource(R.string.remove_btn),
                            bgcolor = Green,
                            onClick = {
                                exerciseAddViewModel.setshowRemoveExercise(false)
                                val data = HashMap<String?, Any?>()
                                data.put("id", exercise.id)
                                exerciseAddViewModel.removeExercise(data)
                            },
                            modifier = modifier
                                .fillMaxWidth()
                                .weight(1.0f)
                        )
                    }

                }
            }

            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {

                CustomTextField(
                    title = stringResource(R.string.name), value = name, iserror = nameError,
                    onValueChange = {
                        exerciseAddViewModel.setName(it)
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                )
                Row {
                    CustomTextField(
                        title = stringResource(R.string.body_part),
                        value = bodypart,
                        iserror = bodypartError,
                        isnumber = true,
                        onValueChange = {
                            exerciseAddViewModel.setBodyPart(it)
                        },
                        isvalidate = true,
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    CustomTextField(
                        title = stringResource(R.string.equipment),
                        value = equipment,
                        iserror = equipmentError,
                        isnumber = true,
                        onValueChange = {
                            exerciseAddViewModel.setEquipment(it)
                        },
                        isvalidate = true,
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }
                Row {
                    CustomTextField(
                        title = stringResource(R.string.target),
                        value = target,
                        iserror = targetError,
                        isnumber = true,
                        onValueChange = {
                            exerciseAddViewModel.setTarget(it)
                        },
                        isvalidate = true,
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    CustomTextField(
                        title = stringResource(R.string.duration),
                        value = duration,
                        iserror = durationError,
                        isnumber = true,
                        onValueChange = {
                            exerciseAddViewModel.setDuration(it)
                        },
                        isvalidate = true,
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }
                Row {
                    CustomTextField(
                        title = stringResource(R.string.sets),
                        value = sets,
                        iserror = setsError,
                        isnumber = true,
                        isvalidate = true,
                        onValueChange = {
                            exerciseAddViewModel.setSets(it)
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    CustomTextField(
                        title = stringResource(R.string.reps),
                        value = reps,
                        iserror = repsError,
                        isnumber = true,
                        isvalidate = true,
                        onValueChange = {
                            exerciseAddViewModel.setReps(it)
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }
                Row {
                    CustomTextField(
                        title = stringResource(R.string.rest),
                        value = rests,
                        iserror = restsError,
                        isnumber = true,
                        isvalidate = true,
                        onValueChange = {
                            exerciseAddViewModel.setRests(it)
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    CustomTextField(
                        title = stringResource(R.string.rest_after_completion),
                        value = restAfterCompletion,
                        iserror = restAfterCompletionError,
                        isnumber = true,
                        isvalidate = true,
                        onValueChange = {
                            exerciseAddViewModel.setRestAfterCompletion(it)
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    )
                }
                CustomTextField(
                    title = "Gif Url",
                    value = gifurl,
                    iserror = gifurlError,
                    isLast = true,
                    onValueChange = {
                        exerciseAddViewModel.setGifurl(it)
                    },
                    isvalidate = true,
                    modifier = modifier
                        .fillMaxWidth()

                )
                GifImage(
                    imageUrl = if (checkString(gifurl, isempty = true).isEmpty()) {
                        R.drawable.placeholder
                    } else {
                        gifurl
                    },
                    tint = if (checkString(gifurl, isempty = true).isEmpty()) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        null
                    },
                    modifier = modifier
                        .padding(top = 5.dp)
                        .height(100.dp)
                        .width(200.dp)
                )

                CustomButton(
                    value = if (checkString(exercise.name, isempty = true).isEmpty()) {
                        stringResource(R.string.add_exercise)
                    } else {
                        stringResource(R.string.edit_exercise)
                    }, onClick = {
                        exerciseAddViewModel.validateForm()
                    }, modifier = modifier.fillMaxWidth()
                )

            }
        })
}