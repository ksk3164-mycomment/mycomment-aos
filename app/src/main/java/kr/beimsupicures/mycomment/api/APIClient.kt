package kr.beimsupicures.mycomment.api

import android.content.Context
import android.util.Log
import kr.beimsupicures.mycomment.BuildConfig
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getAccessToken
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.HTTP
import java.lang.Exception

data class APIResult<T>(
    val success: Boolean,
    val result: T,
    val message: String?
)

class APIClient {
    companion object {
        val baseURL = "http://api.my-comment.co.kr:3000/" // live server
        //        val baseURL = "http://api.my-comment.co.kr:3001/" // dev server

        private fun createOkHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
            builder.addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder().header("User-Agent", "android/${BuildConfig.VERSION_NAME}").build()
                )
            }
            return builder.build()
        }

        var retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        var accessToken: String? = ""
            get() {
                BaseApplication.shared.getSharedPreferences().getAccessToken()?.let { accessToken ->
                    return "Bearer $accessToken"
                } ?: run {
                    return null
                }

            }

        fun <T> create(service: Class<T>): T {
            return retrofit.create(service)
        }

    }
}
