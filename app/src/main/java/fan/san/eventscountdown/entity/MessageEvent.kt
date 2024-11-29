package fan.san.eventscountdown.entity

sealed class MessageEvent {
    data object None : MessageEvent()
    data class SnackBarMessage(val message: String,val id:Long = System.currentTimeMillis()) : MessageEvent()
}