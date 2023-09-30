package com.whis.ui.customComposables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.whis.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyToolbar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navHostController: NavHostController,
    isbackAvailable: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
) {
    if(title !=null || isbackAvailable || actions !=null){
        TopAppBar(
            title = {
                if(title !=null){
                    CustomText(title, isbold = true, color = White, txtsize = 16.sp)
                }
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
    }

}