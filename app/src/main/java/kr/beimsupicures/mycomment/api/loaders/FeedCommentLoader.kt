package kr.beimsupicures.mycomment.api.loaders

import android.util.Log
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

interface FeedCommentService{
    //feed comment

    @GET("feed/{feed_seq}/comment/count")
    fun getFeedCommentCount(@Path("feed_seq") feed_seq: Int): Call<APIResult<ValueModel>>

    @GET("feed/{feed_seq}/comment")
    fun getFeedCommentList(
        @Header("Authorization") accessToken: String?,
        @Path("feed_seq") feed_seq: Int,
        @Query("page") page: Int
    ): Call<APIResult<MutableList<CommentModel>>>

    @GET("feed/{feed_seq}/comment/{latest_id}/new")
    fun getNewFeedComment(
        @Header("Authorization") accessToken: String?,
        @Path("feed_seq") feed_seq: Int,
        @Path("latest_id") latest_id: Int
    ): Call<APIResult<MutableList<CommentModel>>>

    @POST("comment/feed")
    @FormUrlEncoded
    fun addFeedComment(
        @Header("Authorization") accessToken: String?,
        @Field("feed_seq") feed_seq: Int,
        @Field("content") content: String
    ): Call<APIResult<CommentModel>>

    @DELETE("comment/feed/{id}")
    fun deleteFeedComment(
        @Header("Authorization") accessToken: String?,
        @Path("id") id: Int
    ): Call<APIResult<CommentModel>>

    @GET("comment/{id}/feed/pick/users")
    fun getFeedPickedUsers(@Path("id") comment_id: Int): Call<APIResult<MutableList<UserModel>>>

}


class FeedCommentLoader :BaseLoader<FeedCommentService>{

    var items = mutableListOf<CommentModel>()

    companion object {
        val shared = FeedCommentLoader()
    }

    constructor() {
        api = APIClient.create(FeedCommentService::class.java)
    }

    override fun reset() {
        super.reset()
        items.clear()
    }

    fun getFeedCommentCount(feed_seq: Int, completionHandler: (Int) -> Unit) {
        api.getFeedCommentCount(feed_seq)
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
    fun getNewFeedComment(
        feed_seq: Int,
        latest_id: Int,
        completionHandler: (MutableList<CommentModel>) -> Unit
    ) {

        if (!isLoading) {
            isLoading = true

            api.getNewFeedComment(APIClient.accessToken, feed_seq, latest_id)
                .enqueue(object : Callback<APIResult<MutableList<CommentModel>>> {
                    override fun onFailure(
                        call: Call<APIResult<MutableList<CommentModel>>>,
                        t: Throwable
                    ) {
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

    fun deleteFeedComment(id: Int, completionHandler: (CommentModel) -> Unit) {
        api.deleteFeedComment(APIClient.accessToken, id)
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


    fun addFeedComment(feed_seq: Int, content: String, completionHandler: (CommentModel) -> Unit) {
        api.addFeedComment(APIClient.accessToken, feed_seq, content)
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


    //코멘트 댓글 조회
    fun getFeedCommentList(
        feed_seq: Int,
        reset: Boolean,
        completionHandler: (MutableList<CommentModel>) -> Unit
    ) {
        if (reset) {
            this.reset()
        }

        if (available()) {
            isLoading = true
            api.getFeedCommentList(APIClient.accessToken, feed_seq, this.page)
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

                            items.addAll(newValue.distinct())
                            page += 1
                            isLoading = false
                            isLast = (newValue.size == 0)

                            completionHandler(items)
                        }
                    }
                })
        }
    }
    fun getFeedPickedUsers(id: Int, completionHandler: (MutableList<UserModel>) -> Unit) {
        api.getFeedPickedUsers(id)
            .enqueue(object : Callback<APIResult<MutableList<UserModel>>> {
                override fun onFailure(
                    call: Call<APIResult<MutableList<UserModel>>>,
                    t: Throwable
                ) {
                }

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
