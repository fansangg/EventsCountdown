package fan.san.eventscountdown.page

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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

            composable<Routes.Setting>(
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                        animationSpec = tween(500)
                    )
                }
            ){
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
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                        animationSpec = tween(500)
                    )
                }

            ) {
                SelectEventPage()
            }
        }
    }
}