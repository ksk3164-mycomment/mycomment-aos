package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SimpleResultModel(
    var success: String
) : Parcelable