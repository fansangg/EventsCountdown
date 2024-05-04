package fan.san.eventscountdown.widget

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fan.san.eventscountdown.repository.CountdownRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {

    fun getCountdownRepository(): CountdownRepository

}