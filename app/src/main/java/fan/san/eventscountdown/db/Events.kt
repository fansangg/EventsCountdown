package fan.san.eventscountdown.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
@Serializable
@Entity(tableName = "events", indices = [Index(value = ["originId"], unique = true)])
data class Events(
    @PrimaryKey(autoGenerate = true) val id:Long = 0,
    val title:String,
    @ColumnInfo(name = "start_date_time")
    val startDateTime:Long,
    val isShow:Int = 1,
    @ColumnInfo(defaultValue = "自定义") val tag:String,
    val originId:Long? = null
):Parcelable{
    @Dao
    interface EventsDao{
        @Query("SELECT * FROM events ORDER BY start_date_time")
        fun query():Flow<List<Events>>
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insert(events: Events):Long

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insert(events: List<Events>):List<Long>

        @Delete
        suspend fun delete(vararg events: Events):Int
        @Delete
        suspend fun delete(events: List<Events>):Int
        @Query("UPDATE events SET isShow = :isShow where id = :id")
        suspend fun update(isShow: Int,id: Long):Int

        @Query("SELECT * FROM events WHERE start_date_time > :time ORDER BY start_date_time LIMIT :limit")
        fun getNextEvents(time: Long,limit: Int = 1):List<Events>

        @Query("SELECT DISTINCT tag FROM events")
        fun getTags():List<String>

        @Query("DELETE FROM events")
        fun deleteAll(): Int

        @Query("DELETE FROM events where tag = :tag")
        fun deleteByTag(tag: String): Int
    }
}
