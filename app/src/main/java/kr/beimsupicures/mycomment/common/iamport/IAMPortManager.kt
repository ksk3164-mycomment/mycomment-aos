package kr.beimsupicures.mycomment.common.iamport

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class IAMPortCertificationResult(
    val unique_key: String,
    val name: String,
    val birth: String,
    val gender: Gender
) {
    enum class Gender {
        male, female
    }
}

data class IAMPortAccessTokenResult(
    val access_token: String,
    val now: String,
    val expired_at: Int
)

data class IAMPortResult<T>(
    val code: Int,
    val message: String?,
    val response: T
)

interface IAMPortService {
    @GET("/certifications/{imp_uid}")
    fun getAuthProfile(@Header("Authorization") token: String, @Path("imp_uid") imp_uid: String): Call<IAMPortResult<IAMPortCertificationResult>>

    @POST("/users/getToken")
    @FormUrlEncoded
    fun getAccessToken(@Field("imp_key") imp_key: String, @Field("imp_secret") imp_secret: String): Call<IAMPortResult<IAMPortAccessTokenResult>>
}

class IAMPortManager {

    val api: IAMPortService

    companion object {
        val shared = IAMPortManager()
    }

    constructor() {
        api =
            APIClient.create(
                IAMPortService::class.java
            )
    }

    class APIClient {
        companion object {
            val baseURL = "https://api.iamport.kr/"
            val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            fun create(service: Class<IAMPortService>): IAMPortService {
                return retrofit.create(service)
            }
        }
    }

    fun getAuthProfile(token: String, impUid: String, completionHandler: (IAMPortCertificationResult) -> Unit) {
        api.getAuthProfile(token, impUid)
            .enqueue(object : Callback<IAMPortResult<IAMPortCertificationResult>> {
                override fun onFailure(call: Call<IAMPortResult<IAMPortCertificationResult>>, t: Throwable) {

                }

                override fun onResponse(call: Call<IAMPortResult<IAMPortCertificationResult>>, response: Response<IAMPortResult<IAMPortCertificationResult>>) {
                    val response = response.body()?.response
                    response?.let { completionHandler(it) }
                }
            })
    }

    fun getAccessToken(impKey: String, impSecret: String, completionHandler: (String) -> Unit) {
        api.getAccessToken(impKey, impSecret)
            .enqueue(object : Callback<IAMPortResult<IAMPortAccessTokenResult>> {
                override fun onFailure(call: Call<IAMPortResult<IAMPortAccessTokenResult>>, t: Throwable) {

                }

                override fun onResponse(call: Call<IAMPortResult<IAMPortAccessTokenResult>>, response: Response<IAMPortResult<IAMPortAccessTokenResult>>) {
                    val response = response.body()?.response
                    response?.let { completionHandler(it.access_token) }
                }
            })
    }
}