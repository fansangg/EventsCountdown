package fan.san.eventscountdown.page.setting

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.HDivider
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.SpacerW
import fan.san.eventscountdown.common.dynamicTextColor
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.common.getWeekDay
import fan.san.eventscountdown.navigation.Pages
import fan.san.eventscountdown.utils.CommonUtil
import fan.san.eventscountdown.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSettingsPage(navHostController: NavHostController, glanceId: Int) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Log.d("fansangg", "#WidgetSettingsPage: glanceId == $glanceId")


    val viewModel = hiltViewModel<SettingsViewModel>()

    LaunchedEffect(key1 = Unit) {
        viewModel.getWidgetInfo(glanceId)
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
                WidgetPreview(viewModel)
                SpacerH(height = 12.dp)
                Text(
                    text = "背景颜色",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 14.dp)
                )
                SpacerH(height = 8.dp)

                AttributeSetting(viewModel)

                SpacerH(height = 12.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 12.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                        .clickable {
                            navHostController.navigate(route = Pages.SelectEvent.withParam(glanceId))
                        }, verticalArrangement = Arrangement.Center) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "选择事件",
                            fontSize = 16.sp,
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "jump"
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {

                TextButton(
                    onClick = { cancelSetting(context) },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "取消",
                        fontSize = 18.sp
                    )
                }


                TextButton(
                    onClick = {
                        val result =
                            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, glanceId)
                        (context as Activity).setResult(Activity.RESULT_OK, result)
                        scope.launch {
                            viewModel.updateWidgetInfo(glanceId)
                            context.finish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(12.dp), enabled = viewModel.eventsList.isNotEmpty()
                ) {
                    Text(
                        text = "确定",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributeSetting(viewModel: SettingsViewModel) {
    val isDark = isSystemInDarkTheme()
    LaunchedEffect(key1 = isDark) {
        viewModel.changeColor(isDark)
    }
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
                            viewModel.changeColor(isDark)
                        },
                        role = Role.RadioButton,
                        enabled = viewModel.followSystem.not()
                    ), verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = text == viewModel.selectedOption,
                    onClick = null,
                    enabled = viewModel.followSystem.not()
                )

                SpacerW(12.dp)

                Text(
                    text = text,
                    fontSize = 16.sp,
                    modifier = Modifier.alpha(if (viewModel.followSystem) .6f else 1f)
                )
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


        HDivider(vertical = 12.dp, horizontal = 12.dp)

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
                    viewModel.changeColor(isDark)
                }, steps = 9, valueRange = 0f..1f, modifier = Modifier
                    .weight(1f)
                    .height(40.dp), track = {
                    SliderDefaults.Track(
                        sliderState = it,
                        modifier = Modifier.scale(scaleX = 1f, scaleY = 1.2f)
                    )
                }
            )
        }

        HDivider(vertical = 12.dp, horizontal = 12.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(12.dp)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = "跟随系统", fontSize = 16.sp)

            Switch(checked = viewModel.followSystem, onCheckedChange = {
                viewModel.followSystem = it
                viewModel.changeColor(isDark)
            })
        }
    }
}

@Composable
private fun WidgetPreview(
    viewModel: SettingsViewModel,
) {

    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = .5f),
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
            if (viewModel.eventsList.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = viewModel.eventsList.first().title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W700,
                        color = viewModel.currentColor.dynamicTextColor
                    )

                    Text(
                        text = "${viewModel.eventsList.first().startDateTime.formatMd}  ${viewModel.eventsList.first().startDateTime.getWeekDay}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W700,
                        color = viewModel.currentColor.dynamicTextColor
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Row {
                            Text(
                                text = "还剩 ",
                                modifier = Modifier.alignByBaseline(),
                                fontSize = 16.sp,
                                color = viewModel.currentColor.dynamicTextColor
                            )
                            Text(
                                text = CommonUtil.getDaysDiff(viewModel.eventsList.first().startDateTime),
                                modifier = Modifier.alignByBaseline(),
                                fontSize = 37.sp,
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
            } else {
                Text(
                    text = "当前无事件",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W700,
                    color = viewModel.currentColor.dynamicTextColor,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }

        }
    }
}


private fun cancelSetting(context: Context) {
    (context as Activity).setResult(Activity.RESULT_CANCELED)
    context.finish()
}