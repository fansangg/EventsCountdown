package fan.san.eventscountdown.page

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.SpacerW
import fan.san.eventscountdown.common.dynamicTextColor
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.common.getWeekDay
import fan.san.eventscountdown.utils.CommonUtil
import fan.san.eventscountdown.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSettingsPage(glanceId: Int) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Log.d("fansangg", "#WidgetSettingsPage: glanceId == $glanceId")


    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp
    val viewModel = hiltViewModel<SettingsViewModel>()

    LaunchedEffect(key1 = Unit) {
        viewModel.getWidgetInfos(glanceId)
    }

    BackHandler() {
        cancelSetting(context)
    }

    CommonScaffold(
        title = "小组件设置",
        showBack = false
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                SpacerH(height = 14.dp)
                WidgetPreview(width, height, viewModel)
                SpacerH(height = 12.dp)
                Text(
                    text = "背景颜色",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 14.dp)
                )
                SpacerH(height = 8.dp)

                AttributeSetting(viewModel)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                ElevatedButton(onClick = { cancelSetting(context) }) {
                    Text(
                        text = "取消",
                        fontSize = 18.sp
                    )
                }

                ElevatedButton(onClick = {
                    (context as Activity).setResult(Activity.RESULT_OK)
                    scope.launch {
                        viewModel.updateWidgetInfos(glanceId)
                        context.finish()
                    }
                }) {

                    Text(
                        text = "确定",
                        fontSize = 18.sp
                    )
                }

            }
        }
    }
}

@Composable
private fun AttributeSetting(viewModel: SettingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        viewModel.radioOptions.forEachIndexed { index, text ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(50.dp)
                    .selectable(
                        text == viewModel.selectedOption,
                        onClick = {
                            viewModel.selectedOption = text
                            viewModel.changeColor()
                        },
                        role = Role.RadioButton
                    ), verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = text == viewModel.selectedOption,
                    onClick = null
                )

                SpacerW(12.dp)

                Text(text = text, fontSize = 16.sp)
            }

            if (index != viewModel.radioOptions.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        start = 50.dp,
                        end = 12.dp
                    )
                )
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
                text = "${(viewModel.currentAlpha * 100).roundToInt()}%",
                modifier = Modifier.weight(0.2f),
                textAlign = TextAlign.Center
            )
            Slider(
                value = viewModel.currentAlpha, onValueChange = { alpha ->
                    viewModel.currentAlpha = alpha
                    viewModel.changeColor()
                }, steps = 9, valueRange = 0f..1f, modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            )
        }
    }
}

@Composable
private fun WidgetPreview(
    width: Int,
    height: Int,
    viewModel: SettingsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = Dp(width / 5f * 3),
                    height = Dp(height / 7f)
                )
                .background(
                    viewModel.currentColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = viewModel.nextEvents.title.ifEmpty { "春节" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = viewModel.currentColor.dynamicTextColor
                )

                SpacerH(height = 2.dp)

                Text(
                    text = "${viewModel.nextEvents.startDateTime.formatMd}  ${viewModel.nextEvents.startDateTime.getWeekDay}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500,
                    color = viewModel.currentColor.dynamicTextColor
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Row {
                        Text(
                            text = "剩余 ",
                            modifier = Modifier.alignByBaseline(),
                            fontSize = 16.sp,
                            color = viewModel.currentColor.dynamicTextColor
                        )
                        Text(
                            text = CommonUtil.getDaysDiff(viewModel.nextEvents.startDateTime),
                            modifier = Modifier.alignByBaseline(),
                            fontSize = 38.sp,
                            color = viewModel.currentColor.dynamicTextColor,
                            fontWeight = FontWeight.W700
                        )
                        Text(
                            text = " 天",
                            modifier = Modifier.alignByBaseline(),
                            fontSize = 16.sp,
                            color = viewModel.currentColor.dynamicTextColor
                        )
                    }
                }
            }
        }
    }
}


private fun cancelSetting(context: Context) {
    (context as Activity).setResult(Activity.RESULT_CANCELED)
    context.finish()
}