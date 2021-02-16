package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.PickTopModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PickTopService {

    @GET("pick/top/comment/{ymd_date}")
    fun getPickTop(@Path ("ymd_date")ymd_date: String) : Call<APIResult<MutableList<PickTopModel>>>
}

class PickTopLoader : BaseLoader<PickTopService> {

    companion object {
        val shared = PickTopLoader()
    }

    constructor() {
        api = APIClient.create(PickTopService::class.java)
    }

    fun getPickTop(ymd_date : String, completionHandler: (MutableList<PickTopModel>) -> Unit) {
        api.getPickTop(ymd_date)
            .enqueue(object : Callback<APIResult<MutableList<PickTopModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<PickTopModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<PickTopModel>>>,
                    response: Response<APIResult<MutableList<PickTopModel>>>
                ) {
                    val pickTop = response.body()?.result
                    pickTop?.let { completionHandler(it) }
                }

            })
    }
}
