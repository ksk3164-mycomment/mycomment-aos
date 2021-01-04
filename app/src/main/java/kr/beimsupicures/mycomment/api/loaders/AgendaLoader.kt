package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.models.AgendaModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface AgendaService {
    @GET("agenda")
    fun getAgenda(@Header("Authorization") accessToken: String?): Call<APIResult<List<AgendaModel>>>

    @GET("agenda/{id}")
    fun getAgenda(@Header("Authorization") accessToken: String?, @Path("id") id: Int): Call<APIResult<AgendaModel>>

    @POST("agenda")
    @FormUrlEncoded
    fun getAgenda(@Header("Authorization") accessToken: String?, @Field("date") date: String): Call<APIResult<List<AgendaModel>>>

    @POST("agenda/view")
    @FormUrlEncoded
    fun increaseViewCount(@Header("Authorization") accessToken: String?, @Field("agenda_id") agenda_id: Int): Call<APIResult<AgendaModel>>

    @POST("agenda/suggest")
    @FormUrlEncoded
    fun suggestAgenda(@Field("agenda") agenda: String): Call<APIResult<AgendaModel.SuggestModel>>
}

class AgendaLoader : BaseLoader<AgendaService> {

    companion object {
        val shared = AgendaLoader()
    }

    constructor() {
        api = APIClient.create(AgendaService::class.java)
    }

    fun getAgeanda(completionHandler: (List<AgendaModel>) -> Unit) {
        api.getAgenda(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<List<AgendaModel>>> {
                override fun onFailure(call: Call<APIResult<List<AgendaModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<AgendaModel>>>,
                    response: Response<APIResult<List<AgendaModel>>>
                ) {
                    val agenda = response.body()?.result
                    agenda?.let { completionHandler(it) }
                }

            })
    }

    fun getAgenda(id: Int, completionHandler: (AgendaModel) -> Unit) {
        api.getAgenda(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<AgendaModel>> {
                override fun onFailure(call: Call<APIResult<AgendaModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<AgendaModel>>,
                    response: Response<APIResult<AgendaModel>>
                ) {
                    val agenda = response.body()?.result
                    agenda?.let { completionHandler(it) }
                }

            })
    }

    fun getAgenda(date: String, completionHandler: (List<AgendaModel>) -> Unit) {
        api.getAgenda(APIClient.accessToken, date)
            .enqueue(object : Callback<APIResult<List<AgendaModel>>> {
                override fun onFailure(call: Call<APIResult<List<AgendaModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<AgendaModel>>>,
                    response: Response<APIResult<List<AgendaModel>>>
                ) {
                    val agenda = response.body()?.result
                    agenda?.let { completionHandler(it) }
                }

            })
    }

    // 아젠다 조회수 증가
    fun increaseViewCount(id: Int, completionHandler: (AgendaModel) -> Unit) {
        api.increaseViewCount(APIClient.accessToken, id)
            .enqueue(object : Callback<APIResult<AgendaModel>> {
                override fun onFailure(call: Call<APIResult<AgendaModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<AgendaModel>>,
                    response: Response<APIResult<AgendaModel>>
                ) {
                    val agenda = response.body()?.result
                    agenda?.let { completionHandler(it) }
                }

            })
    }

    // 아젠다 제안
    fun suggestAgenda(agenda: String, completionHandler: (AgendaModel.SuggestModel) -> Unit) {
        api.suggestAgenda(agenda)
            .enqueue(object : Callback<APIResult<AgendaModel.SuggestModel>> {
                override fun onFailure(call: Call<APIResult<AgendaModel.SuggestModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<AgendaModel.SuggestModel>>,
                    response: Response<APIResult<AgendaModel.SuggestModel>>
                ) {
                    val agenda = response.body()?.result
                    agenda?.let { completionHandler(it) }
                }

            })
    }
}
