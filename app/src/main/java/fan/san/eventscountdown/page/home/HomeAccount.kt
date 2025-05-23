package fan.san.eventscountdown.page.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import fan.san.eventscountdown.common.EmptyLottie
import fan.san.eventscountdown.common.SpacerH
import fan.san.eventscountdown.common.SpacerW
import fan.san.eventscountdown.entity.CalendarAccountBean
import fan.san.eventscountdown.viewmodel.MainViewModel

@Composable
fun ChooseAccountDialog(
    accountSelected: (Long) -> Unit
) {

    val viewModel = hiltViewModel<MainViewModel>()
    LaunchedEffect(key1 = Unit) {
        if (viewModel.calendarAccounts.isEmpty()){
            viewModel.getCalendarAccounts()
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(.78f)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                val (title, refresh) = createRefs()

                Text(
                    text = "选择日历账户",
                    fontSize = 18.sp,
                    modifier = Modifier.constrainAs(title) {
                        centerTo(parent)
                    })

                RefreshAccount(modifier = Modifier.constrainAs(refresh) {
                    centerVerticallyTo(title)
                    start.linkTo(title.end)
                }, getAccoounts = {
                    viewModel.getCalendarAccounts()
                })
            }

            if (viewModel.calendarAccounts.isEmpty()) {
                NoAccount(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 270.dp)
                        .padding(horizontal = 6.dp)
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.calendarAccounts) {
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
fun ChooseRangeDialog(ret:(Int) -> Unit){
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(.76f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Text("选择要导入事件的范围", fontSize = 18.sp)
            }

            SpacerH(15.dp)

            Card(
                onClick = { ret.invoke(0) },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "全年事件")
                }

            }
            SpacerH(height = 12.dp)

            Card(
                onClick = { ret.invoke(1) },
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "未来半年")
                }
            }

            SpacerH(height = 12.dp)

            Card(
                onClick = { ret.invoke(2) },
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "未来一年")
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