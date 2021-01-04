package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.AdModel
import kr.beimsupicures.mycomment.api.models.ProviderModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProviderService {

    @GET("provider/{id}")
    fun getProvider(@Path("id") id: Int): Call<APIResult<ProviderModel>>

    @GET("provider")
    fun getProviderList(): Call<APIResult<MutableList<ProviderModel>>>
}

class ProviderLoader : BaseLoader<ProviderService> {

    var items: MutableList<ProviderModel> = mutableListOf()

    companion object {
        val shared = ProviderLoader()
    }

    constructor() {
        api = APIClient.create(ProviderService::class.java)
    }

    override fun reset() {
        super.reset()
        items.clear()
    }

    // 방송사 조회
    fun getProvider(id: Int, completionHandler:  (ProviderModel) -> Unit) {
        api.getProvider(id)
            .enqueue(object : Callback<APIResult<ProviderModel>> {
                override fun onFailure(call: Call<APIResult<ProviderModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<ProviderModel>>,
                    response: Response<APIResult<ProviderModel>>
                ) {
                    val link = response.body()?.result
                    link?.let { completionHandler(it) }
                }

            })
    }

    // 방송사 목록 조회
    fun getProviderList(
        reset: Boolean,
        completionHandler: (MutableList<ProviderModel>) -> Unit
    ) {
        if (reset) {
            this.reset()
        }

        if (available()) {
            isLoading = true
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
}
