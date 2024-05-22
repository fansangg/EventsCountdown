package fan.san.eventscountdown.page.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.DialogWrapper
import fan.san.eventscountdown.common.EmptyLottie
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.SpacerW
import fan.san.eventscountdown.entity.MessageEvent
import fan.san.eventscountdown.navigation.Pages
import fan.san.eventscountdown.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomePage(navController: NavController) {

    val context = LocalContext.current
    val viewModel = hiltViewModel<MainViewModel>()
    LaunchedEffect(key1 = Unit) {
        viewModel.getAllEvents()
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val lazyListState = rememberLazyListState()

    //申请权限的时间，用于触发回调做计算，回调时间 - 申请时间 < 300ms，认为是已经永久拒绝了权限
    var lastRequestPermissionTime = remember {
        0L
    }

    var isTriggershouldShowRationale = remember {
        false
    }

    val isScrollInProgress by remember {
        derivedStateOf { lazyListState.isScrollInProgress }
    }

    var showImportDialog by remember {
        mutableStateOf(false)
    }

    var showNewEventDialog by remember {
        mutableStateOf(false)
    }


    var showAddFuncDialog by remember {
        mutableStateOf(false)
    }

    var showNoPermissionDialog by remember {
        mutableStateOf(false)
    }

    val messageEvent by viewModel.messageEvent.collectAsState(initial = MessageEvent.None)

    HandlerMessage(messageEvent, snackbarHostState)

    //AnchoredDraggableState(0f)

    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_CALENDAR) {
        if (!it) {
            if (isTriggershouldShowRationale) showNoPermissionDialog = true
            if (System.currentTimeMillis() - lastRequestPermissionTime < 300) {
                showNoPermissionDialog = true
            }
        } else {
            showAddFuncDialog = false
            showNoPermissionDialog = false
            showImportDialog = true
        }
    }

    LaunchedEffect(key1 = permissionState.status) {
        if (permissionState.status.isGranted) {
            showNoPermissionDialog = false
            if (showAddFuncDialog) {
                showAddFuncDialog = false
                showImportDialog = true
            }
        }

    }

    CommonScaffold(
        title = "事件列表",
        showBack = false,
        titleDoublePress = {
            navController.navigate(Pages.Log.route)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(), verticalArrangement = Arrangement.Center
            ) {
                if (viewModel.allEventsList.isEmpty()) {
                    NoEvents(newEvent = {
                        showNewEventDialog = true
                    }) {
                        if (permissionState.status.isGranted) {
                            showAddFuncDialog = false
                            showImportDialog = true
                        } else {
                            isTriggershouldShowRationale =
                                permissionState.status.shouldShowRationale
                            permissionState.launchPermissionRequest()
                            lastRequestPermissionTime = System.currentTimeMillis()
                        }
                    }
                } else {
                    EventsList(lazyListState, viewModel.allEventsList) { event ->
                        viewModel.deleteEvent(event)
                    }
                }

                when {
                    showImportDialog -> {
                        DialogWrapper(
                            dismissOnClickOutside = true,
                            dismiss = { showImportDialog = false }) {
                            ChooseAccountDialog(accountSelected = { id ->
                                showImportDialog = false
                                viewModel.getCalendarEvents(id)
                            })
                        }
                    }

                    showNewEventDialog -> {
                        DialogWrapper(
                            dismiss = { showNewEventDialog = false },
                            usePlatformDefaultWidth = false
                        ) {
                            NewEvent(
                                cancel = { showNewEventDialog = false },
                                createEvents = { title, date, tag ->
                                    viewModel.createEvents(title, date, tag)
                                    showNewEventDialog = false
                                })
                        }
                    }

                    showAddFuncDialog -> {
                        DialogWrapper(
                            dismiss = { showAddFuncDialog = false },
                            dismissOnClickOutside = true
                        ) {
                            AddFuncDialog(add = {
                                showNewEventDialog = true
                                showAddFuncDialog = false
                            }) {
                                if (permissionState.status.isGranted) {
                                    if (viewModel.calendarAccounts.isEmpty()) {
                                        viewModel.getCalendarAccounts()
                                    }
                                    showImportDialog = true
                                    showAddFuncDialog = false
                                } else {
                                    isTriggershouldShowRationale =
                                        permissionState.status.shouldShowRationale
                                    permissionState.launchPermissionRequest()
                                    lastRequestPermissionTime = System.currentTimeMillis()
                                }
                            }
                        }
                    }
                }

                if (showNoPermissionDialog) {
                    DialogWrapper(dismiss = { showNoPermissionDialog = false }) {
                        NoPremissionDialog(cancel = { showNoPermissionDialog = false }) {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                            context.startActivity(intent)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isScrollInProgress.not() && viewModel.allEventsList.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(bottom = 18.dp, end = 24.dp)
            ) {
                FloatingActionButton(onClick = { showAddFuncDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "add")
                }
            }
        }
    }
}

@Composable
private fun NoPremissionDialog(cancel: () -> Unit, jump2Setting: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(.7f)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "无法访问您的日历账户\n请在设置里允许日历读取权限",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            SpacerH(height = 15.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(onClick = cancel) {
                    Text(text = "取消", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                }
                SpacerW(width = 12.dp)
                TextButton(onClick = jump2Setting) {
                    Text(text = "前往设置", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                }
            }
        }
    }
}

@Composable
private fun HandlerMessage(
    messageEvent: MessageEvent,
    snackbarHostState: SnackbarHostState
) {
    when (messageEvent) {
        is MessageEvent.SnackBarMessage -> {
            LaunchedEffect(key1 = messageEvent.id) {
                snackbarHostState.showSnackbar(
                    messageEvent.message
                )
            }
        }

        MessageEvent.None -> {}
    }
}

@Composable
private fun AddFuncDialog(add: () -> Unit, import: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(.76f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {

            Card(
                onClick = add,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "新建事件")
                }

            }
            SpacerH(height = 12.dp)

            Card(
                onClick = import,
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "从日历导入")
                }
            }
        }
    }
}

@Composable
private fun NoEvents(newEvent: () -> Unit, importClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyLottie(modifier = Modifier.size(150.dp))
        SpacerH(height = 25.dp)
        Row {
            Icon(imageVector = Icons.Default.Info, contentDescription = "info")
            SpacerW(width = 6.dp)
            Text(text = "没有事件", fontWeight = FontWeight.W600)
        }
        SpacerH(height = 25.dp)
        ElevatedButton(onClick = newEvent, modifier = Modifier.width(140.dp)) {
            Text(text = "新建事件", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        SpacerH(height = 12.dp)
        ElevatedButton(onClick = importClick, modifier = Modifier.width(140.dp)) {
            Text(text = "从日历导入", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }

    }
}
