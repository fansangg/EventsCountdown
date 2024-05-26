package fan.san.eventscountdown.page.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.EmptyLottie
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.page.LocalNavController
import fan.san.eventscountdown.viewmodel.SelectEventsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectEventPage() {

    val navHostController = LocalNavController.current
    val viewModel = hiltViewModel<SelectEventsViewModel>()

    val isAllEmpty by remember {
        derivedStateOf {
            viewModel.unSelectedEventsList.isEmpty() && viewModel.selectedEventsList.isEmpty()
        }
    }

    CommonScaffold(
        title = "选择事件",
        backClick = { navHostController.popBackStack() },
        showAction = isAllEmpty.not(),
        actionClick = {
            navHostController.previousBackStackEntry?.savedStateHandle?.set(
                "selectedEvents",
                viewModel.selectedEventsList.toMutableList()
            )
            navHostController.popBackStack()
        },
        actionIcon = Icons.Default.Done
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            if (isAllEmpty) {
                Empty()
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "已选择")
                    Text(text = "未来事件")
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(bottom = 12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {

                        if (viewModel.selectedEventsList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxSize(), contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "无事件")
                                }
                            }
                        } else {
                            items(
                                viewModel.selectedEventsList,
                                key = { events -> events.id }) { events ->
                                EventsItem(modifier = Modifier.animateItem(), events = events) {
                                    viewModel.unSelectEvent(events)
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(bottom = 12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {

                        if (viewModel.unSelectedEventsList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxSize(), contentAlignment = Alignment.Center
                                ) {
                                    Text(text = if (viewModel.selectedEventsList.isEmpty()) "未添加事件" else "所有事件都已添加")
                                }
                            }
                        } else {
                            items(
                                viewModel.unSelectedEventsList,
                                key = { events -> events.id }) { events ->
                                EventsItem(modifier = Modifier.animateItem(), events = events) {
                                    viewModel.selectEvent(events)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Empty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyLottie(modifier = Modifier.size(150.dp))
        SpacerH(height = 12.dp)
        Text(text = "空空如也，请先添加事件吧~", fontSize = 16.sp, fontWeight = FontWeight.W600)
    }
}

@Composable
private fun EventsItem(modifier: Modifier, events: Events, onClick: () -> Unit) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 12.dp),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp), verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = events.startDateTime.formatMd, fontSize = 12.sp)
            Text(
                text = events.title,
                maxLines = 1,
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.W600,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
        }
    }
}