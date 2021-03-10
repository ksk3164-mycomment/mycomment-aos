package kr.beimsupicures.mycomment.controllers.main.detail.webview

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.api.models.WatchModel
import kr.beimsupicures.mycomment.components.fragments.BaseFragment


class WebViewFragment: BaseFragment() {

    var talk: TalkModel? = null
    var watch: WatchModel? = null

    lateinit var wv_banner: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun loadUI() {
        super.loadUI()
        view?.let { view ->
            wv_banner = view.findViewById(R.id.wv_banner)
            talk?.let { talk ->
                val talkURL = "https://mycomment.page.link/mc_talk_" + talk.id
                val webSettings: WebSettings = wv_banner.settings
                webSettings.javaScriptEnabled = true
                wv_banner.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        try {
                            val originURL = url?.split(";")?.get(4)?.split("=")?.get(1)
                            wv_banner.loadUrl(originURL)
                        } catch (e: IndexOutOfBoundsException) {
                            return false
                        }
                        return true
                    }
                }
                wv_banner.loadUrl(talkURL)
            }
            watch?.let { watch ->
                val watchURL = "https://mycomment.page.link/mc_watch_" + watch.id
                val webSettings: WebSettings = wv_banner.settings
                webSettings.javaScriptEnabled = true
                wv_banner.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        try {
                            val originURL = url?.split(";")?.get(4)?.split("=")?.get(1)
                            wv_banner.loadUrl(originURL)
                        } catch (e: IndexOutOfBoundsException) {
                            return false
                        }
                        return true
                    }
                }
                wv_banner.loadUrl(watchURL)
            }
        }
    }

    override fun loadModel() {
        super.loadModel()
        talk = WebViewFragmentArgs.fromBundle(requireArguments()).talk
        watch = WebViewFragmentArgs.fromBundle(requireArguments()).watch
    }

    private fun appInstalledOrNot(context: Context, uri: String): Boolean {
        val pm: PackageManager = context.packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }
}