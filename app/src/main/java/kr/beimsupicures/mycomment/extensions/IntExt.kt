package kr.beimsupicures.mycomment.extensions

import android.content.res.Resources
import java.text.DecimalFormat

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.currencyValue: String
    get() = DecimalFormat("#,##0").format(this)

val Int.showTime: String
    get() {
        val hour = this / 3600
        val minute = (this / 60) % 60
        val second = this % 60

        return String.format("%02d:%02d:%02d", hour, minute, second)
    }