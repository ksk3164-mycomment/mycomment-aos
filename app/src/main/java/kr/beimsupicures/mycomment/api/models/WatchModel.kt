package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser

@Parcelize
data class WatchModel(
    val id: Int,
    val user_id: Int,
    val owner: UserModel?,
    val provider_id: Int,
    var status: Status,
    var title: String,
    var title_image_url: String,
    var content: String,
    var watch_count: Int?,
    var viewer_count: Int,
    var pick: Boolean?,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?

) : Parcelable {

    enum class Status {
        standby, onair
    }
}

val WatchModel.onAir: Boolean
    get() {
        return status == WatchModel.Status.onair
    }

val WatchModel.viewerCount: Int
    get() {
        return if (viewer_count < 0) 0 else viewer_count
    }

val WatchModel.isMe: Boolean
    get() {
        return (user_id == BaseApplication.shared.getSharedPreferences().getUser()?.id)
    }