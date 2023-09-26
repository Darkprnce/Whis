package com.whis.ui.customComposables

import android.os.Build.VERSION.SDK_INT
import android.text.TextUtils
import android.view.KeyEvent.ACTION_DOWN
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.whis.ui.theme.White
import timber.log.Timber

@Composable
fun CustomText(
    value: String,
    modifier: Modifier = Modifier,
    isbold: Boolean = false,
    isheading: Boolean = false,
    color: Color? = null,
    txtsize: TextUnit = 14.sp,
    txtStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        modifier = modifier,
        text = value,
        style = if (isheading) {
            MaterialTheme.typography.headlineMedium
        } else {
            txtStyle
        },
        fontWeight = if (isbold) {
            FontWeight.Bold
        } else {
            FontWeight.Normal
        },
        color = color
            ?: if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        fontSize = txtsize
    )
}

@Composable
fun CustomButton(
    value: String,
    modifier: Modifier = Modifier,
    bgcolor: Color? = null,
    txtcolor: Color = White,
    txtsize: TextUnit = 14.sp,
    bordercolor: Color? = null,
    borderwidth: Dp = 1.dp,
    isoutline: Boolean = false,
    onClick: () -> Unit,
) {
    if (isoutline) {
        OutlinedButton(
            onClick = onClick, colors = ButtonDefaults.buttonColors(
                containerColor = bgcolor ?: MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(borderwidth, bordercolor ?: MaterialTheme.colorScheme.primary),
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            CustomText(value, color = txtcolor, txtsize = txtsize)
        }
    } else {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = bgcolor ?: MaterialTheme.colorScheme.primary
            ),
            modifier = modifier
                .padding(top = 10.dp)
        ) {
            CustomText(value, color = txtcolor, txtsize = txtsize)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier, title: String, value: String = "",
    onValueChange: (String) -> Unit,
    isenabled: Boolean = true,
    isvalidate: Boolean = false,
    isemail: Boolean = false,
    ismobile: Boolean = false,
    ispincode: Boolean = false,
    isnumber: Boolean = false,
    iscapital: Boolean = false,
    isLast: Boolean = false,
    iserror: Boolean = false,
    supportingText: String = "",
    starticon: @Composable (() -> Unit)? = null,
    endicon: @Composable (() -> Unit)? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isFocused by remember { mutableStateOf(false) }
    var isvalid by remember { mutableStateOf(true) }
    var isemailValid by remember { mutableStateOf(true) }
    var ismobileValid by remember { mutableStateOf(true) }
    var ispincodeValid by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    isError = if(iserror){
        true
    }else{
        if (isvalidate) {
            !isvalid
        } else if (isemail) {
            !isemailValid
        } else if (ismobile) {
            !ismobileValid
        } else if (ispincode) {
            !ispincodeValid
        } else {
            false
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            if (isvalidate) {
                isvalid = !TextUtils.isEmpty(it)
            }
            if (isemail) {
                isemailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
            }
            if (ismobile) {
                ismobileValid = it.length >= 10
            }
            if (ispincode) {
                ispincodeValid = it.length >= 6
            }
        },
        enabled = isenabled,
        leadingIcon = starticon,
        trailingIcon = {
            if (isError) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Error",
                        tint = Color.Red
                    )
                }
            } else {
                endicon?.invoke()
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        supportingText = {
            if (isError) {
                CustomText(
                    "Please enter a valid $title",
                    color = Color.Red,
                    txtsize = 9.sp,
                )
            } else {
                if (supportingText.isNotEmpty()) {
                    CustomText(
                        supportingText,
                        txtsize = 9.sp,
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(1.dp)
           /* .focusRequester(
                 focusRequester =
                 (if (isFocused) {
                     rememberUpdatedState(isFocused)
                 } else {
                     FocusRequester()
                 }) as FocusRequester
             )*/
            .onPreviewKeyEvent {
                if (it.key == Key.Tab && it.nativeKeyEvent.action == ACTION_DOWN) {
                    if (isLast) {
                        keyboardController!!.hide()
                        focusManager.clearFocus()
                    } else {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                    true
                } else {
                    false
                }
            },
        label = {
            CustomText(
                title,
                txtsize = 12.sp,
                color =
                if (isError) {
                    Color.Red
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        isError = isError,
        keyboardActions = KeyboardActions(
            onNext = {
                if (isLast) {
                    keyboardController!!.hide()
                    focusManager.clearFocus()
                } else {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization =
            if (iscapital) {
                KeyboardCapitalization.Characters
            } else {
                KeyboardCapitalization.Sentences
            },
            imeAction =
            if (isLast) {
                ImeAction.Done
            } else {
                ImeAction.Next
            },
            keyboardType = if (isemail) {
                KeyboardType.Email
            } else if (ismobile) {
                KeyboardType.Phone
            } else if (ispincode) {
                KeyboardType.Number
            } else if (isnumber) {
                KeyboardType.Number
            } else {
                KeyboardType.Text
            },
        )
    )
}