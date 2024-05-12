package fan.san.eventscountdown.db

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun colorToInt(color: Color):Int{
        return color.toArgb()
    }

    @TypeConverter
    fun colorFromInt(color: Int):Color{
        return Color(color)
    }
}