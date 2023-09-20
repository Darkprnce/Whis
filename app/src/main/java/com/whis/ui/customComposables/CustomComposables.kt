package com.whis.ui.customComposables

import android.os.Build.VERSION.SDK_INT
import android.text.TextUtils
import android.view.Gravity
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
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.whis.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    title: String,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    isbackAvailable: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable() (RowScope.() -> Unit)? = null,
    content: @Composable (SnackbarHostState) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    CustomText(title, isbold = true, color = White, txtsize = 16.sp)
                },
                navigationIcon = {
                    if (isbackAvailable) {
                        IconButton(onClick = {
                            if (onBackClick != null) {
                                onBackClick()
                            } else {
                                navHostController.popBackStack()
                            }
                        }) {
                            Icon(
                                Icons.Filled.KeyboardArrowLeft,
                                "back_icon",
                                tint = White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    if (actions != null) {
                        actions()
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .imePadding()
            ) {
                content(snackBarHostState)
            }
        })
}

@Composable
fun CustomText(
    value: String,
    modifier: Modifier = Modifier,
    isbold: Boolean = false,
    isheading: Boolean = false,
    color: Color? = null,
    txtsize: TextUnit = 14.sp,
) {
    Text(
        modifier = modifier,
        text = value,
        style = if (isheading) {
            MaterialTheme.typography.headlineMedium
        } else {
            MaterialTheme.typography.bodyMedium
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
    iscapital: Boolean = false,
    starticon: @Composable() (() -> Unit)? = null,
    endicon: @Composable() (() -> Unit)? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isFocused by remember { mutableStateOf(false) }
    var isvalid by remember { mutableStateOf(true) }
    var isemailValid by remember { mutableStateOf(true) }
    var ismobileValid by remember { mutableStateOf(true) }
    var ispincodeValid by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
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
                    endicon
                }
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = modifier
                .fillMaxWidth()
//            .border(
//                width = 1.dp,
//                color = if (isError) Color.Red else Color.Gray, // Customize error color
//                shape = RoundedCornerShape(4.dp)
//            )
                .padding(1.dp)
                .focusRequester(
                    focusRequester =
                    (if (isFocused) {
                        rememberUpdatedState(isFocused)
                    } else {
                        FocusRequester()
                    }) as FocusRequester
                ).onPreviewKeyEvent {
                    if (it.key == Key.Tab && it.nativeKeyEvent.action == ACTION_DOWN){
                        focusManager.moveFocus(FocusDirection.Down)
                        true
                    } else {
                        false
                    }
                },
            label = {
                CustomText(
                    title, color =
                    if (isError) {
                        Color.Red
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            isError =
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
            },
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization =
                if (iscapital) {
                    KeyboardCapitalization.Characters
                } else {
                    KeyboardCapitalization.Sentences
                },
                imeAction = ImeAction.Next,
                keyboardType = if (isemail) {
                    KeyboardType.Email
                } else if (ismobile) {
                    KeyboardType.Phone
                } else if (ispincode) {
                    KeyboardType.Number
                } else {
                    KeyboardType.Text
                },
                //imeAction = ImeAction.Done
            ),
        )

        isError = if (isvalidate) {
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

        if (isError) {
            CustomText(
                "Please enter a valid $title",
                color = Color.Red,
                txtsize = 10.sp,
                modifier = modifier.padding(start = 8.dp)
            )
        }
    }
    // return isError
}

@Composable
fun GifImage(
    image_url: Any,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.respectCacheHeaders(false)
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = image_url).apply(block = {
                size(Size.ORIGINAL)
            }).build(), imageLoader = imageLoader
        ),
        contentScale = contentScale,
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
    )
}


@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    isSmall: Boolean = true,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            decorFitsSystemWindows = true,
            usePlatformDefaultWidth = false
        )
    ) {
        var modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
        if (!isSmall) {
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .height(600.dp)
        }
        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.BOTTOM)
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}

@Composable
fun ShimmerListItem(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        LazyColumn(
            modifier = Modifier
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val list = (0..8).map { it.toString() }
            items(count = list.size) {
                Row(modifier = modifier.padding(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(shape = RoundedCornerShape(15.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(20.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    } else {
        contentAfterLoading()
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}