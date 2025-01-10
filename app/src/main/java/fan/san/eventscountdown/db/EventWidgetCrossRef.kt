package fan.san.eventscountdown.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Entity(
    tableName = "event_widget_cross_ref",
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
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        fun insert(eventWidgetCrossRefList: List<EventWidgetCrossRef>)

        @Query("DELETE FROM event_widget_cross_ref WHERE eventId IN (:list)")
        fun delete(list: List<Long>)
    }
}