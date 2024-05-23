package fan.san.eventscountdown.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    version = 7,
    entities = [Events::class, Logs::class, WidgetInfo::class, EventWidgetCrossRef::class],
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(
        from = 2,
        to = 3
    ), AutoMigration(from = 3, to = 4), AutoMigration(from = 4, to = 5), AutoMigration(
        from = 5,
        to = 6
    ), AutoMigration(from = 6, to = 7, spec = AppDb.RenameWidgetInfo::class)]
)
abstract class AppDb : RoomDatabase() {
    abstract fun eventsDao(): Events.EventsDao
    abstract fun logsDao(): Logs.LogsDao
    abstract fun widgetInfoDao(): WidgetInfo.WidgetInfoDao
    abstract fun eventWidgetCrossRefDao(): EventWidgetCrossRef.EventWidgetCrossRefDao

    @RenameTable.Entries(RenameTable(fromTableName = "widget_infos", toTableName = "widget_info"))
    class RenameWidgetInfo : AutoMigrationSpec
}