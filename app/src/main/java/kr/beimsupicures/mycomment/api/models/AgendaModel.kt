package kr.beimsupicures.mycomment.api.models

data class AgendaModel(
    val id: Int,
    val date: String,
    val category: String,
    val title: String,
    val content: String?,
    var pick_count: Int,
    var pick: Boolean?,
    var view_count: Int,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
) {
    data class SuggestModel(
        val id: Int,
        val user_id: Int?,
        val agenda: String,
        val created_at: String,
        val updated_at: String?,
        val deleted_at: String?
    )
}