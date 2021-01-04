package kr.beimsupicures.mycomment.api.models

data class AdModel(
    val id: Int,
    val location: Location,
    val status: Status,
    val banner_image_url: String,
    val url: String?,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String?
) {
    enum class Status {
        standby, onair
    }

    enum class Location {
        today, talk
    }
}
