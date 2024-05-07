package fan.san.eventscountdown.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.entity.CalendarAccountBean
import fan.san.eventscountdown.entity.MessageEvent
import fan.san.eventscountdown.repository.CountdownRepository
import fan.san.eventscountdown.widget.EventsCountdownWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: CountdownRepository
) : ViewModel() {

    var calendarAccounts = mutableStateListOf<CalendarAccountBean>()

    //申请权限的时间，用于触发回调做计算，回调时间 - 申请时间 < 300ms，认为是已经永久拒绝了权限
    var lastRequestPermissionTime = 0L
    var isTriggershouldShowRationale = false

    private val _messageEvent: Channel<MessageEvent> = Channel()

    val messageEvent = _messageEvent.receiveAsFlow()

    val allEventsList = mutableStateListOf<Events>()

    fun getCalendarAccounts() {
        calendarAccounts.addAll(repository.queryCalendarAccounts())
    }

    fun getCalendarEvents(accountId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getCalendarEvents(accountId)
            val count = repository.insertEvents(result)
            val ignoreCount = count.count { it == -1L }
            val sb = StringBuilder()
            sb.append("成功导入${count.size - ignoreCount}个事件")
            if (ignoreCount > 0)
                sb.append(",已忽略${ignoreCount}个重复事件")
            _messageEvent.send(
                MessageEvent.SnackBarMessage(
                    sb.toString(),
                    id = System.currentTimeMillis()
                )
            )
            Log.d("fansangg", "getCalendarEvents: insertCount = $count")

            EventsCountdownWidget().updateAll(context)
        }
    }

    fun getAllEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllEvents()
                .collect{
                    allEventsList.clear()
                    allEventsList.addAll(it)
                }
        }
    }
}