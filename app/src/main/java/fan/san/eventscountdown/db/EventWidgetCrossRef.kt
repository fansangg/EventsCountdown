package fan.san.eventscountdown.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert


@Entity(
    primaryKeys = ["eventId", "widgetId"],
    foreignKeys = [ForeignKey(
        entity = Events::class,
        parentColumns = ["id"],
        childColumns = ["eventId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = WidgetInfo::class,
        parentColumns = ["id"],
        childColumns = ["widgetId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["eventId"]),Index(value = ["widgetId"])]
)
class EventWidgetCrossRef(
    @ColumnInfo(name = "eventId")
    val eventId: Long,
    @ColumnInfo(name = "widgetId")
    val widgetId: Long
){
    @Dao
    interface EventWidgetCrossRefDao {
        @Insert
        fun insert(eventWidgetCrossRefList: List<EventWidgetCrossRef>)
    }
}