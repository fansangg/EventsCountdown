package fan.san.holidaycountdown.common

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 *@author  范三
 *@version 2024/5/2
 */
 
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widgetStyle")

@Composable
fun Int.SpacerW() { Spacer(modifier = Modifier.width(this.dp)) }


@Composable
fun Int.SpacerH(){ Spacer(modifier = Modifier.height(this.dp)) }