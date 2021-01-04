package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.api.models.WatchModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("talk/search")
    fun searchTalk(@Query("keyword") keyword: String): Call<APIResult<MutableList<TalkModel>>>

    @GET("watch/search")
    fun searchWatch(@Query("keyword") keyword: String): Call<APIResult<MutableList<WatchModel>>>
}

class SearchLoader : BaseLoader<SearchService> {

    companion object {
        val shared = SearchLoader()
    }

    constructor() {
        api = APIClient.create(SearchService::class.java)
    }

    // 키워드 검색 결과
    fun searchTalk(keyword: String, completionHandler: (MutableList<TalkModel>) -> Unit) {
        api.searchTalk(keyword)
            .enqueue(object : Callback<APIResult<MutableList<TalkModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<TalkModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<TalkModel>>>,
                    response: Response<APIResult<MutableList<TalkModel>>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }

            })
    }

    // 키워드 검색 결과
    fun searchWatch(keyword: String, completionHandler: (MutableList<WatchModel>) -> Unit) {
        api.searchWatch(keyword)
            .enqueue(object : Callback<APIResult<MutableList<WatchModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<WatchModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<WatchModel>>>,
                    response: Response<APIResult<MutableList<WatchModel>>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }

            })
    }
}
