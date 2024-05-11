package fan.san.eventscountdown.db

import android.text.format.DateFormat
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import fan.san.eventscountdown.common.formatMd
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "logs")
data class Logs(
    @PrimaryKey(autoGenerate = true) val id:Long = 0,
    val date:String,
    val time:Long,
    val message:String
){

    @Dao
    interface LogsDao{
        @Insert
        fun insert(logs: Logs)

        @Query("SELECT * FROM logs WHERE :date = date")
        fun queryByDate(date:String): Flow<List<Logs>>

        @Query("SELECT DISTINCT date FROM logs order by time DESC")
        fun queryDates():List<String>

        @Query("DELETE FROM logs WHERE time < strftime('%s', date('now', '-7 days')) * 1000")
        fun deleteBefore7()
    }

    override fun toString(): String {
        return "$id  - ${DateFormat.format("HH:mm:ss",time)}  - $message"
    }

    companion object{
        fun create(message: String):Logs = Logs(date = System.currentTimeMillis().formatMd, time = System.currentTimeMillis(), message = message)
    }
}
