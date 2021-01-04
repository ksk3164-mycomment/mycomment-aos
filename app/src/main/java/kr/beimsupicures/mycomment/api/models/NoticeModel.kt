package kr.beimsupicures.mycomment.api.models

data class NoticeModel(
    val id: Int,
    val status: Status,
    val content: String,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
) {
    enum class Status {
        standby, onair
    }
}
