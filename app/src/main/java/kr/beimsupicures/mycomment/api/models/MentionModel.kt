package kr.beimsupicures.mycomment.api.models

data class MentionModel(val talk: TalkModel, val mention: MentionInfo)
data class MentionInfo(val id: Int, val comment_id: Int, val mentioned_user_id: Int)