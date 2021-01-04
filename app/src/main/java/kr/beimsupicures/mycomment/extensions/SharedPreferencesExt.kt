package kr.beimsupicures.mycomment.extensions

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import java.text.SimpleDateFormat

fun SharedPreferences.reset() {
    BaseApplication.shared.getSharedPreferences().edit().clear().commit()
}

fun SharedPreferences.setUser(value: UserModel) {
    BaseApplication.shared.getSharedPreferences().edit().putString("user", Gson().toJson(value))
        .commit()
}

fun SharedPreferences.getUser(): UserModel? {
    BaseApplication.shared.getSharedPreferences().getString("user", null)?.let { value ->
        return Gson().fromJson<UserModel>(value, UserModel::class.java)

    } ?: run {
        return null
    }
}

fun SharedPreferences.setAccessToken(value: String) {
    BaseApplication.shared.getSharedPreferences().edit().putString("accessToken", value).commit()
}

fun SharedPreferences.getAccessToken(): String? {
    return BaseApplication.shared.getSharedPreferences().getString("accessToken", null)
}

fun SharedPreferences.setTalkTime() {
    val value = System.currentTimeMillis() / 1000
    BaseApplication.shared.getSharedPreferences().edit().putLong("talkStartTime", value).commit()
}

fun SharedPreferences.getTalkTime(): Long? {
    return BaseApplication.shared.getSharedPreferences().getLong("talkStartTime", 0)
}

fun SharedPreferences.setWatchTime() {
    val value = System.currentTimeMillis() / 1000
    BaseApplication.shared.getSharedPreferences().edit().putLong("watchStartTime", value).commit()
}

fun SharedPreferences.getWatchTime(): Long? {
    return BaseApplication.shared.getSharedPreferences().getLong("watchStartTime", 0)
}

fun SharedPreferences.setCurrentTalkId(id: Int) {
    BaseApplication.shared.getSharedPreferences().edit().putInt("talkId", id).commit()
}

fun SharedPreferences.getCurrentTalkId(): Int? {
    return BaseApplication.shared.getSharedPreferences().getInt("talkId", -1)
}