package fan.san.eventscountdown.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.repository.CountdownRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectEventsViewModel @Inject constructor(private val repository: CountdownRepository):ViewModel() {

    val selectedEventsList = mutableStateListOf<Events>()
    val unSelectedEventsList = mutableStateListOf<Events>()

    fun getEventsList(){
        viewModelScope.launch(Dispatchers.IO) {
            val ret = repository.getNextEvents(limit = -1)
            unSelectedEventsList.addAll(ret - selectedEventsList)
        }
    }

    fun selectEvent(event: Events){
        unSelectedEventsList.remove(event)
        selectedEventsList.add(event)
    }

    fun unSelectEvent(event: Events){
        selectedEventsList.remove(event)
        unSelectedEventsList.add(event)
    }

}