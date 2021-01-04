package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.AdModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET

interface AdService {

    @GET("ad")
    fun getAdList(): Call<APIResult<MutableList<AdModel>>>
}

class AdLoader : BaseLoader<AdService> {

    var items: MutableList<AdModel> = mutableListOf()

    companion object {
        val shared = AdLoader()
    }

    constructor() {
        api = APIClient.create(AdService::class.java)
    }

    override fun reset() {
        super.reset()
        items.clear()
    }

    fun getAdList(
        reset: Boolean,
        location: AdModel.Location,
        completionHandler: (MutableList<AdModel>) -> Unit
    ) {
        if (reset) {
            this.reset()
        }

        if (available()) {
            isLoading = true
            api.getAdList()
                .enqueue(object : Callback<APIResult<MutableList<AdModel>>> {
                    override fun onFailure(
                        call: Call<APIResult<MutableList<AdModel>>>,
                        t: Throwable
                    ) {

                    }

                    override fun onResponse(
                        call: Call<APIResult<MutableList<AdModel>>>,
                        response: Response<APIResult<MutableList<AdModel>>>
                    ) {
                        val newValue = response.body()?.result
                        newValue?.let { newValue ->
                            items.addAll(newValue)
                            page += 1
                            isLoading = false
                            isLast = (newValue.size == 0)

                            completionHandler(items.filter { it.location == location }.toMutableList())
                        }
                    }
                })
        }
    }
}
