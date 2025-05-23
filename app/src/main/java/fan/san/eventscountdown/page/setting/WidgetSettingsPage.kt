package fan.san.eventscountdown.page.setting

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.hilt.navigation.compose.hiltViewModel
import fan.san.eventscountdown.R
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.HDivider
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.SpacerW
import fan.san.eventscountdown.common.dynamicTextColor
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.common.getWeekDay
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.navigation.Routes
import fan.san.eventscountdown.page.LocalNavController
import fan.san.eventscountdown.utils.CommonUtil
import fan.san.eventscountdown.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSettingsPage() {
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()



    val viewModel = hiltViewModel<SettingsViewModel>()

    LaunchedEffect(key1 = Unit) {
        navHostController.currentBackStackEntry?.savedStateHandle?.get<List<Events>>("selectedEvents")?.let {
            viewModel.eventsList.clear()
            viewModel.eventsList.addAll(it)
        }
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
                            navHostController.navigate(
                                route = Routes.SelectEvent(
                                    Json.encodeToString(
                                        viewModel.eventsList.toMutableList()
                                    )
                                )
                            )
                        }, verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            Text(
                                text = "选择事件",
                                fontSize = 16.sp,
                            )


                            Text(text = if (viewModel.eventsList.isEmpty()) "当前未选择事件" else "已选择${viewModel.eventsList.size}条事件", fontSize = 14.sp, modifier = Modifier.alpha(.5f))
                        }


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
                            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, viewModel.glanceId)
                        (context as Activity).setResult(Activity.RESULT_OK, result)
                        scope.launch {
                            viewModel.updateWidgetInfo()
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
                    .height(40.dp), thumb = {
                    val color = SliderDefaults.colors().thumbColor
                    Canvas(modifier = Modifier.size(24.dp)) {
                        drawCircle(color = color)
                        drawCircle(color = Color.Black, radius = size.minDimension / 2.5f)
                    }
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

    val localDensity = LocalDensity.current
    val width = with(localDensity){
        LocalWindowInfo.current.containerSize.width.toDp()
    }
    val height = with(localDensity){
        LocalWindowInfo.current.containerSize.height.toDp()
    }

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
                .size(145.dp,65.dp)
                .clip(RoundedCornerShape(30.dp))
        ) {
            Image(painter = painterResource(R.drawable.countdown_bg),contentDescription = null)
            if (viewModel.eventsList.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(start = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(46.dp)
                            .background(color = Color.White).clip(RoundedCornerShape(23.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = CommonUtil.getDaysDiff(viewModel.eventsList.first{it.startDateTime > System.currentTimeMillis()}.startDateTime),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Column {
                        Text(
                            text = viewModel.eventsList.first{it.startDateTime > System.currentTimeMillis()}.title,
                            color = Color(0xff262626),
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "天剩余",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                /*Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = viewModel.eventsList.first{it.startDateTime > System.currentTimeMillis()}.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W700,
                        color = viewModel.currentColor.dynamicTextColor
                    )

                    SpacerH(2.dp)

                    Text(
                        text = "${viewModel.eventsList.first{it.startDateTime > System.currentTimeMillis()}.startDateTime.formatMd}  ${viewModel.eventsList.first{it.startDateTime > System.currentTimeMillis()}.startDateTime.getWeekDay}",
                        fontSize = 12.sp,
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
                                fontSize = 14.sp,
                                color = viewModel.currentColor.dynamicTextColor
                            )
                            Text(
                                text = CommonUtil.getDaysDiff(viewModel.eventsList.first{it.startDateTime > System.currentTimeMillis()}.startDateTime),
                                modifier = Modifier.alignByBaseline(),
                                fontSize = 26.sp,
                                color = viewModel.currentColor.dynamicTextColor,
                                fontWeight = FontWeight.W700
                            )
                            Text(
                                text = " 天",
                                modifier = Modifier.alignByBaseline(),
                                fontSize = 14.sp,
                                color = viewModel.currentColor.dynamicTextColor
                            )
                        }
                    }
                }*/
            } else {
                Text(
                    text = "当前无事件",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    //color = viewModel.currentColor.dynamicTextColor,
                    color = Color(0xff262626),
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