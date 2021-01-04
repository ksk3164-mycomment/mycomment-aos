package kr.beimsupicures.mycomment.controllers.signs.forgot

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.copyText

class ForgotNicknameFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_nickname, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        ForgotNicknameFragmentArgs.fromBundle(requireArguments()).uniqueKey?.let { uniqueKey ->

            UserLoader.shared.uniqueUser((uniqueKey)) { unique ->
                when (unique) {
                    true -> {
                        Toast.makeText(context, "가입한 적이 없습니다.", Toast.LENGTH_SHORT).show()

                        Handler().postDelayed({

                            activity?.let { activity ->
                                Navigation.findNavController(activity, R.id.nav_host_fragment)
                                    .navigate(R.id.action_global_signInFragment)
                            }

                        }, 2000)
                    }

                    false -> {
                        UserLoader.shared.findUser(uniqueKey) { user ->
                            context?.copyText(user.nickname)

                            Toast.makeText(context, "아이디를 클립보드에 복사하였습니다.", Toast.LENGTH_SHORT).show()

                            Handler().postDelayed({

                                activity?.let { activity ->
                                    Navigation.findNavController(activity, R.id.nav_host_fragment)
                                        .navigate(R.id.action_global_signInFragment)
                                }

                            }, 2000)
                        }
                    }
                }
            }
        }
    }
}
