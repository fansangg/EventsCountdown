package fan.san.eventscountdown.page

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.stringPreferencesKey
import fan.san.eventscountdown.common.CommonScaffold
import fan.san.eventscountdown.common.dataStore
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogPage() {
    val logs = stringPreferencesKey("logs")
    val logsState =
        LocalContext.current.dataStore.data.map { it[logs] ?: "" }.collectAsState(initial = "")
    Log.d("fansangg", "#LogPage: logsState == ${logsState.value}")
    val scrollState = rememberScrollState()
    CommonScaffold(title = "logs") {
        if (logsState.value.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(scrollState)
            ) {
                Text(text = logsState.value, modifier = Modifier.fillMaxSize(), color = Color.White)
            }
        }
    }

}