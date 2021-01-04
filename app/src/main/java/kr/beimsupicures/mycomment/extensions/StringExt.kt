package kr.beimsupicures.mycomment.extensions

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import java.text.SimpleDateFormat
import java.util.*

fun String.timeline(): String {

    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    format.timeZone = TimeZone.getTimeZone("GMT")

    var time = format.parse(this).time
    val now = Calendar.getInstance().time.time

    if (time < 1000000000000L) {
        time *= 1000
    }

    if (time > now || time <= 0) {
        return ""
    }

    val diff: Long = now - time

    return when {
        diff < 3 * 1000L -> "방금"
        diff < 60 * 1000L -> "${diff / 1000L}초 전"
        diff < 2 * 60 * 1000L -> "1분 전"
        diff < 60 * 60 * 1000L -> "${diff / (60 * 1000L)} 분 전"
        diff < 2 * 60 * 60 * 1000L -> "1시간 전"
        diff < 24 * 60 * 60 * 1000L -> "${diff / (60 * 60 * 1000L)} 시간 전"
        diff < 2 * 24 * 60 * 60 * 1000L -> "1일 전"
        diff < 7 * 24 * 60 * 60 * 1000L -> "${diff / (24 * 60 * 60 * 1000L)}일 전"
        diff < 2 * 7 * 24 * 60 * 60 * 1000L -> "1주 전"
        diff < 30 * 24 * 60 * 60 * 1000L -> "${diff / (7 * 24 * 60 * 60 * 1000L)}주 전"
        diff < 60 * 24 * 60 * 60 * 1000L -> "1개월 전"
        diff < 365 * 24 * 60 * 60 * 1000L -> "${diff / (30 * 24 * 60 * 60 * 1000L)}개월 전"
        diff < 2 * 365 * 24 * 60 * 60 * 1000L -> "1년 전"
        else -> "${diff / (365 * 24 * 60 * 60 * 1000L)}년 전"
    }
}

fun String.contraction20(): String {
    val count = 20
    if (this.length < count) { return this }
    return "${this.substring(0, count)}···"
}

fun String.getReplyInfo(): Pair<String, String>? {
    val split = this.split("\n")
    if (split.isNotEmpty() && split[0].isNotEmpty()) {
        val reply = split[0]

        if (reply[0] == '@') {
            val split2 = reply.split("|")
            if (split2.size == 2) {
                val tag = split2[0]
                val origin = split2[1]
                return Pair(tag, origin)
            }
        }
    }
    return null
}

fun String.originalString(): String {
    var result = ""
    val splits = this.split("\n")
    for (line in splits) {
        if (line.getReplyInfo() != null) {
            continue
        }
        result += "$line "
    }
    return result.contraction20()
}

fun String.removeLastEmpty(): String {
    if (this[this.length-1] == ' ') {
        return this.substring(0, this.length-1)
    }
    return this
}

fun String.parseNickname(): String {
    if (this[0] == '@') {
        return this.substring(1, this.length).removeLastEmpty()
    } else {
        return this
    }
}

fun String.makeRepliedMessage(tag: String, origin: String, color: Int): SpannableString {
    val start1 = this.indexOf(tag)
    val end1 = start1 + tag.length
    val start2 = this.indexOf(origin)
    val end2 = start2 + origin.length
    val spannableString = SpannableString(this)
    spannableString.setSpan(ForegroundColorSpan(color), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(ForegroundColorSpan(color), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(StyleSpan(Typeface.BOLD), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannableString
}