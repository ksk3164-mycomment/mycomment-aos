package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface WatchService {

    @GET("watch/category")
    fun getCategoryList(): Call<APIResult<MutableList<CategoryModel>>>

    @GET("watch/provider")
    fun getProviderList(): Call<APIResult<MutableList<ProviderModel>>>

    @GET("watch")
    fun getWatchList(@Header("Authorization") accessToken: String?, @Query("pid") pid: Int): Call<APIResult<MutableList<WatchModel>>>

    @POST("watch/create")
    @FormUrlEncoded
    fun createWatch(
        @Header("Authorization") accessToken: String?,
        @Field("provider_id") provider_id: Int,
        @Field("title") title: String,
        @Field("title_image_url") title_image_url: String,
        @Field("content") content: String
    ): Call<APIResult<WatchModel>>

    @PATCH("watch/{id}")
    @FormUrlEncoded
    fun updateWatch(
        @Header("Authorization") accessToken: String?,
        @Path("id") id: Int,
        @Field("provider_id") provider_id: Int,
        @Field("status") status: String,
        @Field("title") title: String,
        @Field("title_image_url") title_image_url: String,
        @Field("content") content: String
    ): Call<APIResult<WatchModel>>

    @GET("watch/me")
    fun getWatch(@Header("Authorization") accessToken: String?): Call<APIResult<WatchModel?>>

    @GET("watch/{watch_id}/comment")
    fun getCommentList(@Header("Authorization") accessToken: String?,
                       @Path("watch_id") watch_id: Int,
                       @Query("page") page: Int): Call<APIResult<MutableList<KommentModel>>>

    @GET("watch/{watch_id}/comment/count")
    fun getCommentCount(@Path("watch_id") watch_id: Int): Call<APIResult<ValueModel>>

    @GET("watch/{watch_id}/comment/{latest_id}/new")
    fun getNewComment(
        @Header("Authorization") accessToken: String?,
        @Path("watch_id") watch_id: Int,
        @Path("latest_id") latest_id: Int
    ): Call<APIResult<MutableList<KommentModel>>>

    @GET("watch/{watch_id}/comment/count/total")
    fun getCommentCountTotal(@Path("watch_id") watch_id: Int): Call<APIResult<ValueModel>>

    @GET("watch/{watch_id}/comment/replay")
    fun getReplayCommentList(@Header("Authorization") accessToken: String?,
                             @Path("watch_id") watch_id: Int): Call<APIResult<ReplayModel>>

    @DELETE("watch/{watch_id}/comment/{comment_id}")
    fun deleteComment(@Header("Authorization") accessToken: String?,
                      @Path("watch_id") watch_id: Int,
                      @Path("comment_id") comment_id: Int): Call<APIResult<KommentModel>>

    @PATCH("watch/{watch_id}/viewer/reset")
    fun resetViewerCount(@Header("Authorization") accessToken: String?,
                         @Path("watch_id") watch_id: Int): Call<APIResult<WatchModel>>

    @PATCH("watch/{watch_id}/viewer/increase")
    fun increaseViewerCount(@Header("Authorization") accessToken: String?,
                            @Path("watch_id") watch_id: Int): Call<APIResult<WatchModel>>

    @PATCH("watch/{watch_id}/viewer/decrease")
    fun decreaseViewerCount(@Header("Authorization") accessToken: String?,
                            @Path("watch_id") watch_id: Int): Call<APIResult<WatchModel>>

    @PATCH("watch/{watch_id}/view/increase")
    fun increaseViewCount(@Header("Authorization") accessToken: String?,
                          @Path("watch_id") watch_id: Int): Call<APIResult<WatchModel>>

    @PUT("watch/history")
    @FormUrlEncoded
    fun connectWatch(@Header("Authorization") accessToken: String?, @Field("watch_id") watch_id: Int): Call<APIResult<SimpleResultModel>>

    @HTTP(method = "DELETE", path = "watch/{id}/history", hasBody = true)
    fun exitWatch(@Header("Authorization") accessToken: String?, @Path("id") watch_id: Int): Call<APIResult<SimpleResultModel>>

    @PATCH("watch/{id}/history")
    @FormUrlEncoded
    fun exitWatch(@Header("Authorization") accessToken: String?, @Path("id") watch_id: Int, @Field("stay_sec") stay_sec: Int): Call<APIResult<SimpleResultModel>>
}

class WatchLoader : BaseLoader<WatchService> {

    var items: MutableList<KommentModel> = mutableListOf()

    companion object {
        val shared = WatchLoader()
    }

    constructor() {
        api = APIClient.create(WatchService::class.java)
    }

    override fun reset() {
        super.reset()
        items.clear()
    }

    // 카테고리 조회
    fun getCategoryList(completionHandler: (MutableList<CategoryModel>) -> Unit) {
        api.getCategoryList()
            .enqueue(object : Callback<APIResult<MutableList<CategoryModel>>> {
                override fun onFailure(
                    call: Call<APIResult<MutableList<CategoryModel>>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<CategoryModel>>>,
                    response: Response<APIResult<MutableList<CategoryModel>>>
                ) {
                    val talk = response.body()?.result
                    talk?.let { completionHandler(it) }
                }

            })
    }

    // 방송사 목록 조회
    fun getProviderList(completionHandler: (MutableList<ProviderModel>) -> Unit) {
        api.getProviderList()
            .enqueue(object : Callback<APIResult<MutableList<ProviderModel>>> {
                override fun onFailure(
                    call: Call<APIResult<MutableList<ProviderModel>>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<ProviderModel>>>,
                    response: Response<APIResult<MutableList<ProviderModel>>>
                ) {
                    val list = response.body()?.result
                    list?.let { list ->
                        completionHandler(list)
                    }
                }
            })
    }

    // 방송사별 방 목록 조회
    fun getWatchList(provider_id: Int, completionHandler: (MutableList<WatchModel>) -> Unit) {
        api.getWatchList(APIClient.accessToken, provider_id)
            .enqueue(object : Callback<APIResult<MutableList<WatchModel>>> {
                override fun onFailure(
                    call: Call<APIResult<MutableList<WatchModel>>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<WatchModel>>>,
                    response: Response<APIResult<MutableList<WatchModel>>>
                ) {
                    val list = response.body()?.result
                    list?.let { list ->
                        completionHandler(list)
                    }
                }
            })
    }

    // 방만들기
    fun createWatch(
        provider_id: Int,
        title: String,
        title_image_url: String,
        content: String,
        completionHandler: (WatchModel) -> Unit
    ) {
        api.createWatch(APIClient.accessToken, provider_id, title, title_image_url, content)
            .enqueue(object : Callback<APIResult<WatchModel>> {
                override fun onFailure(
                    call: Call<APIResult<WatchModel>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<WatchModel>>,
                    response: Response<APIResult<WatchModel>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }
            })
    }

    // 내가 만든 방 업데이트
    fun updateWatch(
        id: Int,
        provider_id: Int,
        status: WatchModel.Status,
        title: String,
        title_image_url: String,
        content: String,
        completionHandler: (WatchModel) -> Unit
    ) {
        api.updateWatch(
            APIClient.accessToken,
            id,
            provider_id,
            status.toString(),
            title,
            title_image_url,
            content
        )
            .enqueue(object : Callback<APIResult<WatchModel>> {
                override fun onFailure(
                    call: Call<APIResult<WatchModel>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<WatchModel>>,
                    response: Response<APIResult<WatchModel>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }
            })
    }

    // 내가 만든 방 조회
    fun getWatch(completionHandler: (WatchModel?) -> Unit) {
        api.getWatch(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<WatchModel?>> {
                override fun onFailure(
                    call: Call<APIResult<WatchModel?>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<WatchModel?>>,
                    response: Response<APIResult<WatchModel?>>
                ) {
                    val newValue = response.body()?.result
                    completionHandler(newValue)
                }
            })
    }

    // 댓글 목록 조회
    fun getCommentList(
        watch_id: Int,
        reset: Boolean,
        completionHandler: (MutableList<KommentModel>) -> Unit
    ) {
        if (reset) {
            this.reset()
        }

        if (available()) {
            isLoading = true
            api.getCommentList(APIClient.accessToken, watch_id, page)
                .enqueue(object : Callback<APIResult<MutableList<KommentModel>>> {
                    override fun onFailure(
                        call: Call<APIResult<MutableList<KommentModel>>>,
                        t: Throwable
                    ) {
                        isLoading = false
                    }

                    override fun onResponse(
                        call: Call<APIResult<MutableList<KommentModel>>>,
                        response: Response<APIResult<MutableList<KommentModel>>>
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

    // 댓글수 조회
    fun getCommentCount(watch_id: Int, completionHandler: (Int) -> Unit) {
        api.getCommentCount(watch_id)
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

    // 새댓글 목록 조회
    fun getNewComment(
        watch_id: Int,
        latest_id: Int,
        completionHandler: (MutableList<KommentModel>) -> Unit
    ) {

        if (!isLoading) {
            isLoading = true

            api.getNewComment(APIClient.accessToken, watch_id, latest_id)
                .enqueue(object : Callback<APIResult<MutableList<KommentModel>>> {
                    override fun onFailure(
                        call: Call<APIResult<MutableList<KommentModel>>>,
                        t: Throwable
                    ) {
                        isLoading = false
                        completionHandler(mutableListOf())
                    }

                    override fun onResponse(
                        call: Call<APIResult<MutableList<KommentModel>>>,
                        response: Response<APIResult<MutableList<KommentModel>>>
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

    // 댓글수 조회 (삭제글 포함)
    fun getCommentCountTotal(watch_id: Int, completionHandler: (Int) -> Unit) {
        api.getCommentCountTotal(watch_id)
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

    // 플레이 댓글 목록 조회
    fun getReplayCommentList(
        watch_id: Int,
        completionHandler: (ReplayModel) -> Unit
    ) {
        api.getReplayCommentList(APIClient.accessToken, watch_id)
            .enqueue(object : Callback<APIResult<ReplayModel>> {
                override fun onFailure(
                    call: Call<APIResult<ReplayModel>>,
                    t: Throwable
                ) {
                }

                override fun onResponse(
                    call: Call<APIResult<ReplayModel>>,
                    response: Response<APIResult<ReplayModel>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }
            })
    }

    // 댓글 삭제하기
    fun deleteComment(watch_id: Int, comment_id: Int, completionHandler: (KommentModel) -> Unit) {
        api.deleteComment(APIClient.accessToken, watch_id, comment_id)
            .enqueue(object : Callback<APIResult<KommentModel>> {
                override fun onFailure(call: Call<APIResult<KommentModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<KommentModel>>,
                    response: Response<APIResult<KommentModel>>
                ) {
                    val comment = response.body()?.result
                    comment?.let { completionHandler(it) }
                }

            })
    }

    // 시청자 수 초기화
    fun resetViewerCount(watch_id: Int, completionHandler: (WatchModel) -> Unit) {
        api.resetViewerCount(APIClient.accessToken, watch_id)
            .enqueue(object : Callback<APIResult<WatchModel>> {
                override fun onFailure(
                    call: Call<APIResult<WatchModel>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<WatchModel>>,
                    response: Response<APIResult<WatchModel>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }
            })
    }

    // 시청자 수 증가
    fun increaseViewerCount(watch_id: Int, completionHandler: (WatchModel) -> Unit) {
        api.increaseViewerCount(APIClient.accessToken, watch_id)
            .enqueue(object : Callback<APIResult<WatchModel>> {
                override fun onFailure(
                    call: Call<APIResult<WatchModel>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<WatchModel>>,
                    response: Response<APIResult<WatchModel>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }
            })
    }

    // 시청자 수 감소
    fun decreaseViewerCount(watch_id: Int, completionHandler: (WatchModel) -> Unit) {
        api.decreaseViewerCount(APIClient.accessToken, watch_id)
            .enqueue(object : Callback<APIResult<WatchModel>> {
                override fun onFailure(
                    call: Call<APIResult<WatchModel>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<WatchModel>>,
                    response: Response<APIResult<WatchModel>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }
            })
    }

    // 조회수 증가
    fun increaseViewCount(id: Int, completionHandler: (WatchModel) -> Unit) {
        api.increaseViewCount(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<WatchModel>> {
                override fun onFailure(
                    call: Call<APIResult<WatchModel>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<WatchModel>>,
                    response: Response<APIResult<WatchModel>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }
            })
    }

    fun connectWatch(id: Int) {
        api.connectWatch(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<SimpleResultModel>> {
                override fun onFailure(call: Call<APIResult<SimpleResultModel>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<APIResult<SimpleResultModel>>,
                    response: Response<APIResult<SimpleResultModel>>
                ) {
                }
            })
    }

    fun exitWatch(id: Int) {
        api.exitWatch(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<SimpleResultModel>> {
                override fun onFailure(call: Call<APIResult<SimpleResultModel>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<APIResult<SimpleResultModel>>,
                    response: Response<APIResult<SimpleResultModel>>
                ) {
                }
            })
    }

    fun exitWatch(id: Int, sec: Int) {
        api.exitWatch(APIClient.accessToken, id, sec)
            .enqueue(object : Callback<APIResult<SimpleResultModel>> {
                override fun onFailure(call: Call<APIResult<SimpleResultModel>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<APIResult<SimpleResultModel>>,
                    response: Response<APIResult<SimpleResultModel>>
                ) {
                }
            })
    }
}
