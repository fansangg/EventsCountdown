package fan.san.eventscountdown.page.home

import android.text.format.DateFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fan.san.eventscountdown.common.DialogWrapper
import fan.san.eventscountdown.common.HDivider
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.SpacerW
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.common.getWeekDay
import fan.san.eventscountdown.common.toLunar
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.entity.EventsTagsBean
import fan.san.eventscountdown.utils.CommonUtil
import fan.san.eventscountdown.viewmodel.MainViewModel
import kotlin.math.roundToInt

@Composable
fun EventsList(state: LazyListState, allEventsList: List<Events>,delete: (Events) -> Unit) {
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
        items(pastEvents, key = { it.id} ) {
            EventItem(it, delete = delete)
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
                            EventItem(events = it, delete = delete)
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
        items(futureEvents, key = { it.id} ) {
            EventItem(it, delete = delete)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewEvent(cancel: () -> Unit, createEvents: (title: String, date: Long, tag: String) -> Unit) {
    val viewModel = hiltViewModel<MainViewModel>()
    var eventTitle by remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current

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
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = "请输入事件标题", color = Color.White.copy(alpha = .4f))
                },
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                isError = isError,
                shape = RoundedCornerShape(12.dp),
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
                    modifier = Modifier.clickable(enabled = true, onClick = {
                        showDatePickerDialog = true
                        focusManager.clearFocus()
                    }),
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
                EventsTagsItem(eventsTagsBean = tagsList[it] , onClick = { bean ->
                    if (bean.isNew && bean.contet.isEmpty()) {
                        createTag = true
                    } else {
                        tagsList.forEachIndexed { index, _ ->
                            tagsList[index] = tagsList[index].copy(isSelected = it == index)
                        }
                    }
                }){
                    tagsList.removeAt(0)
                    tagsList.add(0, EventsTagsBean.getNewTag())
                }
            }

            SpacerH(height = 15.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp), contentAlignment = Alignment.CenterEnd
            ) {
                Row {
                    TextButton(onClick = cancel) {
                        Text(text = "取消", fontSize = 14.sp)
                    }
                    SpacerW(width = 6.dp)
                    TextButton(onClick = {
                        createEvents.invoke(
                            eventTitle, date,
                            tagsList.first { it.isSelected }.contet
                        )
                    }, enabled = canCreate) {
                        Text(text = "确定", fontSize = 14.sp)
                    }
                }
            }
        }
    }

    if (createTag) {
        DialogWrapper(dismiss = { createTag = false }) {
            CreateTagDialog(createTag = {
                tagsList.forEachIndexed { i, _ ->
                    if (i > 0) {
                        tagsList[i] = tagsList[i].copy(isSelected = false)
                    } else {
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
    val focusRequester = remember {
        FocusRequester()
    }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(text = "请输入标签", color = Color.White.copy(alpha = .4f))
                },
                shape = RoundedCornerShape(12.dp),
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
                    TextButton(
                        onClick = { createTag.invoke(tagContent) },
                        enabled = tagContent.isNotEmpty()
                    ) {
                        Text(text = "确定", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsTagsItem(eventsTagsBean: EventsTagsBean, onClick: (EventsTagsBean) -> Unit,clearClick:() -> Unit) {
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
                    modifier = Modifier.size(FilterChipDefaults.IconSize).clickable(onClick = clearClick)
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
private fun EventItem(events: Events,delete: (Events) -> Unit) {

    val maxWidth = (-120).dp

    var offsetX by remember {
        mutableFloatStateOf(0f)
    }

    val localDensity = LocalDensity.current

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {

            Box(
                modifier = Modifier
                    .width(120.dp)
                    .align(alignment = Alignment.CenterEnd), contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    ).padding(15.dp).clickable { delete.invoke(events) }
                , contentAlignment = Alignment.Center){
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "delete", modifier = Modifier.size(30.dp))
                }

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(x = offsetX.roundToInt(), y = 0)
                    }
                    .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                    .draggable(state = rememberDraggableState {
                        if (offsetX >= with(localDensity) { maxWidth.toPx() } && offsetX <= 0f) {
                            offsetX += it
                            Log.d("fansangg", "#EventItem: offsetX == $offsetX")
                        }

                    }, orientation = Orientation.Horizontal, onDragStopped = {
                        if (offsetX < with(localDensity) { maxWidth.toPx() } || offsetX < with(
                                localDensity
                            ) { maxWidth.toPx() } / 2) {
                            offsetX = with(localDensity) { maxWidth.toPx() }
                        }

                        if (offsetX > 0f || offsetX > with(localDensity) { maxWidth.toPx() } / 2) {
                            offsetX = 0f
                        }
                    })
            ) {
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

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
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