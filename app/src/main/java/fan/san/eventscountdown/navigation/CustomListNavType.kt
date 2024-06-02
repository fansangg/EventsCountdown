package fan.san.eventscountdown.navigation

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class CustomListNavType<T:Parcelable>(
    private val clazz: Class<T>,
    private val serializer: KSerializer<T>
):NavType<ArrayList<T>>(isNullableAllowed = false) {

    private val json = Json { encodeDefaults = true }
    override fun get(bundle: Bundle, key: String): ArrayList<T>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelableArrayList(key, clazz)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelableArrayList(key)
        }


    override fun parseValue(value: String): ArrayList<T>  = json.decodeFromString(value)

    override fun serializeAsValue(value: ArrayList<T>): String = json.encodeToString(ListSerializer(serializer),value)

    override fun put(bundle: Bundle, key: String, value: ArrayList<T>) {
            bundle.putParcelableArrayList(key, value)
    }

}