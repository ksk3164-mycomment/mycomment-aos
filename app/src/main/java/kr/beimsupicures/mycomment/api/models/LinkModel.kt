package kr.beimsupicures.mycomment.api.models

data class LinkModel(
    val id: Int,
    val agenda_id: Int,
    val content: String,
    var count: Int,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
)
