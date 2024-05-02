package fan.san.holidaycountdown.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import fan.san.holidaycountdown.common.ColumnWithTitle

@Composable
fun HomePage(navController: NavController){
    ColumnWithTitle(title = "假日倒计时", showBack = false){

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Text(text = "倒计时")
        }
    }
}