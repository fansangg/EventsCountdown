package fan.san.eventscountdown.widget

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

/**
 *@author  范三
 *@version 2024/5/2
 */

object WidgetStyles {
    val textColor = intPreferencesKey("textColor")
    val backgroundColor = intPreferencesKey("backgroundColor")
    val backgroundAlpha = floatPreferencesKey("backgroundAlpha")
}