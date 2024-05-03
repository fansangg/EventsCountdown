package fan.san.eventscountdown.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import fan.san.eventscountdown.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopAppBar(
    title: String,
    showBack: Boolean = true,
    showAction: Boolean = false,
    actionIcon: ImageVector? = null,
    actionClick: (() -> Unit)? = null,
    backClick: (() -> Unit)? = null,
    behavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title, fontSize = 20.sp) },
        navigationIcon = {
            if (showBack)
                IconButton(onClick = { backClick?.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "back"
                    )
                }
        },
        actions = {
            if (showAction)
                IconButton(onClick = { actionClick?.invoke() }) {
                    Icon(
                        imageVector = actionIcon ?: Icons.Default.Done,
                        contentDescription = "action"
                    )
                }
        },
        scrollBehavior = behavior,
        colors = TopAppBarDefaults.topAppBarColors()
            .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonScaffold(
    modifier: Modifier = Modifier,
    title: String,
    showBack: Boolean = true,
    showAction: Boolean = false,
    actionIcon: ImageVector? = null,
    actionClick: (() -> Unit)? = null,
    backClick: (() -> Unit)? = null,
    behavior: TopAppBarScrollBehavior? = null,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = { CommonTopAppBar(title, showBack, showAction, actionIcon, actionClick, backClick, behavior) },
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets
    ) {
        content(it)
    }
}


@Composable
fun DialogWrapper(dismiss:(() -> Unit)? = null,dismissOnClickOutside:Boolean = false, dismissOnBackPress:Boolean = true, usePlatformDefaultWidth:Boolean = false,content:@Composable () -> Unit){
    Dialog(onDismissRequest = { dismiss?.invoke() }, properties = DialogProperties(dismissOnClickOutside = dismissOnClickOutside, dismissOnBackPress = dismissOnBackPress, usePlatformDefaultWidth = usePlatformDefaultWidth)) {
        content()
    }
}

@Composable
fun EmptyLottie(modifier: Modifier){
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(composition, iterations =  LottieConstants.IterateForever)
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}