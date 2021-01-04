package kr.beimsupicures.mycomment.api.loaders

import android.widget.Toast
import kr.beimsupicures.mycomment.api.APIClient
import kr.beimsupicures.mycomment.api.APIResult
import kr.beimsupicures.mycomment.api.loaders.base.BaseLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.common.iamport.IAMPortCertificationResult
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.setAccessToken
import kr.beimsupicures.mycomment.extensions.setUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.*

interface UserService {

    @POST("user/sign/sns")
    @FormUrlEncoded
    fun addUser(
        @Field("email") email: String,
        @Field("birth") birth: String?,
        @Field("sns") sns: String,
        @Field("gender") gender: IAMPortCertificationResult.Gender?,
        @Field("nickname") nickname: String
    ): Call<APIResult<String>>

    @GET("user")
    fun getUser(@Header("Authorization") accessToken: String?): Call<APIResult<UserModel>>

    @GET("user/{id}")
    fun getUser(@Path("id") id: Int): Call<APIResult<UserModel>>

    @GET("user/{id}/opinion")
    fun getUserOpinion(@Header("Authorization") accessToken: String?, @Path("id") id: Int): Call<APIResult<List<OpinionModel>>>

    @GET("user/agenda/bookmark")
    fun getUserAgendaOpinion(@Header("Authorization") accessToken: String?): Call<APIResult<List<AgendaModel>>>

    @GET("user/opinion/bookmark")
    fun getUserBookmarkOpinion(@Header("Authorization") accessToken: String?): Call<APIResult<List<OpinionModel>>>

    @GET("user/{id}/scrapped")
    fun getUserScrappedCount(@Path("id") id: Int): Call<APIResult<Int>>

    @POST("user/unique")
    @FormUrlEncoded
    fun uniqueUser(unique_key: String): Call<APIResult<Boolean>>

    @POST("user/nickname/unique")
    @FormUrlEncoded
    fun uniqueNickname(@Field("nickname") nickname: String): Call<APIResult<Boolean>>

    @POST("user/forgot/nickname")
    @FormUrlEncoded
    fun findUser(@Field("unique_key") unique_key: String): Call<APIResult<UserModel>>

    @POST("user/sign")
    @FormUrlEncoded
    fun signUser(@Field("nickname") nickname: String, @Field("password") password: String): Call<APIResult<String>>

    @POST("user")
    @FormUrlEncoded
    fun addUser(
        @Field("unique_key") unique_key: String,
        @Field("name") name: String,
        @Field("birth") birth: String,
        @Field("gender") gender: IAMPortCertificationResult.Gender,
        @Field("nickname") nickname: String,
        @Field("phone") phone: String?,
        @Field("password") password: String
    ): Call<APIResult<String>>

    @PATCH("user/password/forgot")
    @FormUrlEncoded
    fun forgotPassword(@Field("nickname") nickname: String, @Field("password") password: String): Call<APIResult<UserModel>>

    @PATCH("user/intro")
    @FormUrlEncoded
    fun updateIntro(@Header("Authorization") accessToken: String?, @Field("intro") intro: String): Call<APIResult<UserModel>>

    @PATCH("user/password")
    @FormUrlEncoded
    fun updatePassword(@Header("Authorization") accessToken: String?, @Field("password") password: String): Call<APIResult<UserModel>>

    @PATCH("user/badge")
    @FormUrlEncoded
    fun updateProfileBadgeImage(@Header("Authorization") accessToken: String?, @Field("profile_badge_image_url") profile_badge_image_url: String): Call<APIResult<UserModel>>

    @PATCH("user/profile")
    @FormUrlEncoded
    fun updateProfileImage(@Header("Authorization") accessToken: String?, @Field("profile_image_url") profile_image_url: String): Call<APIResult<UserModel>>

    @DELETE("user")
    fun secessionUser(@Header("Authorization") accessToken: String?): Call<APIResult<UserModel>>

    @POST("user/email/unique")
    @FormUrlEncoded
    fun uniqueEmail(@Field("email") email: String): Call<APIResult<Boolean>>

    @PATCH("user/nickname")
    @FormUrlEncoded
    fun updateNickname(@Header("Authorization") accessToken: String?, @Field("nickname") nickname: String): Call<APIResult<UserModel>>

    @GET("user/{user_id}/liked/count")
    fun getUserPickedCommentCount(@Header("Authorization") accessToken: String?, @Path("user_id") user_id: Int): Call<APIResult<Int>>

    @GET("user/talk/bookmark")
    fun getUserBookmarkTalk(@Header("Authorization") accessToken: String?): Call<APIResult<MutableList<TalkModel>>>

    @GET("user/watch/bookmark")
    fun getUserBookmarkWatch(@Header("Authorization") accessToken: String?): Call<APIResult<MutableList<WatchModel>>>

    @PATCH("user/access")
    @FormUrlEncoded
    fun accessUser(@Header("Authorization") accessToken: String?, @Field("token") token: String, @Field("os") os: String): Call<APIResult<UserModel>>


    @PATCH("user/signout")
    fun signout(@Header("Authorization") accessToken: String?): Call<APIResult<UserModel>>
}

class UserLoader : BaseLoader<UserService> {

    companion object {
        val shared = UserLoader()
    }

    constructor() {
        api = APIClient.create(UserService::class.java)
    }

    // 소셜회원가입
    fun addUser(email: String, birth: String? = null, sns: UserModel.SocialProvider, gender: IAMPortCertificationResult.Gender? = null, nickname: String, completionHandler: (String) -> Unit) {
        api.addUser(email, birth, sns.toString(), gender, nickname)
            .enqueue(object : Callback<APIResult<String>> {
                override fun onFailure(call: Call<APIResult<String>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<APIResult<String>>,
                    response: Response<APIResult<String>>
                ) {
                    val accessToken = response.body()?.result
                    accessToken?.let {
                        BaseApplication.shared.getSharedPreferences().setAccessToken(it)
                        completionHandler(it)
                    }
                }
            })
    }

    // 로그인
    fun signUser(nickname: String, password: String, completionHandler: (String) -> Unit) {
        api.signUser(nickname, password)
            .enqueue(object : Callback<APIResult<String>> {
                override fun onFailure(call: Call<APIResult<String>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<String>>,
                    response: Response<APIResult<String>>
                ) {

                    when (response.body()?.success) {
                        true -> {
                            val accessToken = response.body()?.result
                            accessToken?.let { accessToken ->
                                BaseApplication.shared.getSharedPreferences()
                                    .setAccessToken(accessToken)
                                completionHandler(accessToken)
                            }
                        }

                        false -> {
                            Toast.makeText(
                                BaseApplication.shared.context(),
                                "아이디 또는 비밀번호를 확인해주세요",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
    }

    // 사용자 조회
    fun getUser(completionHandler: (UserModel) -> Unit) {
        api.getUser(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { user ->

                        BaseApplication.shared.getSharedPreferences().setUser(user)
                        completionHandler(user)
                    }
                }

            })
    }

    // 사용자 조회 (다른사람)
    fun getUser(id: Int, completionHandler: (UserModel) -> Unit) {
        api.getUser(id)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { completionHandler(it) }
                }

            })
    }

    // 히스토리
    fun getUserOpinion(user_id: Int, completionHandler: (List<OpinionModel>) -> Unit) {
        api.getUserOpinion(APIClient.accessToken, user_id)
            .enqueue(object : Callback<APIResult<List<OpinionModel>>> {
                override fun onFailure(call: Call<APIResult<List<OpinionModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<OpinionModel>>>,
                    response: Response<APIResult<List<OpinionModel>>>
                ) {
                    val opinion = response.body()?.result
                    opinion?.let { completionHandler(it) }
                }
            })
    }

    // 즐겨찾기 논의
    fun getUserAgendaOpinion(completionHandler: (List<AgendaModel>) -> Unit) {
        api.getUserAgendaOpinion(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<List<AgendaModel>>> {
                override fun onFailure(call: Call<APIResult<List<AgendaModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<AgendaModel>>>,
                    response: Response<APIResult<List<AgendaModel>>>
                ) {
                    val agenda = response.body()?.result
                    agenda?.let { completionHandler(it) }
                }
            })
    }

    // 타인의 의견
    fun getUserBookmarkOpinion(completionHandler: (List<OpinionModel>) -> Unit) {
        api.getUserBookmarkOpinion(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<List<OpinionModel>>> {
                override fun onFailure(call: Call<APIResult<List<OpinionModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<List<OpinionModel>>>,
                    response: Response<APIResult<List<OpinionModel>>>
                ) {
                    val opinion = response.body()?.result
                    opinion?.let { completionHandler(it) }
                }
            })
    }

    // 스크랩된 횟수
    fun getUserScrappedCount(id: Int, completionHandler: (Int) -> Unit) {
        api.getUserScrappedCount(id)
            .enqueue(object : Callback<APIResult<Int>> {
                override fun onFailure(call: Call<APIResult<Int>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<Int>>,
                    response: Response<APIResult<Int>>
                ) {
                    val count = response.body()?.result
                    count?.let { completionHandler(it) }
                }
            })
    }

    // 소개 업데이트
    fun updateIntro(intro: String, completionHandler: (UserModel) -> Unit) {
        api.updateIntro(APIClient.accessToken, intro)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { user ->

                        BaseApplication.shared.getSharedPreferences().setUser(user)
                        completionHandler(user)
                    }
                }
            })
    }

    // 비밀번호 찾기
    fun forgotPassword(nickname: String, password: String, completionHandler: (UserModel) -> Unit) {
        api.forgotPassword(nickname, password)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { user ->

                        BaseApplication.shared.getSharedPreferences().setUser(user)
                        completionHandler(user)
                    }
                }
            })
    }

    // 비밀번호 업데이트
    fun updatePassword(password: String, completionHandler: (UserModel) -> Unit) {
        api.updatePassword(APIClient.accessToken, password)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { user ->

                        BaseApplication.shared.getSharedPreferences().setUser(user)
                        completionHandler(user)
                    }
                }
            })
    }

    // 중복가입 확인
    fun uniqueUser(uniqueKey: String, completionHandler: (Boolean) -> Unit) {
        api.uniqueUser(uniqueKey)
            .enqueue(object : Callback<APIResult<Boolean>> {
                override fun onFailure(call: Call<APIResult<Boolean>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<Boolean>>,
                    response: Response<APIResult<Boolean>>
                ) {
                    val unique = response.body()?.result
                    unique?.let { completionHandler(it) }
                }
            })
    }

    // 아이디중복 확인
    fun uniqueNickname(nickname: String, completionHandler: (Boolean) -> Unit) {
        api.uniqueNickname(nickname)
            .enqueue(object : Callback<APIResult<Boolean>> {
                override fun onFailure(call: Call<APIResult<Boolean>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<Boolean>>,
                    response: Response<APIResult<Boolean>>
                ) {
                    val unique = response.body()?.result
                    unique?.let { completionHandler(it) }
                }
            })
    }

    // 사용자 조회 (아이디찾기)
    fun findUser(uniqueKey: String, completionHandler: (UserModel) -> Unit) {
        api.findUser(uniqueKey)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { completionHandler(it) }
                }
            })
    }

    // 회원가입
    fun addUser(uniqueKey: String, name: String, birth: String, gender: IAMPortCertificationResult.Gender, nickname: String, phone: String? = null, password: String, completionHandler: (String) -> Unit) {
        api.addUser(uniqueKey, name, birth, gender, nickname, phone, password)
            .enqueue(object : Callback<APIResult<String>> {
                override fun onFailure(call: Call<APIResult<String>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<APIResult<String>>,
                    response: Response<APIResult<String>>
                ) {
                    val accessToken = response.body()?.result
                    accessToken?.let {
                        BaseApplication.shared.getSharedPreferences().setAccessToken(it)
                        completionHandler(it)
                    }
                }
            })
    }

    // 배경 이미지 업데이트
    fun updateProfileBadgeImage(profileBadgeImageURL: String, completionHandler: (UserModel) -> Unit) {
        api.updateProfileBadgeImage(APIClient.accessToken, profileBadgeImageURL)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { user ->

                        BaseApplication.shared.getSharedPreferences().setUser(user)
                        completionHandler(user)
                    }
                }
            })
    }

    // 이미지 업데이트
    fun updateProfileImage(profileImageURL: String, completionHandler: (UserModel) -> Unit) {
        api.updateProfileImage(APIClient.accessToken, profileImageURL)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { user ->

                        BaseApplication.shared.getSharedPreferences().setUser(user)
                        completionHandler(user)
                    }
                }
            })
    }

    // 탈퇴하기
    fun secessionUser(completionHandler: (UserModel) -> Unit) {
        api.secessionUser(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { user ->

                        BaseApplication.shared.getSharedPreferences().setUser(user)
                        completionHandler(user)
                    }
                }
            })
    }

    // 이메일중복 확인
    fun uniqueEmail(email: String, completionHandler: (Boolean) -> Unit) {
        api.uniqueEmail(email)
            .enqueue(object : Callback<APIResult<Boolean>> {
                override fun onFailure(call: Call<APIResult<Boolean>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<Boolean>>,
                    response: Response<APIResult<Boolean>>
                ) {
                    val unique = response.body()?.result
                    unique?.let { completionHandler(it) }
                }
            })
    }

    // 닉네임 업데이트
    fun updateNickname(nickname: String, completionHandler: (UserModel) -> Unit) {
        api.updateNickname(APIClient.accessToken, nickname)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                    val user = response.body()?.result
                    user?.let { user ->

                        BaseApplication.shared.getSharedPreferences().setUser(user)
                        completionHandler(user)
                    }
                }
            })
    }

    // 좋아요 누적 횟수
    fun getUserPickedCommentCount(user_id: Int, completionHandler: (Int) -> Unit) {
        api.getUserPickedCommentCount(APIClient.accessToken, user_id)
            .enqueue(object : Callback<APIResult<Int>> {
                override fun onFailure(call: Call<APIResult<Int>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<Int>>,
                    response: Response<APIResult<Int>>
                ) {
                    val value = response.body()?.result
                    value?.let { value ->
                        completionHandler(value)
                    }
                }
            })
    }

    // 본방사수 (즐겨찾기)
    fun getUserBookmarkTalk(completionHandler: (MutableList<TalkModel>) -> Unit) {
        api.getUserBookmarkTalk(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<MutableList<TalkModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<TalkModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<TalkModel>>>,
                    response: Response<APIResult<MutableList<TalkModel>>>
                ) {
                    val value = response.body()?.result

                    value?.let { value ->
                        completionHandler(value)
                    }
                }
            })
    }

    // 나랑 볼 사람 (즐겨찾기)
    fun getUserBookmarkWatch(completionHandler: (MutableList<WatchModel>) -> Unit) {
        api.getUserBookmarkWatch(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<MutableList<WatchModel>>> {
                override fun onFailure(call: Call<APIResult<MutableList<WatchModel>>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<APIResult<MutableList<WatchModel>>>,
                    response: Response<APIResult<MutableList<WatchModel>>>
                ) {
                    val value = response.body()?.result
                    value?.let { value ->
                        completionHandler(value)
                    }
                }
            })
    }

    // 마지막 접속 시간 & FCM Token
    fun accessUser(token: String) {
        api.accessUser(APIClient.accessToken, token, "aos")
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                }
            })
    }

    // 로그아웃
    fun signout() {
        api.signout(APIClient.accessToken)
            .enqueue(object : Callback<APIResult<UserModel>> {
                override fun onFailure(call: Call<APIResult<UserModel>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<APIResult<UserModel>>,
                    response: Response<APIResult<UserModel>>
                ) {
                }
            })
    }
}
