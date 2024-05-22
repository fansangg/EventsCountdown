package fan.san.eventscountdown.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 6,
    entities = [Events::class, Logs::class, WidgetInfos::class, EventWidgetCrossRef::class],
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(
        from = 2,
        to = 3
    ), AutoMigration(from = 3, to = 4), AutoMigration(from = 4, to = 5),AutoMigration(from = 5, to = 6)]
)
abstract class AppDb : RoomDatabase() {
    abstract fun eventsDao(): Events.EventsDao
    abstract fun logsDao(): Logs.LogsDao
    abstract fun widgetInfosDao(): WidgetInfos.WidgetInfosDao
}