package kr.beimsupicures.mycomment.extensions

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import kr.beimsupicures.mycomment.components.application.BaseApplication

fun Application.getSharedPreferences(): SharedPreferences {
    return BaseApplication.shared.getSharedPreferences(
        "default",
        Context.MODE_PRIVATE
    )
}
