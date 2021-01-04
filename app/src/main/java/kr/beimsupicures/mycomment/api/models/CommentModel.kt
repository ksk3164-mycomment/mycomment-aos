package kr.beimsupicures.mycomment.api.models

import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser

data class CommentModel(
    val id: Int,
    val talk_id: Int,
    val user_id: Int,
    val owner: UserModel,
    val content: String,
    var pick: Boolean?,
    var pick_count: Int,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
)

val CommentModel.isMe: Boolean
    get() {
        return (user_id == BaseApplication.shared.getSharedPreferences().getUser()?.id)
    }