package fan.san.holidaycountdown.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 *@author  范三
 *@version 2024/5/2
 */
 
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widgetStyle")