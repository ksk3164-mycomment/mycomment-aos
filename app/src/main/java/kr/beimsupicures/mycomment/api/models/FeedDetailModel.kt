package kr.beimsupicures.mycomment.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class FeedDetailModel(
    var feed_seq : Int,
    var type: String?,
var talk_id : Int,
var user_id: Int,
var title : String?,
var imgs : String?,
var content: String?,
var c_ts: String?,
var m_ts : String?
)