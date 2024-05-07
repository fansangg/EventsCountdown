package fan.san.eventscountdown.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 2, entities = [Events::class,Logs::class],autoMigrations = [AutoMigration(from = 1, to = 2)])
abstract class AppDb:RoomDatabase() {
    abstract fun eventsDao(): Events.EventsDao
    abstract fun logsDao():Logs.LogsDao
}