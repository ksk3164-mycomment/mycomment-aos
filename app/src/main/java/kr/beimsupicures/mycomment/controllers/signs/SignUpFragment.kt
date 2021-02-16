package kr.beimsupicures.mycomment.controllers.signs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.TermModel
import kr.beimsupicures.mycomment.common.iamport.IAMPortCertificationResult
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.toString

class SignUpFragment : BaseFragment() {
    var validation: Boolean = false
        get() = when {
            nicknameField.text.isEmpty() -> {
                Toast.makeText(context, "아이디 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                false
            }

            passwordField.text.isEmpty() -> {
                Toast.makeText(context, "아이디 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                false
            }
            passwordField.text.toString() != confirmField.text.toString() -> {
                Toast.makeText(context, "비밀번호가 서로 맞지 않습니다", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    lateinit var nicknameField: EditText
    lateinit var passwordField: EditText
    lateinit var confirmField: EditText
    lateinit var btnService: TextView
    lateinit var btnPrivacy: TextView
    lateinit var btnSignUp: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            nicknameField = view.findViewById(R.id.titleField)
            passwordField = view.findViewById(R.id.birthField)
            confirmField = view.findViewById(R.id.confirmField)

            btnService = view.findViewById(R.id.btnService)
            btnService.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalTermFragment(TermModel.Category.service)
                view.findNavController().navigate(action)
            }

            btnPrivacy = view.findViewById(R.id.btnPrivacy)
            btnPrivacy.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalTermFragment(TermModel.Category.privacy)
                view.findNavController().navigate(action)
            }
            btnSignUp = view.findViewById(R.id.btnSignUp)
            btnSignUp.setOnClickListener {

                when (validation) {
                    true -> {
                        UserLoader.shared.uniqueNickname("${nicknameField.text}") { unique ->
                            when (unique) {
                                true -> {

                                    SignUpFragmentArgs.fromBundle(requireArguments())?.let { profile ->
                                        UserLoader.shared.addUser(
                                            profile.uniqueKey,
                                            profile.name,
                                            profile.birth.toLong().toString("yyyy-MM-dd"),
                                            IAMPortCertificationResult.Gender.valueOf(profile.gender.toString()),
                                            "${nicknameField.text}",
                                            null,
                                            "${passwordField.text}"

                                        ) { accessToken ->

                                            UserLoader.shared.getUser {
                                                Navigation.findNavController(view).popBackStack(R.id.signInFragment, false)
                                                Navigation.findNavController(view).popBackStack()
                                            }
                                        }
                                    }
                                }

                                false -> {
                                    Toast.makeText(context, "이미 등록된 아이디입니다", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    false -> {
                        Toast.makeText(context, "아이디 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}