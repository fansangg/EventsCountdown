package fan.san.eventscountdown.db

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverters

@Entity(tableName = "widget_infos")
@TypeConverters(Converters::class)
data class WidgetInfos(
    @PrimaryKey val id: Int,
    var color: Color,
    var colorOption:String = "白色",
    var followSystem:Boolean = false,
    var backgroundImg: String? = null,
){
    @Dao
    interface WidgetInfosDao{

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(widgetInfos: WidgetInfos)

        @Query("SELECT * FROM widget_infos WHERE :id = id")
        fun queryById(id: Int):List<WidgetInfos>

        @Query("DELETE FROM widget_infos WHERE :id = id")
        fun delete(id:Int)
    }
}