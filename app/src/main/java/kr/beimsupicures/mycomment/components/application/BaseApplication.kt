package kr.beimsupicures.mycomment.components.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.kakao.auth.*

class BaseApplication : Application() {

    var currentActivity: Activity? = null

    companion object {
        lateinit var shared: BaseApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        shared = this

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
                currentActivity = null
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {

            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

        })

        KakaoSDK.init(KakaoSDKAdapter())
    }

    fun context(): Context = applicationContext

    inner class KakaoSDKAdapter : KakaoAdapter() {
        override fun getSessionConfig(): ISessionConfig {
            return object : ISessionConfig {
                override fun getAuthTypes(): Array<AuthType> {
                    return arrayOf(AuthType.KAKAO_TALK)
                }

                override fun isSecureMode(): Boolean {
                    return false
                }

                override fun isUsingWebviewTimer(): Boolean {
                    return false
                }

                override fun getApprovalType(): ApprovalType? {
                    return ApprovalType.INDIVIDUAL
                }

                override fun isSaveFormData(): Boolean {
                    return true
                }
            }
        }

        override fun getApplicationConfig(): IApplicationConfig {
            return object : IApplicationConfig {
                override fun getApplicationContext(): Context {
                    return shared.context()
                }
            }
        }
    }
}
