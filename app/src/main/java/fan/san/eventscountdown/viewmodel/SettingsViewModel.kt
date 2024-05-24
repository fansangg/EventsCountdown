package fan.san.eventscountdown.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fan.san.eventscountdown.common.defaultLightColor
import fan.san.eventscountdown.common.defaultNightColor
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.db.WidgetInfo
import fan.san.eventscountdown.repository.CountdownRepository
import fan.san.eventscountdown.repository.WidgetsInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

/**
 *@author  范三
 *@version 2024/5/2
 */

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val countdownRepository: CountdownRepository,
    private val widgetsInfoRepository: WidgetsInfoRepository
) : ViewModel() {


    val eventsList = mutableStateListOf<Events>()
    val radioOptions = listOf("白色", "黑色")
    var currentColor by mutableStateOf(Color(253, 253, 253))
    var currentAlpha by mutableFloatStateOf(1f)
    var selectedOption by mutableStateOf(radioOptions[0])
    var followSystem by mutableStateOf(false)

    fun getWidgetInfo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val ret = widgetsInfoRepository.queryWidgetWithEvents(id = id)
            if (ret.isNotEmpty()) {
                currentColor = ret.first().widget.color
                currentAlpha = currentColor.alpha
                selectedOption = ret.first().widget.colorOption
                followSystem = ret.first().widget.followSystem
                eventsList.clear()
                eventsList.addAll(ret.first().events)
            }
        }
    }

    fun changeColor(isDark: Boolean) {
        currentColor = if (followSystem){
            if (isDark.not()) {
                defaultLightColor.copy(alpha = currentAlpha)
            } else defaultNightColor.copy(alpha = currentAlpha)
        }else{
            if (selectedOption == "白色") {
                defaultLightColor.copy(alpha = currentAlpha)
            } else defaultNightColor.copy(alpha = currentAlpha)
        }
    }

    fun updateWidgetInfo(glaceId: Int) {

        val widgetInfo = WidgetInfo(glaceId, color = currentColor, selectedOption, followSystem = followSystem)
        viewModelScope.launch(Dispatchers.IO) {
            widgetsInfoRepository.insert(widgetInfo)
            widgetsInfoRepository.updateWidgetInfoState(widgetInfo, eventsList.first())
        }
    }
}