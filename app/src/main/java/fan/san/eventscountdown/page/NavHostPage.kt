package fan.san.eventscountdown.page

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fan.san.eventscountdown.navigation.Routes
import fan.san.eventscountdown.page.home.HomePage
import fan.san.eventscountdown.page.setting.SelectEventPage
import fan.san.eventscountdown.page.setting.WidgetSettingsPage


val LocalNavController = compositionLocalOf<NavController> { error("No NavController provided") }

@Composable
fun NavHostPage(startDestination: Routes) {
    val navController = rememberNavController()
    CompositionLocalProvider(value = LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable<Routes.Home> {
                HomePage()
            }

            composable<Routes.Setting> {
                WidgetSettingsPage()
            }

            composable<Routes.Log> {
                LogPage()
            }

            /*typeMap = mapOf(
                    typeOf<List<Events>>() to CustomListNavType(
                        Events::class.java,
                        Events.serializer()
                    )
                ),*/

            composable<Routes.SelectEvent>(
                 enterTransition = {
                    slideInVertically(animationSpec = tween(durationMillis = 400)) {
                        it
                    } + fadeIn()
                }, exitTransition = {
                    slideOutVertically(animationSpec = tween(durationMillis = 400)) {
                        it
                    } + fadeOut()
                }) {
                SelectEventPage()
            }
        }
    }
}