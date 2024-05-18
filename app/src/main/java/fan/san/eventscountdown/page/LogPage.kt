package fan.san.eventscountdown.page

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import fan.san.eventscountdown.BuildConfig
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.DialogWrapper
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.viewmodel.LogsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogPage(navHostController: NavHostController) {

    val viewModel = hiltViewModel<LogsViewModel>()
    LaunchedEffect(key1 = Unit) {
        viewModel.getAllLogDate()
        viewModel.getLogsByDate()
    }

    val currentDate by viewModel.currentDate.collectAsState(initial = System.currentTimeMillis().formatMd)

    var showMenu by remember {
        mutableStateOf(false)
    }

    var showInfoDialog by remember {
        mutableStateOf(false)
    }

    CommonScaffold(title = "日志", backClick = { navHostController.popBackStack() }, actionIcon = Icons.Default.Info, actionClick = {
        showInfoDialog = true
    }, showAction = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            SpacerH(height = 12.dp)

            Box(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
                ElevatedButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .width(120.dp)
                        .align(alignment = Alignment.Center)
                ) {
                    Text(text = currentDate)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    offset = DpOffset((-5).dp, 2.dp),
                    modifier = Modifier.width(130.dp)
                ) {
                    viewModel.allDates.fastForEachIndexed { index, s ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = s,
                                    color = if (currentDate == s) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            onClick = {
                                viewModel.currentDate.tryEmit(s)
                                viewModel.getLogsByDate()
                                showMenu = false
                            }, colors = MenuDefaults.itemColors()
                        )
                        if (index != viewModel.allDates.lastIndex)
                            HorizontalDivider()
                    }
                }

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

            if (showInfoDialog){
                DialogWrapper(dismiss = {showInfoDialog = false}, dismissOnClickOutside = true) {
                    Column(modifier = Modifier.fillMaxWidth(0.7f).padding(12.dp).background(color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(12.dp))) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 8.dp)
                        ) {
                            Text(
                                text = "版本代码",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = BuildConfig.VERSION_CODE.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 8.dp)
                        ) {
                            Text(
                                text = "版本名称",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = BuildConfig.VERSION_NAME,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 8.dp)
                        ) {
                            Text(
                                text = "构建时间",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = DateFormat.format("yyyy-MM-dd HH:mm:ss",BuildConfig.BUILD_TIME).toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 8.dp)
                        ) {
                            Text(
                                text = "构建类型",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = BuildConfig.BUILD_TYPE,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }

}