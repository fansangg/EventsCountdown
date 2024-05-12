package fan.san.eventscountdown.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.db.WidgetInfos
import fan.san.eventscountdown.repository.CountdownRepository
import fan.san.eventscountdown.repository.WidgetsInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt
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

    var nextEvents by mutableStateOf(
        Events(
            0L,
            "",
            (System.currentTimeMillis().milliseconds + 7.days).inWholeMilliseconds
        )
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val nextEventsList = countdownRepository.getNextEvents()
            if (nextEventsList.isNotEmpty()) {
                withContext(Dispatchers.Main){
                    nextEvents = nextEventsList.first()
                }
            }
        }
    }

    val radioOptions = listOf("白色", "黑色")
    var currentColor by mutableStateOf(Color(253, 253, 253))
    var currentAlpha by mutableFloatStateOf(1f)
    var selectedOption by mutableStateOf(radioOptions[0])

    fun getWidgetInfos(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val ret = widgetsInfoRepository.queryById(id = id)
            if (ret.isNotEmpty()) {
                currentColor = ret.first().color
                currentAlpha = currentColor.alpha
                selectedOption = ret.first().colorOption
            }
        }
    }

    fun changeColor() {
        currentColor = if (selectedOption == "白色") {
            Color(253, 253, 253, alpha = (currentAlpha * 255).roundToInt())
        } else Color(37, 37, 39, alpha = (currentAlpha * 255).roundToInt())
    }


    fun updateWidgetInfos(glaceId: Int) {

        val widgetInfos = WidgetInfos(glaceId, color = currentColor, selectedOption)
        viewModelScope.launch(Dispatchers.IO) {
            widgetsInfoRepository.insert(widgetInfos)
            widgetsInfoRepository.updateWidgetInfosState(widgetInfos, nextEvents)
        }
    }
}