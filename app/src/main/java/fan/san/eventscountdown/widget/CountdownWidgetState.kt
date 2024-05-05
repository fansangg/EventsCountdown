package fan.san.eventscountdown.widget

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 *@author  范三
 *@version 2024/5/2
 */

object CountdownWidgetStyles {
    val textColor = intPreferencesKey("countdown_textColor")
    val backgroundColor = intPreferencesKey("countdown_backgroundColor")
    val backgroundColorOptions = stringPreferencesKey("countdown_backgroundColor_options")
    val backgroundAlpha = floatPreferencesKey("countdown_backgroundAlpha")
}

object CountdownWidgetInfos{
    val title = stringPreferencesKey("countdown_title")
    val days = stringPreferencesKey("countdown_days")
}