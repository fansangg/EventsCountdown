package fan.san.eventscountdown.page.setting

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import fan.san.eventscountdown.common.CommonScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectEventPage(navHostController: NavHostController,glanceId:String){

    CommonScaffold(title = "选择事件", backClick = {navHostController.popBackStack()}) {

    }
}