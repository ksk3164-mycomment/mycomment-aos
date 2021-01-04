package kr.beimsupicures.mycomment.controllers.signs

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.signin
import kr.beimsupicures.mycomment.viewmodels.signs.SignStep1ViewModel
import org.json.JSONObject

class SignInFragment : BaseFragment() {

    lateinit var callbackManager: CallbackManager

    lateinit var btnFacebook: TextView
    lateinit var btnGoogle: TextView
    lateinit var btnKakao: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun loadModel() {
        super.loadModel()

    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(callbackManager, object :
                FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.e("TAG", "${loginResult?.accessToken} ${loginResult?.accessToken?.userId}")
                    getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)
                }

                override fun onCancel() {

                }

                override fun onError(exception: FacebookException) {

                }
            })

            Session.getCurrentSession().addCallback(object : ISessionCallback {
                override fun onSessionOpenFailed(exception: KakaoException?) {
                    Log.e("onSessionOpened", "SessionStatusCallback.onSessionOpenFailed exception: $exception")
                }

                override fun onSessionOpened() {
                    Log.e("onSessionOpened", "SessionStatusCallback.onSessionOpened")

                    UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                        override fun onSuccess(result: MeV2Response?) {
                            Log.e("onSuccess", "success to update profile. msg = $result")

                            val kakaoAccount = JSONObject(result.toString()).getJSONObject("kakao_account")
                            val properties = JSONObject(result.toString()).getJSONObject("properties")

                            kakaoAccount?.let { kakaoAccount ->
                                properties?.let { properties ->

                                    val nickname = properties.getString("nickname")
                                    val email = kakaoAccount.getString("email")

                                    UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                                        override fun onCompleteLogout() {
                                            sign(email, nickname, UserModel.SocialProvider.kakao)
                                        }
                                    })
                                }
                            }
                        }

                        override fun onSessionClosed(errorResult: ErrorResult?) {
                            Log.e("onSessionClosed", "failed to update profile. msg = $errorResult")
                        }

                    })
                }
            })
            Session.getCurrentSession().checkAndImplicitOpen()

            btnFacebook = view.findViewById(R.id.btnFacebook)
            btnFacebook.setOnClickListener{
                LoginManager.getInstance().logInWithReadPermissions(activity, listOf("user_birthday", "public_profile", "user_gender", "email"))
            }
            btnGoogle = view.findViewById(R.id.btnGoogle)
            btnGoogle.setOnClickListener {
                activity?.let { activity ->
                    // Configure Google Sign In
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    activity?.startActivityForResult(GoogleSignIn.getClient(activity, gso).signInIntent, 99)
                }
            }
            btnKakao = view.findViewById(R.id.btnKakao)
            btnKakao.setOnClickListener {
                UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                    override fun onCompleteLogout() {
                        Session.getCurrentSession().open(AuthType.KAKAO_TALK, activity)
                    }
                })
            }

            // 참고
            /*
            btnSignIn.setOnClickListener {

                hideKeyboard()

                when (validation) {
                    true -> {
                        UserLoader.shared.signUser(
                            "${nicknameField.text}",
                            "${passwordField.text}"
                        ) { accessToken ->

                            UserLoader.shared.getUser {
                                activity?.onBackPressed()
                            }
                        }
                    }

                    false -> {
                        Toast.makeText(context, "아이디 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            */
        }
    }

    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {

        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, middle_name, last_name, name, picture, email"
        )
        GraphRequest(token,
            "/$userId/",
            parameters,
            HttpMethod.GET,
            GraphRequest.Callback { response ->
                val jsonObject = response.jsonObject

                // Facebook Access Token
                // You can see Access Token only in Debug mode.
                // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                }

                jsonObject.getString("email")?.let { email ->
                    jsonObject.getString("name")?.let { nickname ->

                        sign(email, nickname, UserModel.SocialProvider.facebook)
                    }
                }

            }).executeAsync()
    }
}

fun SignInFragment.sign(email: String, nickname: String, type: UserModel.SocialProvider) {
    // 닉네임 중복 확인
    Log.e("TAG", "${email}, ${nickname}, ${type}")
    UserLoader.shared.uniqueEmail(email) { result ->
        when (result) {
            true -> {
                val viewmodel = SignStep1ViewModel(email, nickname, type)
                val action = SignInFragmentDirections.actionSignInFragmentToSignStep1Fragment(viewmodel)
                findNavController().navigate(action)
            }
            false -> {
                // 소셜회원가입 (중복인 경우 로그인)
                UserLoader.shared.addUser(email = email, sns = type, nickname = nickname) {
                    UserLoader.shared.getUser { user ->
                        signin {
                            activity?.onBackPressed()
                        }
                    }
                }
            }
        }
    }
}
