package kr.beimsupicures.mycomment.extensions

import java.util.*

fun weekday(): String{

    var calendar : Calendar  = Calendar.getInstance()
    var days : Array<String> = arrayOf("일","월","화","수","목","금","토")

    return  days[calendar.get(Calendar.DAY_OF_WEEK)-1]

}