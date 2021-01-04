package kr.beimsupicures.mycomment.api.models

data class ReplayModel(
    val replayInfo: ReplayInfo,
    val comments: MutableList<KommentModel>
)

data class ReplayInfo(val id: Int, val watch_id: Int, val talk_id: Int, val start_time: String, val end_time: String)