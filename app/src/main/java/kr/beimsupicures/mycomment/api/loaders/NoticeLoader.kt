package kr.beimsupicures.mycomment.api.loaders

import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.AdModel
import kr.beimsupicures.mycomment.api.models.NoticeModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET

interface NoticeService {

    @GET("notice")
    fun getNoticeList(): Call<APIResult<MutableList<NoticeModel>>>
}

class NoticeLoader : BaseLoader<NoticeService> {

    var items: MutableList<NoticeModel> = mutableListOf()

    companion object {
        val shared = NoticeLoader()
    }

    constructor() {
        api = APIClient.create(NoticeService::class.java)
    }

    override fun reset() {
        super.reset()
        items.clear()
    }

    fun getNoticeList(
        reset: Boolean,
        completionHandler: (MutableList<NoticeModel>) -> Unit
    ) {
        if (reset) {
            this.reset()
        }

        if (available()) {
            isLoading = true
            api.getNoticeList()
                .enqueue(object : Callback<APIResult<MutableList<NoticeModel>>> {
                    override fun onFailure(
                        call: Call<APIResult<MutableList<NoticeModel>>>,
                        t: Throwable
                    ) {

                    }

                    override fun onResponse(
                        call: Call<APIResult<MutableList<NoticeModel>>>,
                        response: Response<APIResult<MutableList<NoticeModel>>>
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
