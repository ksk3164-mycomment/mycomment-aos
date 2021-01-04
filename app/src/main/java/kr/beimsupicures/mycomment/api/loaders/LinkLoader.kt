package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.models.LinkModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface LinkService {

    @GET("agenda/{id}/link")
    fun getLink(@Path("id") id: Int): Call<APIResult<List<LinkModel>>>
}

class LinkLoader : BaseLoader<LinkService> {

    companion object {
        val shared = LinkLoader()
    }

    constructor() {
        api = APIClient.create(LinkService::class.java)
    }

    fun getLink(id: Int, completionHandler: (List<LinkModel>) -> Unit) {
        api.getLink(id)
            .enqueue(object : Callback<APIResult<List<LinkModel>>> {
                override fun onFailure(call: Call<APIResult<List<LinkModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<LinkModel>>>,
                    response: Response<APIResult<List<LinkModel>>>
                ) {
                    val link = response.body()?.result
                    link?.let { completionHandler(it) }
                }

            })
    }
}
