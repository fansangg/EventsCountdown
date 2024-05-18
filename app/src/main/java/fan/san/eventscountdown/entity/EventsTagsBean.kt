package fan.san.eventscountdown.entity

data class EventsTagsBean(val contet:String,val isSelected:Boolean,val isNew:Boolean){
    companion object{
        fun getNewTag():EventsTagsBean{
            return EventsTagsBean("", isNew = true, isSelected = false)
        }
    }
}
