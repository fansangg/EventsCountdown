package fan.san.eventscountdown.db

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters

@Entity(tableName = "widget_info")
@TypeConverters(Converters::class)
data class WidgetInfo(
    @PrimaryKey val id: Int,
    var color: Color,
    var colorOption:String = "白色",
    var followSystem:Boolean = false,
    var backgroundImg: String? = null,
){
    @Dao
    interface WidgetInfoDao{

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(widgetInfo: WidgetInfo)

        @Query("SELECT * FROM widget_info WHERE :id = id")
        fun queryById(id: Int):List<WidgetInfo>

        @Query("DELETE FROM widget_info WHERE :id = id")
        fun delete(id:Int)

        @Transaction
        @Query("SELECT * FROM widget_info where :id = id")
        fun getWidgetEvents(id:Int):List<WidgetWithEvents>

        @Transaction
        @Query("SELECT events.* FROM events INNER JOIN event_widget_cross_ref ON events.id = event_widget_cross_ref.eventId WHERE event_widget_cross_ref.widgetId = :widgetId AND events.start_date_time > strftime('%s', 'now') * 1000 ORDER BY events.start_date_time LIMIT 1")
        fun getNextEventsByWidgetId(widgetId:Int):List<Events>

        @Query("DELETE FROM widget_info WHERE id NOT IN (:ids)")
        fun deleteIfNotExists(ids:List<Int>):Int
    }
}