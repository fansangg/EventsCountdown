package fan.san.eventscountdown.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

class WidgetWithEvents(
    @Embedded val widget:WidgetInfo,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(EventWidgetCrossRef::class, parentColumn = "widgetId", entityColumn = "eventId")
    )
    val events:List<Events>
)
