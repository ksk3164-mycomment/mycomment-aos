package kr.beimsupicures.mycomment.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Long.toString(format: String): String {

    val date = SimpleDateFormat(format)
    date.timeZone = TimeZone.getTimeZone("Asia/Seoul")

    return date.format(Date(if (this  < 1000000000000L) (this * 1000L) else (this)))
}
fun Long.currentTimeMillis(): Long{
    return System.currentTimeMillis()
}