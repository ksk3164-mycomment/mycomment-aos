package kr.beimsupicures.mycomment.controllers.signs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.navigation.findNavController
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.common.iamport.IAMPortManager
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.controllers.main.profile.password.ChangePasswordFragment

class CertificationFragment : BaseFragment() {

    enum class Category {
        nickname, password, signup
    }

    lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_certification, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            webView = view.findViewById(R.id.webView)
            webView.apply {
                settings.javaScriptEnabled = true
                addJavascriptInterface(JavaScriptInterface(), "AndroidBridge")
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
                isScrollbarFadingEnabled = true

                loadUrl("file:///android_asset/certifications.html")
            }
        }
    }

    inner class JavaScriptInterface {
        @JavascriptInterface
        fun resultAuth(impUid: String?) {
            impUid?.let { impUid ->
                IAMPortManager.shared.getAccessToken(
                    getString(R.string.impKey),
                    getString(R.string.impSecret)
                ) { token ->

                    IAMPortManager.shared.getAuthProfile(token, impUid) { profile ->
                        view?.let { view ->

                            when (CertificationFragmentArgs.fromBundle(requireArguments()).category) {
                                Category.nickname -> {

                                    val action =
                                        CertificationFragmentDirections.actionCertificationFragmentToForgotNicknameFragment(profile.unique_key)
                                    view.findNavController().navigate(action)
                                }

                                Category.password -> {
                                    CertificationFragmentArgs.fromBundle(requireArguments()).nickname?.let { nickname ->
                                        val action = NavigationDirections.actionGlobalChangePasswordFragment(nickname)
                                        view.findNavController().navigate(action)
                                    }
                                }

                                Category.signup -> {
                                    val action = CertificationFragmentDirections.actionCertificationFragmentToSignUpFragment(profile.unique_key,profile.name,profile.birth,profile.gender)
                                    view.findNavController().navigate(action)
                                }

                                else -> { }
                            }
                        }
                    }
                }
            }
        }
    }
}