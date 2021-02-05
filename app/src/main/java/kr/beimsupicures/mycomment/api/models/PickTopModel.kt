package kr.beimsupicures.mycomment.api.models

data class PickTopModel(
    val category: String,
    var category_owner_id: Int,
    val score: Int,
    val nickname: String?,
    val profile_image_url: String?
)