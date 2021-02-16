package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class TalkModel(
    val id: Int,
    val provider_id: Int,
    val otts : String,
    val category_id: Int,
    val title: String,
    val title_image_url: String,
    val live: Int,
    val poster_image_url:String,
    val content: String,
    val content_image_url: String,
    val open_day: String,
    val open_time: String,
    val close_time: String,
    val bookmark_count: Int,
    val talk_count: Int?,
    var pick: Boolean?,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?

) : Parcelable {
    enum class Weekday(val value: String) {
        mon("월"), tue("화"), wed("수"), thu("목"), fri("금"), sat("토"), sun("일")
    }
}

val TalkModel.dayString: String
    get() {
        return open_day.replace("|", "")
    }

val TalkModel.openTimeString: String
    get() {
        val hour = open_time.split(":").first().toInt()
        val minute = open_time.split(":").last().toInt()

        val a = if ((0 < hour) && (hour < 12)) "오전" else "오후"
        return "${a} ${hour}시 ${String.format("%02d", minute)}분"
    }

val TalkModel.onAir: Boolean
    get() {
        var today = false
        val now: Int = SimpleDateFormat("HHmm").format(Date()).toInt()

        val day = Calendar.getInstance()[Calendar.DAY_OF_WEEK]

        for (value in open_day.split("|")) {
            if (!today) {
                when (value) {
                    TalkModel.Weekday.sun.value -> {
                        today = (1 == day)
                    }
                    TalkModel.Weekday.mon.value -> {
                        today = (2 == day)
                    }
                    TalkModel.Weekday.tue.value -> {
                        today = (3 == day)
                    }
                    TalkModel.Weekday.wed.value -> {
                        today = (4 == day)
                    }
                    TalkModel.Weekday.thu.value -> {
                        today = (5 == day)
                    }
                    TalkModel.Weekday.fri.value -> {
                        today = (6 == day)
                    }
                    TalkModel.Weekday.sat.value -> {
                        today = (7 == day)
                    }
                }
            }
        }

        if (today) {
            return (open_time.replace(":", "").toInt() <= now) && (now <= close_time.replace(
                ":",
                ""
            ).toInt())
        }

        return false
    }
