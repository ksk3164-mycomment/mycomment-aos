package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.ReportModel
import kr.beimsupicures.mycomment.api.models.TermModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ReportService {

    @POST("report")
    @FormUrlEncoded
    fun report(@Header("Authorization") accessToken: String?,  @Field("category") category: String, @Field("category_id") category_id: Int, @Field("content") content: String): Call<APIResult<ReportModel>>
}

class ReportLoader : BaseLoader<ReportService> {

    companion object {
        val shared = ReportLoader()
    }

    constructor() {
        api = APIClient.create(ReportService::class.java)
    }

    // 신고하기
    fun report(category: ReportModel.Category, category_id: Int, content: String, completionHandler: (ReportModel) -> Unit) {
        api.report(APIClient.accessToken, category.toString(), category_id, content)
            .enqueue(object : Callback<APIResult<ReportModel>> {
                override fun onFailure(call: Call<APIResult<ReportModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<ReportModel>>,
                    response: Response<APIResult<ReportModel>>
                ) {
                    val report = response.body()?.result
                    report?.let { completionHandler(it) }
                }

            })
    }
}
