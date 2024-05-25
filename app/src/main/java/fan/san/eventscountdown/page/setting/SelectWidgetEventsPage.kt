package fan.san.eventscountdown.page.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.page.LocalNavController
import fan.san.eventscountdown.viewmodel.SelectEventsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectEventPage(selectedEvents:List<Events>){

    val navHostController = LocalNavController.current
    val viewModel = hiltViewModel<SelectEventsViewModel>()
    LaunchedEffect(key1 = Unit) {
        viewModel.selectedEventsList.addAll(selectedEvents)
        viewModel.getEventsList()
    }

    BackHandler {
        navHostController.popBackStack()
        navHostController.currentBackStackEntry?.savedStateHandle?.set("list",viewModel.selectedEventsList.toMutableList())
    }

    CommonScaffold(title = "选择事件", backClick = {navHostController.popBackStack()}) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp),){

            LazyColumn(modifier = Modifier.weight(1f),verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                items(viewModel.selectedEventsList, key = { events -> events.id }){ events ->
                    EventsItem(modifier = Modifier.animateItem(),events = events){
                        viewModel.unSelectEvent(events)
                    }
                }
            }

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                items(viewModel.unSelectedEventsList, key = { events -> events.id }){ events ->
                    EventsItem(modifier = Modifier.animateItem(),events = events){
                        viewModel.selectEvent(events)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsItem(modifier: Modifier,events: Events,onClick:()->Unit){
    ElevatedCard(modifier = modifier
        .fillMaxWidth()
        .height(80.dp), onClick = onClick) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Text(text = events.title)
        }
    }
}