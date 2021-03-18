package kr.beimsupicures.mycomment.api.loaders

import android.util.Log
import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.*
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface FeedService {

    @GET("feed/list/{talk_id}/{page}")
    fun getFeedList(
        @Header("Authorization") accessToken: String?,
        @Path("talk_id") talk_id: Int,
        @Path("page") page: Int
    ): Call<APIResult<MutableList<FeedModel>>>

    @GET("/feed/count/{talk_id}")
    fun getFeedCount(@Path("talk_id") talk_id: Int): Call<APIResult<ValueModel>>

    @GET("feed/detail/{feed_seq}")
    fun getFeedDetail(@Path("feed_seq") feed_seq: Int): Call<APIResult<FeedDetailModel>>

    @DELETE("feed/delete/{feed_seq}")
    fun deleteFeedDetail(
        @Header("Authorization") accessToken: String?,
        @Path("feed_seq") feed_seq: Int
    ): Call<APIResult<UpdatedModel>>

    //현재는 사용안함(putfeed로 대체)
    @Multipart
    @POST("feed/post/{talk_id}")
    fun postFeed(
        @Header("Authorization") accessToken: String?,
        @Path("talk_id") talk_id: Int,
//        @PartMap map : Map<String, RequestBody>,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part imgs: ArrayList<MultipartBody.Part>
//        @Field("imgs") imgs: Array<String>?
    ): Call<APIResult<FeedModel>>

    @PUT("feed/new/{feed_seq}")
    @FormUrlEncoded
    fun putFeed(
        @Header("Authorization") accessToken: String?,
        @Path("feed_seq") feed_seq: Int,
        @Field("title") title: String,
        @Field("content") content: String
    ): Call<APIResult<FeedModel>>

    @PUT("feed/edit/{feed_seq}")
    @FormUrlEncoded
    fun editFeed(
        @Header("Authorization") accessToken: String?,
        @Path("feed_seq") feed_seq: Int,
        @Field("title") title: String,
        @Field("content") content: String
    ): Call<APIResult<UpdatedModel>>

}

class FeedLoader : BaseLoader<FeedService> {

    var items = mutableListOf<FeedModel>()

    companion object {
        val shared = FeedLoader()
    }

    constructor() {
        api = APIClient.create(FeedService::class.java)
    }

    override fun reset() {
        super.reset()
        items.clear()
    }

    // 댓글 목록 조회
    fun getFeedList(
        talk_id: Int,
        reset: Boolean,
        page: Int,
        completionHandler: (MutableList<FeedModel>) -> Unit
    ) {
        if (reset) {
            this.reset()
        }

        if (available()) {
            isLoading = true
            api.getFeedList(APIClient.accessToken, talk_id, page)
                .enqueue(object : Callback<APIResult<MutableList<FeedModel>>> {
                    override fun onFailure(
                        call: Call<APIResult<MutableList<FeedModel>>>,
                        t: Throwable
                    ) {
                        isLoading = false
                    }

                    override fun onResponse(
                        call: Call<APIResult<MutableList<FeedModel>>>,
                        response: Response<APIResult<MutableList<FeedModel>>>
                    ) {
                        val newValue = response.body()?.result
                        newValue?.let { newValue ->
                            items.addAll(newValue)
                            isLoading = false
                            isLast = (newValue.size == 0)
                            completionHandler(items)
                        }
                    }
                })
        }
    }

    //피드 상세
    fun getFeedDetail(
        feed_seq: Int,
        completionHandler: (FeedDetailModel) -> Unit
    ) {
        api.getFeedDetail(feed_seq)
            .enqueue(object : Callback<APIResult<FeedDetailModel>> {
                override fun onFailure(
                    call: Call<APIResult<FeedDetailModel>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<FeedDetailModel>>,
                    response: Response<APIResult<FeedDetailModel>>
                ) {
                    val talk = response.body()?.result
                    talk?.let { completionHandler(it) }
                }

            })
    }

    fun deleteFeedDetail(feed_seq: Int, completionHandler: (UpdatedModel) -> Unit) {
        api.deleteFeedDetail(APIClient.accessToken, feed_seq)
            .enqueue(object : Callback<APIResult<UpdatedModel>> {
                override fun onResponse(
                    call: Call<APIResult<UpdatedModel>>,
                    response: Response<APIResult<UpdatedModel>>
                ) {
                    val update = response.body()?.result
                    update?.let { completionHandler(it) }
                }

                override fun onFailure(call: Call<APIResult<UpdatedModel>>, t: Throwable) {

                }

            })
    }

    //피드 개수
    fun getFeedCount(talk_id: Int, completionHandler: (Int) -> Unit) {
        api.getFeedCount(talk_id)
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

    fun postFeed(
        talk_id: Int,
        title: RequestBody,
        content: RequestBody,
        imgs: ArrayList<MultipartBody.Part>,
        completionHandler: (FeedModel) -> Unit
    ) {
        api.postFeed(APIClient.accessToken, talk_id, title, content, imgs)
            .enqueue(object : Callback<APIResult<FeedModel>> {
                override fun onResponse(
                    call: Call<APIResult<FeedModel>>,
                    response: Response<APIResult<FeedModel>>
                ) {
                    val feed_seq = response.body()?.result
                    feed_seq?.let { completionHandler(it) }
                }

                override fun onFailure(call: Call<APIResult<FeedModel>>, t: Throwable) {
                    Log.e("통신실패", t.message)
                }

            })
    }

    fun putFeed(
        feed_seq: Int,
        title: String,
        content: String,
        completionHandler: (FeedModel) -> Unit
    ) {
        api.putFeed(APIClient.accessToken, feed_seq, title, content)
            .enqueue(object : Callback<APIResult<FeedModel>> {
                override fun onResponse(
                    call: Call<APIResult<FeedModel>>,
                    response: Response<APIResult<FeedModel>>
                ) {
                    val feed_seq = response.body()?.result
                    feed_seq?.let { completionHandler(it) }
                }

                override fun onFailure(call: Call<APIResult<FeedModel>>, t: Throwable) {
                    Log.e("통신실패", t.message)
                }

            })
    }
    fun editFeed(
        feed_seq: Int,
        title: String,
        content: String,
        completionHandler: (UpdatedModel) -> Unit
    ) {
        api.editFeed(APIClient.accessToken, feed_seq, title, content)
            .enqueue(object : Callback<APIResult<UpdatedModel>> {
                override fun onResponse(
                    call: Call<APIResult<UpdatedModel>>,
                    response: Response<APIResult<UpdatedModel>>
                ) {
                    val feed_seq = response.body()?.result
                    feed_seq?.let { completionHandler(it) }
                }

                override fun onFailure(call: Call<APIResult<UpdatedModel>>, t: Throwable) {
                    Log.e("통신실패", t.message)
                }

            })
    }
}
