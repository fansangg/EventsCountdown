package fan.san.eventscountdown.page

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.DialogWrapper
import fan.san.eventscountdown.common.EmptyLottie
import fan.san.eventscountdown.common.HDivider
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.SpacerW
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.common.getWeekDay
import fan.san.eventscountdown.common.toLunar
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.entity.CalendarAccountBean
import fan.san.eventscountdown.entity.EventsTagsBean
import fan.san.eventscountdown.entity.MessageEvent
import fan.san.eventscountdown.navigation.Pages
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

    val lazyListState = rememberLazyListState()

    val isScrollInProgress by remember {
        derivedStateOf { lazyListState.isScrollInProgress }
    }

    val alwaysDenial = remember {
        mutableStateOf(false)
    }

    var showImportDialog by remember {
        mutableStateOf(false)
    }

    var showNewEventDialog by remember {
        mutableStateOf(false)
    }


    var showAddDialog by remember {
        mutableStateOf(false)
    }

    val messageEvent by viewModel.messageEvent.collectAsState(initial = MessageEvent.None)

    HandlerMessage(messageEvent, snackbarHostState)

    //AnchoredDraggableState(0f)

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
        title = "事件列表",
        showBack = false,
        titleDoublePress = {
            navController.navigate(Pages.Log.route)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(it)){
            Column(
                modifier = Modifier
                    .fillMaxSize(), verticalArrangement = Arrangement.Center
            ) {
                if (permissionState.status.isGranted) {
                    LaunchedEffect(key1 = Unit) {
                        viewModel.getCalendarAccounts()
                    }
                    if (viewModel.allEventsList.isEmpty()) {
                        NoEvents(newEvent = {
                            showNewEventDialog = true
                        }) {
                            showImportDialog = true
                        }
                    } else {
                        EventsList(lazyListState, viewModel.allEventsList)
                    }

                    if (showImportDialog) {
                        DialogWrapper(
                            dismissOnClickOutside = true,
                            dismiss = { showImportDialog = false }) {
                            ChooseAccountDialog(viewModel.calendarAccounts, getAccoounts = {
                                viewModel.getCalendarAccounts()
                            }, accountSelected = { id ->
                                showImportDialog = false
                                viewModel.getCalendarEvents(id)
                            })
                        }
                    }
                    if (showNewEventDialog) {
                        DialogWrapper(
                            dismissOnClickOutside = true,
                            dismiss = { showNewEventDialog = false }, usePlatformDefaultWidth = false) {
                            NewEvent(cancel = {showNewEventDialog = false}, createEvents = {title, date, tag ->
                                viewModel.createEvents(title, date, tag)
                                showNewEventDialog = false
                                showAddDialog = false
                            })
                        }
                    }

                    if (showAddDialog) {
                        DialogWrapper(dismiss = { showAddDialog = false }, dismissOnClickOutside = true) {
                            AddDialog(add = { showNewEventDialog = true }) {
                                showImportDialog = true
                            }
                        }
                    }

                } else {
                    if (permissionState.status.shouldShowRationale)
                        viewModel.isTriggershouldShowRationale = true

                    NoPermissionPage(alwaysDiandel = alwaysDenial.value) {
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
            }

            AnimatedVisibility(
                visible = isScrollInProgress.not() && viewModel.allEventsList.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(alignment = Alignment.BottomEnd).padding(bottom = 18.dp, end = 24.dp)
            ) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "add")
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
private fun AddDialog(add: () -> Unit, import: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(.76f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp)
        ) {

            Card(
                onClick = add,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(text = "从日历导入")
                }
            }
        }
    }
}

@Composable
private fun NoPermissionPage(alwaysDiandel: Boolean, requestPermisison: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = if (alwaysDiandel) "请在设置里允许日历读取权限" else "点击来授予日历权限",
            modifier = Modifier.clickable(onClick = requestPermisison)
        )
    }
}

@Composable
private fun ChooseAccountDialog(
    calendarAccountsList: List<CalendarAccountBean>,
    getAccoounts: () -> Unit,
    accountSelected: (Long) -> Unit
) {
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
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                val (titile, refresh) = createRefs()

                Text(
                    text = "选择日历账户",
                    fontSize = 18.sp,
                    modifier = Modifier.constrainAs(titile) {
                        centerTo(parent)
                    })

                RefreshAccount(modifier = Modifier.constrainAs(refresh) {
                    centerVerticallyTo(titile)
                    start.linkTo(titile.end)
                }, getAccoounts)
            }

            if (calendarAccountsList.isEmpty()) {
                NoAccount(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(calendarAccountsList) {
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
private fun RefreshAccount(modifier: Modifier, getAccoounts: () -> Unit) {
    var rotation by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    IconButton(onClick = {
        rotation += 360f
        getAccoounts.invoke()
    }, modifier = modifier) {
        Icon(imageVector = Icons.Default.Refresh,
            contentDescription = "refreshBtn",
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer {
                    rotationZ = animatedRotation
                }
        )
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
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun NoAccount(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyLottie(modifier = Modifier.size(150.dp))
        SpacerH(height = 12.dp)
        Row {
            Icon(imageVector = Icons.Default.Info, contentDescription = "info")
            SpacerW(width = 6.dp)
            Text(text = "没有日历账户")
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

@Composable
private fun EventsList(state: LazyListState, allEventsList: List<Events>) {
    val pastEvents = allEventsList.filter {
        System.currentTimeMillis().todayZeroTime > it.startDateTime
    }

    val todayEvents = allEventsList.filter {
        System.currentTimeMillis().todayZeroTime == it.startDateTime
    }

    val futureEvents = allEventsList.filter {
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
                            .padding(start = 8.dp)
                    ) {
                        Text(text = "今天", fontSize = 18.sp)
                    }
                    SpacerH(height = 12.dp)
                    Column {
                        todayEvents.forEach {
                            EventItem(events = it)
                            SpacerH(height = 12.dp)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NewEvent(cancel: () -> Unit,createEvents:(title:String,date:Long,tag:String) -> Unit) {
    val viewModel = hiltViewModel<MainViewModel>()
    var eventTitle by remember {
        mutableStateOf("")
    }
    var isError by remember {
        mutableStateOf(false)
    }

    var showDatePickerDialog by remember {
        mutableStateOf(false)
    }

    var date by remember {
        mutableLongStateOf(0L)
    }

    val tagsList = remember {
        mutableStateListOf(EventsTagsBean.getNewTag())
    }

    LaunchedEffect(key1 = Unit) {
        tagsList.addAll(
            viewModel.getAllTags()
                .map { EventsTagsBean(contet = it, isNew = false, isSelected = false) })
    }

    SideEffect {
        Log.d("fansangg", "#NewEvent: date == $date")
    }

    var createTag by remember {
        mutableStateOf(false)
    }

    val canCreate by remember {
        derivedStateOf {
            tagsList.any { it.isSelected } && eventTitle.isNotEmpty() && date > 0
        }
    }

    val scrollState = rememberScrollState()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(.82f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp), contentAlignment = Alignment.Center
            ) {

                Text(text = "新建事件", fontSize = 16.sp, fontWeight = FontWeight.W600)
            }

            SpacerH(height = 8.dp)

            OutlinedTextField(
                value = eventTitle,
                onValueChange = {
                    eventTitle = it
                    isError = it.length > 12
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "请输入事件标题", color = Color.White.copy(alpha = .4f))
                },
                isError = isError,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                singleLine = true,
                supportingText = {
                    Text(
                        text = if (isError) "${eventTitle.length}/12" else "",
                        fontSize = 8.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors()
                    .copy(errorContainerColor = MaterialTheme.colorScheme.errorContainer)
            )

            SpacerH(height = 12.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "时间", fontSize = 18.sp)
                Row(
                    modifier = Modifier.clickable(enabled = true, onClick = {showDatePickerDialog = true}),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (date > 0L) DateFormat.format(
                            "yyyy年MM月dd日EEEE",
                            date
                        ).toString() else "请选择日期", fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "arrow"
                    )
                }
            }

            HDivider(vertical = 20.dp)

            Text(
                text = "选择标签",
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            SpacerH(height = 6.dp)
            ContextualFlowRow(
                itemCount = tagsList.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .heightIn(max = 200.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                EventsTagsItem(eventsTagsBean = tagsList[it]) { bean ->
                    if (bean.isNew && bean.contet.isNotEmpty()) {
                        tagsList.removeAt(0)
                        tagsList.add(0,EventsTagsBean.getNewTag())
                    } else if (bean.isNew && bean.contet.isEmpty()) {
                        createTag = true
                    } else {
                        tagsList.forEachIndexed {index,_->
                            tagsList[index] = tagsList[index].copy(isSelected = it == index)
                        }
                    }
                }
            }

            SpacerH(height = 15.dp)
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(40.dp), contentAlignment = Alignment.CenterEnd) {
                Row {
                    TextButton(onClick = cancel) {
                        Text(text = "取消", fontSize = 14.sp)
                    }
                    SpacerW(width = 6.dp)
                    TextButton(onClick = { createEvents.invoke(eventTitle,date,
                        tagsList.first { it.isSelected }.contet) }, enabled = canCreate) {
                        Text(text = "确定", fontSize = 14.sp)
                    }
                }
            }
        }
    }

    if (createTag) {
        DialogWrapper(dismiss = { createTag = false }) {
            CreateTagDialog(createTag = {
                tagsList.forEachIndexed  { i, _ ->
                    if (i > 0){
                        tagsList[i] = tagsList[i].copy(isSelected = false)
                    }else{
                        tagsList[i] = tagsList[i].copy(contet = it, isSelected = true)
                    }
                }
                createTag = false
            }) {
                createTag = false
            }
        }
    }

    if (showDatePickerDialog) {
        DialogWrapper(
            dismiss = { showDatePickerDialog = false },
            dismissOnClickOutside = true
        ) {
            MyDatePickerDialog(cancel = {
                showDatePickerDialog = false
            }, confirm = {
                date = it
                showDatePickerDialog = false
            })
        }
    }
}

@Composable
private fun CreateTagDialog(createTag: (String) -> Unit, cancel: () -> Unit) {
    var tagContent by remember {
        mutableStateOf("")
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(.68f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp), contentAlignment = Alignment.Center
            ) {

                Text(text = "创建标签", fontSize = 16.sp, fontWeight = FontWeight.W600)
            }

            SpacerH(height = 8.dp)

            OutlinedTextField(
                value = tagContent,
                onValueChange = {
                    tagContent = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "请输入标签", color = Color.White.copy(alpha = .4f))
                },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                singleLine = true
            )

            SpacerH(height = 12.dp)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Row {
                    TextButton(onClick = cancel) {
                        Text(text = "取消", fontSize = 14.sp)
                    }
                    SpacerW(width = 6.dp)
                    TextButton(onClick = { createTag.invoke(tagContent) }) {
                        Text(text = "确定", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsTagsItem(eventsTagsBean: EventsTagsBean, onClick: (EventsTagsBean) -> Unit) {
    InputChip(selected = eventsTagsBean.isSelected,
        onClick = { onClick.invoke(eventsTagsBean) },
        label = {
            Text(text = eventsTagsBean.contet.ifEmpty { "创建标签" })
        },
        trailingIcon = if (eventsTagsBean.isNew && eventsTagsBean.contet.isNotEmpty()) {
            {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "clear",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyDatePickerDialog(cancel: () -> Unit, confirm: (Long) -> Unit) {
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(onDismissRequest = cancel, confirmButton = {
        TextButton(onClick = {
            confirm.invoke(datePickerState.selectedDateMillis ?: 0L)
        }, enabled = confirmEnabled.value) {
            Text(text = "确定")
        }
    }, dismissButton = {
        TextButton(onClick = cancel) {
            Text(text = "取消")
        }
    }) {
        DatePicker(state = datePickerState, showModeToggle = false)
    }
}

@Composable
private fun EventItem(events: Events) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
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
                        fontSize = 22.sp,
                        modifier = Modifier.alignByBaseline(),
                    )
                    SpacerW(width = 6.dp)
                    Text(
                        text = events.startDateTime.getWeekDay,
                        fontSize = 16.sp,
                        modifier = Modifier.alignByBaseline()
                    )
                }
                SpacerH(height = 2.dp)
                Text(text = "农历 ${events.startDateTime.toLunar}", fontSize = 15.sp)
                SpacerH(height = 2.dp)
                Text(
                    text = events.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W700
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Row {
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

    }
}
