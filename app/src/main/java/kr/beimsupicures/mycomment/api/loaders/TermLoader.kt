package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.TermModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TermService {

    @GET("terms/{category}")
    fun getTerm(@Path("category") category: TermModel.Category): Call<APIResult<TermModel>>
}

class TermLoader : BaseLoader<TermService> {

    companion object {
        val shared = TermLoader()
    }

    constructor() {
        api = APIClient.create(TermService::class.java)
    }

    // 약관 조회
    fun getTerm(category: TermModel.Category, completionHandler: (TermModel) -> Unit) {
        api.getTerm(category)
            .enqueue(object : Callback<APIResult<TermModel>> {
                override fun onFailure(call: Call<APIResult<TermModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<TermModel>>,
                    response: Response<APIResult<TermModel>>
                ) {
                    val term = response.body()?.result
                    term?.let { completionHandler(it) }
                }

            })
    }
}
