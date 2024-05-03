package fan.san.eventscountdown.navigation

sealed class Pages(val route:String) {
    data object Home:Pages(RouterConfig.HOME)
    data object Setting:Pages(RouterConfig.SETTINGS){
        const val glanceId = "glanceId"
        fun withParam(id:Int) = route.replace("{$glanceId}", newValue = "$id")
    }
}