package fan.san.eventscountdown.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

class WidgetWithEvents(
    @Embedded val widget:WidgetInfo,
    @Relation(
        parentColumn = "widgetId",
        entityColumn = "eventId",
        associateBy = Junction(EventWidgetCrossRef::class)
    )
    val events:List<Events>
)
