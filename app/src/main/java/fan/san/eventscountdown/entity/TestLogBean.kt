package fan.san.eventscountdown.entity

import android.text.format.DateFormat

data class TestLogBean(
    val time:Long,
    val message:String
){
    override fun toString(): String {
        return "${DateFormat.format("MM-dd HH:mm:ss",time)} --- $message \n"
    }
}
