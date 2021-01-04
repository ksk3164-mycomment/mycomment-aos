package kr.beimsupicures.mycomment.api.models

import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser

data class OpinionModel(
    val id: Int,
    val agenda_id: Int,
    val opinion_id: Int?,
    val user_id: Int,
    val content: String,
    var pick_count: Int,
    var pick: Boolean?,
    var read_at: String?,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
)

typealias ReplyModel = OpinionModel

fun OpinionModel.isMe(): Boolean {
    return BaseApplication.shared.getSharedPreferences().getUser()?.id == user_id
}