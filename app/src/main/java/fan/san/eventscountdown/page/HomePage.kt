package fan.san.eventscountdown.page

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.common.getWeekDay
import fan.san.eventscountdown.common.toLunr
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.entity.CalendarAccountBean
import fan.san.eventscountdown.entity.MessageEvent
import fan.san.eventscountdown.utils.CommonUtil
import fan.san.eventscountdown.viewmodel.MainViewModel
import kotlinx.coroutines.delay

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

    val alwaysDenial = remember {
        mutableStateOf(false)
    }

    var showImportDialog by remember {
        mutableStateOf(false)
    }

    val messageEvent by viewModel.messageEvent.collectAsState(initial = MessageEvent.None)

    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_CALENDAR) {
        if (!it) {
            if (viewModel.isTriggershouldShowRationale) alwaysDenial.value = true
            if (System.currentTimeMillis() - viewModel.lastRequestPermissionTime < 500)
                alwaysDenial.value = true
        }
    }

    LaunchedEffect(key1 = Unit) {
        delay(300)
        permissionState.launchPermissionRequest()
        viewModel.lastRequestPermissionTime = System.currentTimeMillis()
    }

    CommonScaffold(
        title = "日历事件",
        showBack = false,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        actionClick = { showImportDialog = true },
        actionIcon = Icons.Default.Add,
        showAction = viewModel.allEventsList.isNotEmpty()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), verticalArrangement = Arrangement.Center
        ) {
            if (permissionState.status.isGranted) {
                LaunchedEffect(key1 = Unit) {
                    viewModel.getCalendarAccounts()
                }
                if (viewModel.allEventsList.isEmpty()) {
                    NoEvents {
                        showImportDialog = true
                    }
                } else {
                    EventsList(viewModel)
                }

                if (showImportDialog) {
                    DialogWrapper(
                        dismissOnClickOutside = true,
                        dismiss = { showImportDialog = false }) {
                        ChooseAccountDialog(viewModel, accountSelected = { id ->
                            showImportDialog = false
                            viewModel.getCalendarEvents(id)
                        })
                    }
                }
            } else {
                if (permissionState.status.shouldShowRationale)
                    viewModel.isTriggershouldShowRationale = true

                NoPermissionPage(alwaysDiandel = alwaysDenial) {
                    if (alwaysDenial.value) {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    } else {
                        permissionState.launchPermissionRequest()
                        viewModel.lastRequestPermissionTime = System.currentTimeMillis()
                    }
                }
            }

            when (messageEvent) {
                is MessageEvent.SnackBarMessage -> {
                    LaunchedEffect(key1 = (messageEvent as MessageEvent.SnackBarMessage).id) {
                        snackbarHostState.showSnackbar(
                            (messageEvent as MessageEvent.SnackBarMessage).message
                        )
                    }
                }

                MessageEvent.None -> {}
            }

        }
    }
}

@Composable
private fun NoPermissionPage(alwaysDiandel: MutableState<Boolean>, requestPermisison: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = if (alwaysDiandel.value) "请在设置里允许日历读取权限" else "点击来授予日历权限",
            modifier = Modifier.clickable(onClick = requestPermisison)
        )
    }
}

@Composable
private fun ChooseAccountDialog(viewModel: MainViewModel, accountSelected: (Long) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(.78f)
            .height(320.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "选择一个日历账户来导入事件", fontSize = 18.sp)
            12.SpacerH()
            if (viewModel.calendarAccounts.isEmpty()) {
                NoAccount()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.calendarAccounts) {
                        AccountItem(bean = it) {
                            accountSelected.invoke(it.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountItem(bean: CalendarAccountBean, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(color = Color(bean.color))
            )

            Text(
                text = bean.displayName,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NoAccount() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyLottie(modifier = Modifier.size(150.dp))
        12.SpacerH()
        Row {
            Icon(imageVector = Icons.Default.Info, contentDescription = "info")
            6.SpacerW()
            Text(text = "没有日历账户")
        }

    }
}

@Composable
private fun NoEvents(importClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyLottie(modifier = Modifier.size(150.dp))
        25.SpacerH()
        Row {
            Icon(imageVector = Icons.Default.Info, contentDescription = "info")
            6.SpacerW()
            Text(text = "没有事件", fontWeight = FontWeight.W600)
        }
        25.SpacerH()
        ElevatedButton(onClick = importClick) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "import")
            8.SpacerW()
            Text(text = "从日历中导入")
        }
    }
}

@Composable
private fun EventsList(viewModel: MainViewModel) {
    val state = rememberLazyListState()
    val pastEvents = viewModel.allEventsList.filter {
        System.currentTimeMillis() > it.startDateTime
    }

    val todayEvents = viewModel.allEventsList.filter {
        System.currentTimeMillis().todayZeroTime == it.startDateTime
    }

    val futureEvents = viewModel.allEventsList.filter {
        System.currentTimeMillis() < it.startDateTime
    }

    LaunchedEffect(key1 = Unit) {
        state.animateScrollToItem(pastEvents.size)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        state = state
    ) {
        items(pastEvents) {
            EventItem(it)
        }

        if (todayEvents.isNotEmpty())
            item {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 8.dp)
                    ) {
                        Text(text = "今天", fontSize = 18.sp)
                    }
                    12.SpacerH()
                    Column {
                        todayEvents.forEach {
                            EventItem(events = it)
                            12.SpacerH()
                        }
                    }
                }

            }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Text(text = "接下来", fontSize = 18.sp)
            }
        }
        items(futureEvents) {
            EventItem(it)
        }
    }
}

@Composable
private fun EventItem(events: Events) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = events.startDateTime.formatMd,
                        fontSize = 24.sp,
                        modifier = Modifier.alignByBaseline(),
                    )
                    6.SpacerW()
                    Text(
                        text = events.startDateTime.getWeekDay,
                        fontSize = 16.sp,
                        modifier = Modifier.alignByBaseline()
                    )
                }
                2.SpacerH()
                Text(text = "农历 ${events.startDateTime.toLunr}", fontSize = 15.sp)

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = events.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.alignByBaseline()
                )

                if (events.startDateTime > System.currentTimeMillis()) {
                    Text(text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 14.sp,
                                baselineShift = BaselineShift.None
                            )
                        ) {
                            append("还剩 ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 38.sp,
                                fontWeight = FontWeight.W700,
                                baselineShift = BaselineShift(-0.06f)
                            )
                        ) {
                            append(CommonUtil.getDaysDiff(events.startDateTime))
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 14.sp,
                                baselineShift = BaselineShift.None
                            )
                        ) {
                            append(" 天")
                        }
                    }, modifier = Modifier.alignByBaseline())
                } else {
                    if (System.currentTimeMillis().todayZeroTime == events.startDateTime) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        Text(text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.W700,
                                    baselineShift = BaselineShift(-0.06f)
                                )
                            ) {
                                append(CommonUtil.getDaysDiff(events.startDateTime))
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 14.sp,
                                    baselineShift = BaselineShift.None
                                )
                            ) {
                                append(" 天前")
                            }
                        }, modifier = Modifier.alignByBaseline())
                    }
                }
            }
        }

    }
}
