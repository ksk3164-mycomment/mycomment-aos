package kr.beimsupicures.mycomment.viewmodels.signs

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kr.beimsupicures.mycomment.api.models.UserModel

@Parcelize
data class SignStep1ViewModel(
    val email: String,
    val nickname: String,
    val type: UserModel.SocialProvider
) : Parcelable