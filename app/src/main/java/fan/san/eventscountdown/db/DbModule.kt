package fan.san.eventscountdown.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): AppDb = Room.databaseBuilder(context,AppDb::class.java,"fansanApp.db")
        .fallbackToDestructiveMigration().build()


    @Singleton
    @Provides
    fun provideEventDao(db:AppDb):Events.EventsDao = db.eventsDao()

    @Singleton
    @Provides
    fun provideLogsDao(db: AppDb):Logs.LogsDao = db.logsDao()
}