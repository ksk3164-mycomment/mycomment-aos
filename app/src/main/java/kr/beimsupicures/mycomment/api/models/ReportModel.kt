package kr.beimsupicures.mycomment.api.models

data class ReportModel(

    val id: Int,
    val category: Category,
    val category_id: Int,
    val user_id: Int,
    val content: String,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
) {
    enum class Category {
        opinion, comment, komment
    }
}
