package com.whis.ui.customComposables

import android.view.Gravity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    isSmall: Boolean = true,
    content: @Composable (ColumnScope.() -> Unit)
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