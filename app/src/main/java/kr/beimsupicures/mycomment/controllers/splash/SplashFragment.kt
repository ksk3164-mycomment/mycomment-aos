package kr.beimsupicures.mycomment.controllers.splash

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.kakao.util.helper.Utility.getKeyHash
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.signin
import kr.beimsupicures.mycomment.controllers.signs.SignInFragment
import kr.beimsupicures.mycomment.extensions.getAccessToken
import kr.beimsupicures.mycomment.extensions.getSharedPreferences

class SplashFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        Log.e("getKeyHash", getKeyHash(context))

        BaseApplication.shared.getSharedPreferences().getAccessToken()?.let {
            signin { result ->
                when (result) {

                    true -> {
                        Handler().postDelayed({
                            view?.let { view ->
                                Navigation.findNavController(view)
                                    .navigate(R.id.action_splashFragment_to_talkFragment)
                            }
                        }, 1000)
                    }
                }
            }

        } ?: run {
            Handler().postDelayed({
                view?.let { view ->
                    Navigation.findNavController(view)
                        .navigate(R.id.action_splashFragment_to_talkFragment)
                }
            }, 1000)
        }
    }
}

