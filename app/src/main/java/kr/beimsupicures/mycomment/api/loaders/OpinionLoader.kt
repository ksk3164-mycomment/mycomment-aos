package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.OpinionModel
import kr.beimsupicures.mycomment.api.models.ReplyModel
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.api.models.ValueModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface OpinionService {
    @GET("opinion/{id}")
    fun getOpinion(@Path("id") id: Int): Call<APIResult<OpinionModel>>

    @GET("agenda/{id}/opinion")
    fun getOpinionList(@Header("Authorization") accessToken: String?, @Path("id") id: Int): Call<APIResult<List<OpinionModel>>>

    @GET("opinion/{id}/reply")
    fun getReplyList(@Path("id") id: Int): Call<APIResult<List<ReplyModel>>>

    @GET("agenda/{id}/opinion/rank")
    fun getOpinionRankList(@Path("id") id: Int): Call<APIResult<List<OpinionModel>>>

    @GET("agenda/{id}/opinion/rank/user")
    fun getOpinionUserRankList(@Path("id") id: Int): Call<APIResult<List<UserModel>>>

    @GET("opinion/{id}/count")
    fun getCommentCount(@Path("id") id: Int): Call<APIResult<ValueModel>>

    @GET("opinion/unread")
    fun getUnreadCount(@Header("Authorization") accessToken: String?): Call<APIResult<List<OpinionModel>>>

    @POST("opinion")
    @FormUrlEncoded
    fun addOpinion(
        @Header("Authorization") accessToken: String?, @Field("agenda_id") agenda_id: Int, @Field(
            "content"
        ) content: String
    ): Call<APIResult<OpinionModel?>>

    @POST("opinion/duplicate")
    @FormUrlEncoded
    fun duplicate(
        @Header("Authorization") accessToken: String?, @Field("agenda_id") agenda_id: Int
    ): Call<APIResult<Boolean>>

    @POST("opinion/reply")
    @FormUrlEncoded
    fun addReply(
        @Header("Authorization") accessToken: String?, @Field("agenda_id") agenda_id: Int, @Field("opinion_id") opinion_id: Int, @Field(
            "content"
        ) content: String
    ): Call<APIResult<OpinionModel>>

    @POST("opinion/read")
    @FormUrlEncoded
    fun markAsRead(@Header("Authorization") accessToken: String?, @Field("opinion_id") opinion_id: Int): Call<APIResult<OpinionModel>>

    @DELETE("opinion/{id}")
    fun deleteOpinion(@Header("Authorization") accessToken: String?, @Path("id") id: Int): Call<APIResult<OpinionModel>>
}

class OpinionLoader : BaseLoader<OpinionService> {

    companion object {
        val shared = OpinionLoader()
    }

    constructor() {
        api = APIClient.create(OpinionService::class.java)
    }

    fun getOpinion(id: Int, completionHandler: (OpinionModel) -> Unit) {
        api.getOpinion(id)
            .enqueue(object : Callback<APIResult<OpinionModel>> {
                override fun onFailure(call: Call<APIResult<OpinionModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<OpinionModel>>,
                    response: Response<APIResult<OpinionModel>>
                ) {
                    val opinion = response.body()?.result
                    opinion?.let { completionHandler(it) }
                }
            })
    }

    fun getOpinionList(id: Int, completionHandler: (List<OpinionModel>) -> Unit) {
        api.getOpinionList(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<List<OpinionModel>>> {
                override fun onFailure(
                    call: Call<APIResult<List<OpinionModel>>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<OpinionModel>>>,
                    response: Response<APIResult<List<OpinionModel>>>
                ) {
                    val users = response.body()?.result
                    users?.let { completionHandler(it) }
                }

            })

    }

    fun getReplyList(id: Int, completionHandler: (List<ReplyModel>) -> Unit) {
        api.getReplyList(id)
            .enqueue(object : Callback<APIResult<List<ReplyModel>>> {
                override fun onFailure(call: Call<APIResult<List<ReplyModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<ReplyModel>>>,
                    response: Response<APIResult<List<ReplyModel>>>
                ) {
                    val users = response.body()?.result
                    users?.let { completionHandler(it) }
                }

            })
    }

    fun getOpinionRankList(id: Int, completionHandler: (List<OpinionModel>) -> Unit) {
        api.getOpinionRankList(id)
            .enqueue(object : Callback<APIResult<List<OpinionModel>>> {
                override fun onFailure(call: Call<APIResult<List<OpinionModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<OpinionModel>>>,
                    response: Response<APIResult<List<OpinionModel>>>
                ) {
                    val users = response.body()?.result
                    users?.let { completionHandler(it) }
                }

            })
    }

    fun getOpinionUserRankList(id: Int, completionHandler: (List<UserModel>) -> Unit) {
        api.getOpinionUserRankList(id)
            .enqueue(object : Callback<APIResult<List<UserModel>>> {
                override fun onFailure(call: Call<APIResult<List<UserModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<UserModel>>>,
                    response: Response<APIResult<List<UserModel>>>
                ) {
                    val users = response.body()?.result
                    users?.let { completionHandler(it) }
                }

            })
    }

    // 대댓글 갯수 조회
    fun getCommentCount(id: Int, completionHandler: (Int) -> Unit) {
        api.getCommentCount(id)
            .enqueue(object : Callback<APIResult<ValueModel>> {
                override fun onFailure(call: Call<APIResult<ValueModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<ValueModel>>,
                    response: Response<APIResult<ValueModel>>
                ) {
                    val value = response.body()?.result
                    value?.let { completionHandler(it.count) }
                }

            })
    }

    // 안읽은 댓글이 존재하는 오피니언 목록 조회
    fun getUnreadCount(completionHandler: (List<OpinionModel>) -> Unit) {
        api.getUnreadCount(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<List<OpinionModel>>> {
                override fun onFailure(
                    call: Call<APIResult<List<OpinionModel>>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<OpinionModel>>>,
                    response: Response<APIResult<List<OpinionModel>>>
                ) {
                    val users = response.body()?.result
                    users?.let { completionHandler(it) }
                }

            })
    }

    fun addOpinion(agendaId: Int, content: String, completionHandler: (OpinionModel?) -> Unit) {
        api.addOpinion(APIClient.accessToken, agendaId, content)
            .enqueue(object : Callback<APIResult<OpinionModel?>> {
                override fun onFailure(call: Call<APIResult<OpinionModel?>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<OpinionModel?>>,
                    response: Response<APIResult<OpinionModel?>>
                ) {

                    when (response.isSuccessful) {

                        true -> {
                            val opinion = response.body()?.result
                            opinion?.let { completionHandler(it) }
                        }

                        false -> {

                        }
                    }
                }
            })
    }

    fun duplicate(agendaId: Int, completionHandler: (Boolean) -> Unit) {
        api.duplicate(APIClient.accessToken, agendaId)
            .enqueue(object : Callback<APIResult<Boolean>> {
                override fun onFailure(call: Call<APIResult<Boolean>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<Boolean>>,
                    response: Response<APIResult<Boolean>>
                ) {

                    val duplicate = response.body()?.result
                    duplicate?.let { completionHandler(it) }
                }
            })
    }

    // 대댓글 읽음 표시
    fun markAsRead(id: Int, completionHandler: (OpinionModel) -> Unit) {
        api.markAsRead(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<OpinionModel>> {
                override fun onFailure(call: Call<APIResult<OpinionModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<OpinionModel>>,
                    response: Response<APIResult<OpinionModel>>
                ) {
                    val opinion = response.body()?.result
                    opinion?.let { completionHandler(it) }
                }
            })
    }

    fun addReply(
        agendaId: Int,
        opinionId: Int,
        content: String,
        completionHandler: (OpinionModel) -> Unit
    ) {
        api.addReply(APIClient.accessToken, agendaId, opinionId, content)
            .enqueue(object : Callback<APIResult<OpinionModel>> {
                override fun onFailure(call: Call<APIResult<OpinionModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<OpinionModel>>,
                    response: Response<APIResult<OpinionModel>>
                ) {

                    val opinion = response.body()?.result
                    opinion?.let { completionHandler(it) }
                }
            })
    }

    fun deleteOpinion(id: Int, completionHandler: (OpinionModel) -> Unit) {
        api.deleteOpinion(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<OpinionModel>> {
                override fun onFailure(call: Call<APIResult<OpinionModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<OpinionModel>>,
                    response: Response<APIResult<OpinionModel>>
                ) {

                    val opinion = response.body()?.result
                    opinion?.let { completionHandler(it) }
                }
            })
    }
}
