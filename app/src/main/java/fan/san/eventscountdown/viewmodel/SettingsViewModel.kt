package fan.san.eventscountdown.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fan.san.eventscountdown.widget.WidgetStyles
import fan.san.eventscountdown.common.dataStore
import fan.san.eventscountdown.widget.EventsCountdownWidget
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *@author  范三
 *@version 2024/5/2
 */

@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext private val context: Context) : ViewModel() {


    fun updateWidgetAlpha(newValue: Float) {
        Log.d("fansangg", "updateWidgetAlpha: $newValue")
        viewModelScope.launch {
            context.dataStore.edit {
                it[WidgetStyles.backgroundAlpha] = newValue
            }
            GlanceAppWidgetManager(context).apply {
                getGlanceIds(EventsCountdownWidget::class.java).lastOrNull()?.let {
                    updateAppWidgetState(context, it) { prefs ->
                        prefs[WidgetStyles.backgroundAlpha] = newValue
                    }
                    EventsCountdownWidget().update(context,it)
                }
            }

        }
    }
}