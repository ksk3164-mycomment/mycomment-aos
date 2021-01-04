package kr.beimsupicures.mycomment.api.models

data class TermModel(

    val id: Int,
    val category: Category,
    val content: String,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
) {
    enum class Category {
        guide, service, privacy, company
    }
}
