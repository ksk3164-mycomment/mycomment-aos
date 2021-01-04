package kr.beimsupicures.mycomment.controllers.main.profile.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.hideKeyboard

class ChangePasswordFragment : BaseFragment() {

    enum class Category {
        change, forgot
    }

    var validation: Boolean = false
        get() = when {
            passwordField.text.isEmpty() -> {
                Toast.makeText(context, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                false
            }
            confirmField.text.isEmpty() -> {
                Toast.makeText(context, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                false
            }
            passwordField.text.toString() != confirmField.text.toString() -> {
                Toast.makeText(context, "비밀번호가 서로 맞지 않습니다", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    lateinit var passwordField: EditText
    lateinit var confirmField: EditText
    lateinit var btnConfirm: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            passwordField = view.findViewById(R.id.birthField)
            confirmField = view.findViewById(R.id.confirmField)

            btnConfirm = view.findViewById(R.id.btnConfirm)
            btnConfirm.setOnClickListener {

                hideKeyboard()

                when (validation) {
                    true -> {

                        ChangePasswordFragmentArgs.fromBundle(requireArguments()).category?.let { category ->

                            when (category) {

                                Category.change -> {
                                    UserLoader.shared.updatePassword("${passwordField.text}") { user ->
                                        Toast.makeText(context, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show()
                                        activity?.onBackPressed()
                                    }
                                }

                                Category.forgot -> {
                                    ChangePasswordFragmentArgs.fromBundle(requireArguments()).nickname?.let { nickname ->
                                        UserLoader.shared.forgotPassword(nickname, "${passwordField.text}") { user ->
                                            Toast.makeText(context, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show()

                                            Navigation.findNavController(view).popBackStack(R.id.signInFragment, false)
                                        }
                                    }
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