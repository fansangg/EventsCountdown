package fan.san.eventscountdown.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
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

        val currentColor = mutableStateOf(Color(253, 253, 253))

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
}