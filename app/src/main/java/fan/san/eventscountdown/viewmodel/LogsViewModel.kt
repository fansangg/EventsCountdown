package fan.san.eventscountdown.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.db.Logs
import fan.san.eventscountdown.repository.CountdownRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(private val repository: CountdownRepository):ViewModel() {

    val logs = mutableStateListOf<Logs>()
    val allDates = mutableListOf<String>()

    fun getAllLogDate(){
        viewModelScope.launch(Dispatchers.IO) {
            val dates = repository.queryLogDates()
            allDates.clear()
            allDates.addAll(dates)
        }
    }

    fun getLogsByDate(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.queryLogsByDate(System.currentTimeMillis().formatMd)
                .collect{
                    logs.clear()
                    logs.addAll(it)
                }
        }

    }
}