package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.CommentModel
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.api.models.PickModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface FeedService {

    @GET("feed/list/{talk_id}/{page}")
    fun getFeedList(@Header("Authorization") accessToken: String?, @Path("talk_id") talk_id: Int, @Path("page") page: Int): Call<APIResult<MutableList<FeedModel>>>


//    @POST("unpick")
//    @FormUrlEncoded
//    fun unpick(
//        @Header("Authorization") accessToken: String?,
//        @Field("category") category: PickModel.Category,
//        @Field("category_id") category_id: Int
//    ): Call<APIResult<PickModel>>

}

class FeedLoader : BaseLoader<FeedService> {

    var items = mutableListOf<FeedModel>()

    companion object {
        val shared = FeedLoader()
    }

    constructor() {
        api = APIClient.create(FeedService::class.java)
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
}
