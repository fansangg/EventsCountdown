package fan.san.holidaycountdown.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ColumnWithTitle(
    title: String,
    showBack: Boolean = true,
    showAction: Boolean = false,
    actionIcon: ImageVector? = null,
    actionClick: (() -> Unit)? = null,
    backClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 4.dp)
        ) {

            if (showBack) {
                IconButton(
                    onClick = { backClick?.invoke() },
                    modifier = Modifier.align(alignment = Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "backIcon"
                    )
                }
            }

            Text(text = title, modifier = Modifier.align(alignment = Alignment.Center))

            if (showAction) {
                IconButton(
                    onClick = { actionClick?.invoke() },
                    modifier = Modifier.align(alignment = Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = actionIcon ?: Icons.Default.Done,
                        contentDescription = "actionIcon"
                    )
                }
            }
        }

        content()
    }
}