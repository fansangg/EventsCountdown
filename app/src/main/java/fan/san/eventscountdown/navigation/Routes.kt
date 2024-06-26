package fan.san.eventscountdown.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    data object Home:Routes()
    @Serializable
    data object Log:Routes()

    @Serializable
    data class Setting(val glanceId: Int) : Routes()

    @Serializable
    data class SelectEvent(val list:String):Routes()
}