package fan.san.eventscountdown.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.entity.CalendarAccountBean
import fan.san.eventscountdown.entity.MessageEvent
import fan.san.eventscountdown.repository.CountdownRepository
import fan.san.eventscountdown.widget.EventsCountdownWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    private val _messageEvent: Channel<MessageEvent> = Channel()

    val messageEvent = _messageEvent.receiveAsFlow()

    val allEventsList = mutableStateListOf<Events>()

    suspend fun getAllTags() =
        viewModelScope.async(Dispatchers.IO) { repository.getAllTags() }.await()

    fun getCalendarAccounts() {
        calendarAccounts.clear()
        calendarAccounts.addAll(repository.queryCalendarAccounts())
    }

    fun deleteEvent(events: Events){
        viewModelScope.launch(Dispatchers.IO){
            repository.delete(events)
            EventsCountdownWidget().updateAll(context)
        }
    }

    fun createEvents(title:String,date:Long,tag:String){
        viewModelScope.launch(Dispatchers.IO) {
            val ret = repository.insertEvents(Events(title = title, startDateTime = date.todayZeroTime, tag = tag))
            if (ret != -1L){
                _messageEvent.send(MessageEvent.SnackBarMessage(message = "已添加「$title」", id = System.currentTimeMillis()))
            }
        }
    }

    fun getCalendarEvents(accountId: Long,type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getCalendarEvents(accountId,type)
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
                .collect {
                    allEventsList.clear()
                    allEventsList.addAll(it)
                }
        }
    }

    fun deleteEventsByType(type: String){
        viewModelScope.launch(Dispatchers.IO){
            when(type){
                "0" -> {
                    val ret = clearSameEvents()
                    if (ret == 0){
                        _messageEvent.send(MessageEvent.SnackBarMessage("没有重复事件"))
                    }else{
                        _messageEvent.send(MessageEvent.SnackBarMessage("已删除${ret}条事件"))
                    }
                }

                "1" -> {
                    val ret = deleteAll()
                    _messageEvent.send(MessageEvent.SnackBarMessage("已删除${ret}条事件"))
                }

                else -> {
                    val ret = deleteByTag(type)
                    _messageEvent.send(MessageEvent.SnackBarMessage("已删除${ret}条事件"))
                }
            }
        }
    }

    private suspend fun deleteAll(): Int{
        return repository.deleteAllEvents()
    }

    private suspend fun deleteByTag(tag: String): Int{
        return repository.deleteByTag(tag)
    }

    private suspend fun clearSameEvents():Int{
        return repository.clearSameEvents()
    }
}