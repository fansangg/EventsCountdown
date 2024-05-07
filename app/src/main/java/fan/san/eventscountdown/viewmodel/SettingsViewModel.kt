package fan.san.eventscountdown.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fan.san.eventscountdown.common.dataStore
import fan.san.eventscountdown.widget.CountdownWidgetStyles
import fan.san.eventscountdown.widget.EventsCountdownWidget
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 *@author  范三
 *@version 2024/5/2
 */

@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel() {

    var currentColor by mutableStateOf(Color(253, 253, 253))

    fun changeColor(color: String){
        currentColor = if (color == "白色"){
            Color(253, 253, 253)
        }else Color(37, 37, 39)
    }

    fun updateWidgetAlpha(color: String, newValue: Float) {
        val newColor = if (color == "白色")
            Color(253, 253, 253, alpha = (newValue * 255).roundToInt())
        else Color(37, 37, 39, alpha = (newValue * 255).roundToInt())
        Log.d("fansangg", "updateWidgetAlpha: $newValue")
        viewModelScope.launch {
            context.dataStore.edit {
                it[CountdownWidgetStyles.backgroundColor] = newColor.toArgb()
                it[CountdownWidgetStyles.backgroundAlpha] = newValue
                it[CountdownWidgetStyles.backgroundColorOptions] = color
            }
            GlanceAppWidgetManager(context).apply {
                getGlanceIds(EventsCountdownWidget::class.java).lastOrNull()?.let {
                    updateAppWidgetState(context, it) { prefs ->
                        prefs[CountdownWidgetStyles.backgroundColor] = newColor.toArgb()
                    }
                    EventsCountdownWidget().update(context, it)
                }
            }

        }
    }

    fun isLightColor(): Color {
        val luminance = (0.2126 * currentColor.red + 0.7152 * currentColor.green + 0.0722 * currentColor.blue)
        return if (luminance > 0.5) Color.Black else Color.White
    }
}