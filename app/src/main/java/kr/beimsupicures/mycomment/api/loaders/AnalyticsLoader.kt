package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.SimpleResultModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface AnalyticsService {
    @PUT("talk/history")
    @FormUrlEncoded
    fun connectTalk(@Header("Authorization") accessToken: String?, @Field("talk_id") talk_id: Int): Call<APIResult<SimpleResultModel>>

    @HTTP(method = "DELETE", path = "talk/{id}/history", hasBody = true)
    fun exitTalk(@Header("Authorization") accessToken: String?, @Path("id") talk_id: Int): Call<APIResult<SimpleResultModel>>

    @PATCH("talk/{id}/history")
    @FormUrlEncoded
    fun exitTalk(@Header("Authorization") accessToken: String?, @Path("id") talk_id: Int, @Field("stay_sec") stay_sec: Int): Call<APIResult<SimpleResultModel>>

    @PATCH("analytics/comment/mention/{id}/reception")
    @FormUrlEncoded
    fun reportMentionReceive(@Header("Authorization") accessToken: String?, @Path("id") mention_id: Int, @Field("reception") reception: Boolean = true): Call<APIResult<SimpleResultModel>>

    @PATCH("analytics/comment/mention/{id}/reception/disable")
    @FormUrlEncoded
    fun reportMentionReceiveDisable(@Header("Authorization") accessToken: String?, @Path("id") mention_id: Int, @Field("reception") reception: Boolean = true): Call<APIResult<SimpleResultModel>>

    @PATCH("analytics/comment/mention/{id}/confirm")
    @FormUrlEncoded
    fun reportMentionConfirm(@Header("Authorization") accessToken: String?, @Path("id") mention_id: Int, @Field("confirm") confirm: Boolean = true): Call<APIResult<SimpleResultModel>>
}

class AnalyticsLoader : BaseLoader<AnalyticsService> {

    companion object {
        val shared = AnalyticsLoader()
    }

    constructor() {
        api = APIClient.create(AnalyticsService::class.java)
    }

    fun connectTalk(id: Int) {
        api.connectTalk(APIClient.accessToken, id)
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

    fun exitTalk(id: Int) {
        api.exitTalk(APIClient.accessToken, id)
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

    fun exitTalk(id: Int, sec: Int) {
        api.exitTalk(APIClient.accessToken, id, sec)
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

    fun reportMentionReceive(id: Int) {
        api.reportMentionReceive(APIClient.accessToken, id)
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

    fun reportMentionReceiveDisable(id: Int) {
        api.reportMentionReceiveDisable(APIClient.accessToken, id)
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

    fun reportMentionConfirm(id: Int) {
        api.reportMentionConfirm(APIClient.accessToken, id)
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