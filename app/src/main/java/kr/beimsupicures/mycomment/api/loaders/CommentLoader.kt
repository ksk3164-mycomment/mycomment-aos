package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.CommentModel
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.api.models.ValueModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface CommentService {

    @GET("talk/{talk_id}/comment/count")
    fun getCommentCount(@Path("talk_id") talk_id: Int): Call<APIResult<ValueModel>>

    @GET("talk/{talk_id}/comment/count/total")
    fun getCommentCountTotal(@Path("talk_id") talk_id: Int): Call<APIResult<ValueModel>>

    @GET("talk/{talk_id}/comment")
    fun getCommentList(@Header("Authorization") accessToken: String?, @Path("talk_id") talk_id: Int, @Query("page") page: Int): Call<APIResult<MutableList<CommentModel>>>

    @GET("talk/{talk_id}/comment/{latest_id}/new")
    fun getNewComment(@Header("Authorization") accessToken: String?, @Path("talk_id") talk_id: Int, @Path("latest_id") latest_id: Int): Call<APIResult<MutableList<CommentModel>>>

    @POST("comment/talk")
    @FormUrlEncoded
    fun addTalkComment(@Header("Authorization") accessToken: String?, @Field("talk_id") talk_id: Int, @Field("content") content: String): Call<APIResult<CommentModel>>

    @POST("comment/watch")
    @FormUrlEncoded
    fun addWatchComment(@Header("Authorization") accessToken: String?, @Field("watch_id") watch_id: Int, @Field("content") content: String): Call<APIResult<CommentModel>>

    @DELETE("comment/{id}")
    fun deleteComment(@Header("Authorization") accessToken: String?, @Path("id") id: Int): Call<APIResult<CommentModel>>

    @GET("comment/{id}/talk/pick/users")
    fun getTalkPickedUsers(@Path("id") comment_id: Int): Call<APIResult<MutableList<UserModel>>>

    @GET("comment/{id}/watch/pick/users")
    fun getWatchPickedUsers(@Path("id") comment_id: Int): Call<APIResult<MutableList<UserModel>>>
}

class CommentLoader : BaseLoader<CommentService> {

    var items = mutableListOf<CommentModel>()

    companion object {
        val shared = CommentLoader()
    }

    constructor() {
        api = APIClient.create(CommentService::class.java)
    }

    override fun reset() {
        super.reset()
        items.clear()
    }

    // 댓글수 조회
    fun getCommentCount(talk_id: Int, completionHandler: (Int) -> Unit) {
        api.getCommentCount(talk_id)
            .enqueue(object : Callback<APIResult<ValueModel>> {
                override fun onFailure(call: Call<APIResult<ValueModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<ValueModel>>,
                    response: Response<APIResult<ValueModel>>
                ) {
                    val value = response.body()?.result
                    value?.let { completionHandler(value.count) }
                }

            })
    }

    // 댓글수 조회 (삭제글 포함)
    fun getCommentCountTotal(talk_id: Int, completionHandler: (Int) -> Unit) {
        api.getCommentCountTotal(talk_id)
            .enqueue(object : Callback<APIResult<ValueModel>> {
                override fun onFailure(call: Call<APIResult<ValueModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<ValueModel>>,
                    response: Response<APIResult<ValueModel>>
                ) {
                    val value = response.body()?.result
                    value?.let { completionHandler(value.count) }
                }

            })
    }

    // 댓글 목록 조회
    fun getCommentList(
        talk_id: Int,
        reset: Boolean,
        completionHandler: (MutableList<CommentModel>) -> Unit
    ) {
        if (reset) {
            this.reset()
        }

        if (available()) {
            isLoading = true
            api.getCommentList(APIClient.accessToken, talk_id, this.page)
                .enqueue(object : Callback<APIResult<MutableList<CommentModel>>> {
                    override fun onFailure(
                        call: Call<APIResult<MutableList<CommentModel>>>,
                        t: Throwable
                    ) {
                        isLoading = false
                    }

                    override fun onResponse(
                        call: Call<APIResult<MutableList<CommentModel>>>,
                        response: Response<APIResult<MutableList<CommentModel>>>
                    ) {
                        val newValue = response.body()?.result
                        newValue?.let { newValue ->
                            items.addAll(newValue)
                            page += 1
                            isLoading = false
                            isLast = (newValue.size == 0)

                            completionHandler(items)
                        }
                    }
                })
        }
    }

    // 새댓글 목록 조회
    fun getNewComment(talk_id: Int, latest_id: Int, completionHandler: (MutableList<CommentModel>) -> Unit) {

        if (!isLoading) {
            isLoading = true

            api.getNewComment(APIClient.accessToken, talk_id, latest_id)
                .enqueue(object : Callback<APIResult<MutableList<CommentModel>>> {
                    override fun onFailure(call: Call<APIResult<MutableList<CommentModel>>>, t: Throwable) {
                        isLoading = false
                        completionHandler(mutableListOf())
                    }

                    override fun onResponse(
                        call: Call<APIResult<MutableList<CommentModel>>>,
                        response: Response<APIResult<MutableList<CommentModel>>>
                    ) {
                        isLoading = false
                        val comment = response.body()?.result
                        comment?.let {
                            completionHandler(it)
                        }
                    }

                })

        } else {
            completionHandler(mutableListOf())
        }
    }

    // 댓글 작성하기
    fun addTalkComment(talk_id: Int, content: String, completionHandler: (CommentModel) -> Unit) {
        api.addTalkComment(APIClient.accessToken, talk_id, content)
            .enqueue(object : Callback<APIResult<CommentModel>> {
                override fun onFailure(call: Call<APIResult<CommentModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<CommentModel>>,
                    response: Response<APIResult<CommentModel>>
                ) {
                    val comment = response.body()?.result
                    comment?.let { completionHandler(it) }
                }

            })
    }

    // 댓글 작성하기
    fun addWatchComment(watch_id: Int, content: String, completionHandler: (CommentModel) -> Unit) {
        api.addWatchComment(APIClient.accessToken, watch_id, content)
            .enqueue(object : Callback<APIResult<CommentModel>> {
                override fun onFailure(call: Call<APIResult<CommentModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<CommentModel>>,
                    response: Response<APIResult<CommentModel>>
                ) {
                    val comment = response.body()?.result
                    comment?.let { completionHandler(it) }
                }

            })
    }

    // 댓글 삭제하기
    fun deleteComment(id: Int, completionHandler: (CommentModel) -> Unit) {
        api.deleteComment(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<CommentModel>> {
                override fun onFailure(call: Call<APIResult<CommentModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<CommentModel>>,
                    response: Response<APIResult<CommentModel>>
                ) {
                    val comment = response.body()?.result
                    comment?.let { completionHandler(it) }
                }

            })
    }

    fun getTalkPickedUsers(id: Int, completionHandler: (MutableList<UserModel>) -> Unit) {
        api.getTalkPickedUsers(id)
            .enqueue(object : Callback<APIResult<MutableList<UserModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<UserModel>>>, t: Throwable) { }

                override fun onResponse(
                    call: Call<APIResult<MutableList<UserModel>>>,
                    response: Response<APIResult<MutableList<UserModel>>>
                ) {
                    val value = response.body()?.result
                    value?.let { completionHandler(value) }
                }

            })
    }

    fun getWatchPickedUsers(id: Int, completionHandler: (MutableList<UserModel>) -> Unit) {
        api.getWatchPickedUsers(id)
            .enqueue(object : Callback<APIResult<MutableList<UserModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<UserModel>>>, t: Throwable) { }

                override fun onResponse(
                    call: Call<APIResult<MutableList<UserModel>>>,
                    response: Response<APIResult<MutableList<UserModel>>>
                ) {
                    val value = response.body()?.result
                    value?.let { completionHandler(value) }
                }

            })
    }
}
