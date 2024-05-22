package fan.san.eventscountdown.db

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction

class WidgetWithEvents(
    @Embedded val widget:WidgetInfos,
    @Relation(
        parentColumn = "widgetId",
        entityColumn = "eventId",
        associateBy = Junction(EventWidgetCrossRef::class)
    )
    val events:List<Events>
) {

    @Dao
    interface WidgetWithEventsDao {
        @Transaction
        @Query("SELECT * FROM widget_infos where :id = id")
        fun getWidgetEvents(id:Long):List<WidgetWithEvents>
    }
}