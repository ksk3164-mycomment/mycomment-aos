package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeedModel(
    val feed_seq: Int,
    val title: String,
    val c_ts: String,
    val feed_thumbnail: String?,
    val nickname : String,
    val profile_image_url : String,
    val view_cnt : Int,
    val talk_cnt : Int
): Parcelable