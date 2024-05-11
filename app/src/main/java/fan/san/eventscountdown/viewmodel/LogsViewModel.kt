package fan.san.eventscountdown.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.db.Logs
import fan.san.eventscountdown.repository.CountdownRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(private val repository: CountdownRepository):ViewModel() {

    val logs = mutableStateListOf<Logs>()
    val allDates = mutableListOf<String>()
    val currentDate = MutableStateFlow(System.currentTimeMillis().formatMd)

    fun getAllLogDate(){
        viewModelScope.launch(Dispatchers.IO) {
            val dates = repository.queryLogDates()
            allDates.clear()
            allDates.addAll(dates)
            currentDate.emit(allDates.firstOrNull()?:System.currentTimeMillis().formatMd)
            getLogsByDate()
        }
    }

    fun getLogsByDate(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.queryLogsByDate(currentDate.value)
                .collect{
                    logs.clear()
                    logs.addAll(it)
                }
        }

    }
}