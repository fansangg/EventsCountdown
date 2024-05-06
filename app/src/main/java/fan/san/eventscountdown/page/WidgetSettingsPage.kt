package fan.san.eventscountdown.page

import android.app.Activity
import android.app.WallpaperManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.SpacerW
import fan.san.eventscountdown.common.dataStore
import fan.san.eventscountdown.viewmodel.SettingsViewModel
import fan.san.eventscountdown.widget.CountdownWidgetStyles
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSettingsPage(glanceId: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentAlpha by remember {
        mutableFloatStateOf(1f)
    }

    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp
    val viewModel = hiltViewModel<SettingsViewModel>()
    val radioOptions = listOf("白色", "黑色")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    LaunchedEffect(key1 = Unit) {
        context.dataStore.data.collect{
            currentAlpha = it[CountdownWidgetStyles.backgroundAlpha]?:1f
            onOptionSelected(it[CountdownWidgetStyles.backgroundColorOptions]?:"白色")
        }
    }
    CommonScaffold(
        title = "小组件设置",
        showAction = true,
        showBack = false,
        actionIcon = Icons.Default.Done,
        actionClick = {
            (context as Activity).setResult(Activity.RESULT_OK)
            scope.launch {
                viewModel.updateWidgetAlpha(selectedOption,currentAlpha)
                context.finish()
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(12.dp)
                    )
            , verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                Box(modifier = Modifier.size(width = Dp(width / 5f * 3),height = Dp(height / 5f))){

                }
            }

            SpacerH(height = 12.dp)
            Text(text = "背景颜色", fontSize = 14.sp, modifier = Modifier.padding(start = 14.dp))
            SpacerH(height = 8.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                radioOptions.forEachIndexed { index, text ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .height(50.dp)
                            .selectable(
                                text == selectedOption,
                                onClick = { onOptionSelected(text) },
                                role = Role.RadioButton
                            ), verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = text == selectedOption,
                            onClick = null
                        )

                        SpacerW(12.dp)

                        Text(text = text, fontSize = 16.sp)
                    }

                    if (index != radioOptions.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(start = 50.dp, end = 12.dp))
                    }
                }

                SpacerH(6.dp)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                SpacerH(6.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp)
                        .height(50.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(currentAlpha * 100).roundToInt()}%",
                        modifier = Modifier.weight(0.2f),
                        textAlign = TextAlign.Center
                    )
                    Slider(value = currentAlpha, onValueChange = { alpha ->
                        currentAlpha = alpha
                    }, steps = 9, valueRange = 0f..1f, modifier = Modifier
                        .weight(1f)
                        .height(40.dp))
                }
            }
        }
    }
}