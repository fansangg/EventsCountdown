package fan.san.eventscountdown.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.navigation.CustomListNavType
import fan.san.eventscountdown.navigation.Routes
import fan.san.eventscountdown.page.home.HomePage
import fan.san.eventscountdown.page.setting.SelectEventPage
import fan.san.eventscountdown.page.setting.WidgetSettingsPage
import kotlin.reflect.typeOf


val LocalNavController = compositionLocalOf<NavController> { error("No NavController provided") }

@Composable
fun NavHostPage(startDestination: Routes) {
    val navController = rememberNavController()
    CompositionLocalProvider(value = LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable<Routes.Home> {
                HomePage()
            }

            composable<Routes.Setting>(typeMap =  mapOf(
                typeOf<List<Events>>() to CustomListNavType(Events::class.java,Events.serializer()))) {
                val entity = it.toRoute<Routes.Setting>()
                WidgetSettingsPage(entity.glanceId)
            }

            composable<Routes.Log> {
                LogPage()
            }

            composable<Routes.SelectEvent>(
                typeMap = mapOf(
                    typeOf<List<Events>>() to CustomListNavType(
                        Events::class.java,
                        Events.serializer()
                    )
                )
            ) {
                val selectEvent = it.toRoute<Routes.SelectEvent>()
                SelectEventPage(selectEvent.list)
            }
        }
    }
}