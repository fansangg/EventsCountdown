package fan.san.eventscountdown.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Events::class])
abstract class AppDb:RoomDatabase() {
    abstract fun eventsDao(): Events.EventsDao
}