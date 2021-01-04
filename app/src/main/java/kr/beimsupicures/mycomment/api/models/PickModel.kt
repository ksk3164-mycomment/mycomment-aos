package kr.beimsupicures.mycomment.api.models

data class PickModel(
    val id: Int,
    val category: Category,
    val category_id: Int,
    val category_owner_id: Int?,
    val user_id: Int,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
) {
    enum class Category {
        agenda, opinion, user, talk, watch, comment, komment
    }
}

fun PickModel.pick(): Boolean {
    return deleted_at == null
}
