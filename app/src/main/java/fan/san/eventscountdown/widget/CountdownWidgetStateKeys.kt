package fan.san.eventscountdown.widget

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 *@author  范三
 *@version 2024/5/2
 */

object CountdownWidgetStateKeys {
    val textColor = intPreferencesKey("countdown_textColor")
    val backgroundColor = intPreferencesKey("countdown_backgroundColor")
    val title = stringPreferencesKey("countdown_title")
    val date = longPreferencesKey("countdown_date")
    val isFollowSystem = booleanPreferencesKey("countdown_follow_system")
}