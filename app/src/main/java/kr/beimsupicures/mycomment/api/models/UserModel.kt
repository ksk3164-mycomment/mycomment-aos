package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kr.beimsupicures.mycomment.common.iamport.IAMPortCertificationResult
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser

@Parcelize
data class UserModel(
    val id: Int,
    val unique_key: String?,
    val sns: SocialProvider,
    val nickname: String,
    val name: String?,
    val title: String?,  // 칭호
    val birth: String?,
    val password: String,
    val phone: String?,
    val profile_image_url: String?,
    val profile_badge_image_url: String?,
    val gender: IAMPortCertificationResult.Gender?,
    val job: String?,
    val intro: String?,
    var pick_count: Int,
    var pick: Boolean?,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?,
    val banned_at: String?

) : Parcelable {

    enum class SocialProvider {
        apple, facebook, google, kakao, none
    }
}

fun UserModel.nameOnly(): String? {

    name?.let { name ->
        when (name.length) {
            0 -> {
            }
            1 -> return name.substring(0, 1)
            else -> return name.substring(1, name.length)
        }
    }

    return "-"
}

fun UserModel.isMe(): Boolean {
    BaseApplication.shared.getSharedPreferences().getUser()?.let { user ->
        return user.id == this.id
    } ?: run {
        return false
    }
}