package kr.beimsupicures.mycomment.api.models

data class CategoryModel(
    val id: Int,
    val name: String,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
)
