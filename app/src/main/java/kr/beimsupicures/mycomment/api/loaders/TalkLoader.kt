package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.TalkModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface TalkService {

    @GET("provider/{provider_id}/talk")
    fun getTalkList(@Header("Authorization") accessToken: String?, @Path("provider_id") provider_id: Int): Call<APIResult<MutableList<TalkModel>>>

    @GET("talk")
    fun getTalkList(@Header("Authorization") accessToken: String?, @Query("weekday") weekday: String): Call<APIResult<MutableList<TalkModel>>>

    @GET("talk/today")
    fun getTalkList(@Header("Authorization") accessToken: String?): Call<APIResult<MutableList<TalkModel>>>


    @GET("talk/{id}")
    fun getTalk(@Header("Authorization") accessToken: String?, @Path("id") id: Int): Call<APIResult<TalkModel>>

    @PATCH("talk/{id}/view/increase")
    fun increaseViewCount(@Header("Authorization") accessToken: String?, @Path("id") id: Int): Call<APIResult<TalkModel>>
}

class TalkLoader : BaseLoader<TalkService> {

    companion object {
        val shared = TalkLoader()
    }

    constructor() {
        api = APIClient.create(TalkService::class.java)
    }

    // 방송사별 방송목록 조회
    fun getTalkList(provider_id: Int, completionHandler: (MutableList<TalkModel>) -> Unit) {
        api.getTalkList(APIClient.accessToken, provider_id)
            .enqueue(object : Callback<APIResult<MutableList<TalkModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<TalkModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<TalkModel>>>,
                    response: Response<APIResult<MutableList<TalkModel>>>
                ) {
                    val talk = response.body()?.result
                    talk?.let { completionHandler(it) }
                }

            })
    }

    // 요일별 방송목록 조회
//    fun getTalkList(weekday: TalkModel.Weekday, completionHandler: (MutableList<TalkModel>) -> Unit) {
//        api.getTalkList(APIClient.accessToken, weekday.value)
//            .enqueue(object : Callback<APIResult<MutableList<TalkModel>>> {
//                override fun onFailure(call: Call<APIResult<MutableList<TalkModel>>>, t: Throwable) {
//
//                }
//
//                override fun onResponse(
//                    call: Call<APIResult<MutableList<TalkModel>>>,
//                    response: Response<APIResult<MutableList<TalkModel>>>
//                ) {
//                    val talk = response.body()?.result
//                    talk?.let { completionHandler(it) }
//                }
//
//            })
//    }

    fun getTalkList(weekday: String , completionHandler: (MutableList<TalkModel>) -> Unit) {
        api.getTalkList(APIClient.accessToken, weekday)
            .enqueue(object : Callback<APIResult<MutableList<TalkModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<TalkModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<TalkModel>>>,
                    response: Response<APIResult<MutableList<TalkModel>>>
                ) {
                    val talk = response.body()?.result
                    talk?.let { completionHandler(it) }
                }

            })
    }
    fun getTalkList(completionHandler: (MutableList<TalkModel>) -> Unit) {
        api.getTalkList(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<MutableList<TalkModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<TalkModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<TalkModel>>>,
                    response: Response<APIResult<MutableList<TalkModel>>>
                ) {
                    val talk = response.body()?.result
                    talk?.let { completionHandler(it) }
                }

            })
    }

    // 특정 방송목록 조회
    fun getTalk(id: Int, completionHandler: (TalkModel) -> Unit) {
        api.getTalk(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<TalkModel>> {
                override fun onFailure(call: Call<APIResult<TalkModel>>, t: Throwable) { }

                override fun onResponse(
                    call: Call<APIResult<TalkModel>>,
                    response: Response<APIResult<TalkModel>>
                ) {
                    val talk = response.body()?.result
                    talk?.let { completionHandler(it) }
                }

            })
    }

    // 조회수 증가
    fun increaseViewCount(id: Int, completionHandler: (TalkModel) -> Unit) {
        api.increaseViewCount(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<TalkModel>> {
                override fun onFailure(
                    call: Call<APIResult<TalkModel>>,
                    t: Throwable
                ) {

                }

                override fun onResponse(
                    call: Call<APIResult<TalkModel>>,
                    response: Response<APIResult<TalkModel>>
                ) {
                    val newValue = response.body()?.result
                    newValue?.let { newValue ->
                        completionHandler(newValue)
                    }
                }
            })
    }
}
