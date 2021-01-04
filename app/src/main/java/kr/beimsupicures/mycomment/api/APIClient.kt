package kr.beimsupicures.mycomment.api

import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getAccessToken
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class APIResult<T>(
    val success: Boolean,
    val result: T,
    val message: String?
)

class APIClient {
    companion object {
        val baseURL = "http://api.my-comment.co.kr:3000/" // live server
//        val baseURL = "http://api.my-comment.co.kr:3001/" // dev server
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var accessToken: String? = ""
            get() {
                BaseApplication.shared.getSharedPreferences().getAccessToken()?.let { accessToken ->
                    return "Bearer ${accessToken}"

                } ?: run {
                    return null
                }
            }

        fun <T> create(service: Class<T>): T {
            return retrofit.create(service)
        }
    }
}
