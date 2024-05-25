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
    private val serializer: KSerializer<T>,
):NavType<List<T>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): List<T>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelableArrayList(key, clazz)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelableArrayList(key)
        }


    override fun parseValue(value: String): List<T>  = Json.decodeFromString(ListSerializer(serializer),value)

    override fun serializeAsValue(value: List<T>): String = Json.encodeToString(ListSerializer(serializer),value)

    override fun put(bundle: Bundle, key: String, value: List<T>) {
            bundle.putParcelableArrayList(key,value as ArrayList)
    }

}