package fan.san.eventscountdown.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.viewmodel.LogsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogPage(navHostController: NavHostController) {

    val viewModel = hiltViewModel<LogsViewModel>()
    LaunchedEffect(key1 = Unit) {
        viewModel.getAllLogDate()
        viewModel.getLogsByDate()
    }
    CommonScaffold(title = "logs", backClick = {navHostController.popBackStack()}) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            SpacerH(height = 12.dp)
            ElevatedButton(
                onClick = { },
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = viewModel.allDates.lastOrNull() ?: "")
            }

            SpacerH(height = 12.dp)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {

                items(viewModel.logs) { logs ->
                    Text(text = logs.toString(), fontSize = 18.sp)
                }
            }
        }
    }

}