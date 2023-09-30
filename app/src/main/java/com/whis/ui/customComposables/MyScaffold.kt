package com.whis.ui.customComposables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.whis.Network.ConnectionUtil.connectivityState
import com.whis.Network.sealed.ConnectionState
import com.whis.model.BottomNavigationItem
import com.whis.ui.theme.White
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn( ExperimentalCoroutinesApi::class)
@Composable
fun MyScaffold(
    modifier: Modifier = Modifier,
    showConnectivity:Boolean = true,
    navHostController: NavHostController,
    snackBarState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable (SnackbarHostState) -> Unit,
    bottomBar: @Composable (() -> Unit)? = null,
) {
    //val snackBarHostState = remember { SnackbarHostState() }
    val connection by connectivityState()
    val isConnected = connection === ConnectionState.Available

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .imePadding()
            ) {
                if(showConnectivity){
                    ConnectivityStatus(isConnected)
                }
                content(snackBarState)
            }
        }, bottomBar = { bottomBar?.invoke() })
}
