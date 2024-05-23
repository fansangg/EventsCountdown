package fan.san.eventscountdown.repository

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface EventsCountdownEntryPoint {

    fun getCountdownRepository(): CountdownRepository

    fun getWidgetInfoRepository():WidgetsInfoRepository

}