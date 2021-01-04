package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.PickModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface PickService {

    @POST("pick")
    @FormUrlEncoded
    fun pick(
        @Header("Authorization") accessToken: String?,
        @Field("category") category: PickModel.Category,
        @Field("category_owner_id") category_owner_id: Int? = null,
        @Field("category_id") category_id: Int
    ): Call<APIResult<PickModel>>

    @POST("unpick")
    @FormUrlEncoded
    fun unpick(
        @Header("Authorization") accessToken: String?,
        @Field("category") category: PickModel.Category,
        @Field("category_id") category_id: Int
    ): Call<APIResult<PickModel>>

}

class PickLoader : BaseLoader<PickService> {

    companion object {
        val shared = PickLoader()
    }

    constructor() {
        api = APIClient.create(PickService::class.java)
    }

    fun pick(
        category: PickModel.Category,
        category_owner_id: Int? = null,
        category_id: Int,
        completionHandler: (PickModel) -> Unit
    ) {
        api.pick(APIClient.accessToken, category, category_owner_id, category_id)
            .enqueue(object : Callback<APIResult<PickModel>> {
                override fun onFailure(call: Call<APIResult<PickModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<PickModel>>,
                    response: Response<APIResult<PickModel>>
                ) {
                    val pick = response.body()?.result
                    pick?.let { completionHandler(it) }
                }

            })
    }

    fun unpick(
        category: PickModel.Category,
        category_id: Int,
        completionHandler: (PickModel) -> Unit
    ) {
        api.unpick(APIClient.accessToken, category, category_id)
            .enqueue(object : Callback<APIResult<PickModel>> {
                override fun onFailure(call: Call<APIResult<PickModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<PickModel>>,
                    response: Response<APIResult<PickModel>>
                ) {
                    val pick = response.body()?.result
                    pick?.let { completionHandler(it) }
                }

            })
    }
}
