package fan.san.eventscountdown.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    version = 8,
    entities = [Events::class, Logs::class, WidgetInfo::class, EventWidgetCrossRef::class],
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(
        from = 2,
        to = 3
    ), AutoMigration(from = 3, to = 4), AutoMigration(from = 4, to = 5), AutoMigration(
        from = 5,
        to = 6
    ), AutoMigration(from = 6, to = 7, spec = AppDb.RenameWidgetInfo::class),AutoMigration(from = 7, to = 8, spec = AppDb.RenameEventWidgetCrossRef::class)]
)
abstract class AppDb : RoomDatabase() {
    abstract fun eventsDao(): Events.EventsDao
    abstract fun logsDao(): Logs.LogsDao
    abstract fun widgetInfoDao(): WidgetInfo.WidgetInfoDao
    abstract fun eventWidgetCrossRefDao(): EventWidgetCrossRef.EventWidgetCrossRefDao

    @RenameTable.Entries(RenameTable(fromTableName = "widget_infos", toTableName = "widget_info"))
    class RenameWidgetInfo : AutoMigrationSpec

    @RenameTable.Entries(RenameTable(fromTableName = "EventWidgetCrossRef", toTableName = "event_widget_cross_ref"))
    class RenameEventWidgetCrossRef : AutoMigrationSpec
}