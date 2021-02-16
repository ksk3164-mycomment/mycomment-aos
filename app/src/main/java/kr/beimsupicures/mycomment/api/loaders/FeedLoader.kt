package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.*
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

    @GET("feed/detail/{talk_id}")
    fun getFeedDetail(@Path("talk_id") talk_id: Int): Call<APIResult<FeedDetailModel>>

    @DELETE("feed/delete/{talk_id}")
    fun deleteFeedDetail(@Header("Authorization") accessToken: String?,@Path("talk_id") talk_id: Int): Call<APIResult<UpdatedModel>>
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
        talk_id: Int,
        completionHandler: (FeedDetailModel) -> Unit
    ) {
        api.getFeedDetail(talk_id)
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

    fun deleteFeedDetail(talk_id: Int, completionHandler: (UpdatedModel) -> Unit){
        api.deleteFeedDetail(APIClient.accessToken,talk_id)
            .enqueue(object : Callback<APIResult<UpdatedModel>>{
                override fun onResponse(
                    call: Call<APIResult<UpdatedModel>>,
                    response: Response<APIResult<UpdatedModel>>
                ) {
                    val update = response.body()?.result
                    update?.let{ completionHandler(it) }
                }

                override fun onFailure(call: Call<APIResult<UpdatedModel>>, t: Throwable) {

                }

            })
    }

}
