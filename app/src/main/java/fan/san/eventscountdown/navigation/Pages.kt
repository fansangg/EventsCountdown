package fan.san.eventscountdown.navigation

sealed class Pages(val route:String) {
    data object Home:Pages(RouterConfig.HOME)
    data object Log:Pages(RouterConfig.LOG)
    data object Setting:Pages(RouterConfig.SETTINGS){
        const val GLANCID = "glanceId"
        fun withParam(id:Int) = route.replace("{$GLANCID}", newValue = "$id")
    }

    data object SelectEvent:Pages(RouterConfig.SELECTEVENT){
        const val GLANCID = "glanceId"
        fun withParam(id:Int) = route.replace("{$GLANCID}", newValue = "$id")
    }
}