package fan.san.holidaycountdown.page

import android.appwidget.AppWidgetManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fan.san.holidaycountdown.navigation.Pages


@Composable
fun NavHostPage(paddingValues: PaddingValues,startDestination:String){
    val navController = rememberNavController()
    NavHost(modifier = Modifier.padding(paddingValues), navController = navController, startDestination = startDestination){
        composable(route = Pages.Home.route){
            HomePage(navController = navController)
        }

        composable(Pages.Setting.route, arguments = listOf(
            navArgument(Pages.Setting.glanceId){
                type = NavType.IntType
                defaultValue = AppWidgetManager.INVALID_APPWIDGET_ID
            }
        )){
            val glanceId = it.arguments?.getInt(Pages.Setting.glanceId)?:AppWidgetManager.INVALID_APPWIDGET_ID
            WidgetSettingsPage(glanceId)
        }
    }
}