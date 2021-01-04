package kr.beimsupicures.mycomment.api.models

data class ProviderModel(
    val id: Int = 0,
    val name: String,
    val created_at: String = "",
    val updated_at: String? = null,
    val deleted_at: String? = null
)
