package fan.san.eventscountdown.page

import android.appwidget.AppWidgetManager
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fan.san.eventscountdown.navigation.Pages
import fan.san.eventscountdown.page.home.HomePage


@Composable
fun NavHostPage(startDestination:String,appWidgetId:Int){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination){
        composable(route = Pages.Home.route){
            HomePage(navController = navController)
        }

        composable(Pages.Setting.route, arguments = listOf(
            navArgument(Pages.Setting.glanceId){
                type = NavType.IntType
                defaultValue = appWidgetId
            }
        )){
            val glanceId = it.arguments?.getInt(Pages.Setting.glanceId)?:AppWidgetManager.INVALID_APPWIDGET_ID
            WidgetSettingsPage(glanceId)
        }

        composable(Pages.Log.route){
            LogPage(navController)
        }
    }
}