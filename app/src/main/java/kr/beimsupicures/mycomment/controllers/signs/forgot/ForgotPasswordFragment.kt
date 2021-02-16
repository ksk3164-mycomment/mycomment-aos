package kr.beimsupicures.mycomment.controllers.signs.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.controllers.signs.CertificationFragment
import kr.beimsupicures.mycomment.extensions.hideKeyboard

class ForgotPasswordFragment : BaseFragment() {

    var validation: Boolean = false
        get() = when {
            nicknameField.text.isEmpty() -> {
                Toast.makeText(context, "아이디를 확인해주세요", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    lateinit var nicknameField: EditText
    lateinit var btnConfirm: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            nicknameField = view.findViewById(R.id.titleField)

            btnConfirm = view.findViewById(R.id.btnConfirm)
            btnConfirm.setOnClickListener {

                hideKeyboard()

                when (validation) {
                    true -> {

                        val action = NavigationDirections.actionGlobalCertificationFragment(CertificationFragment.Category.password,nicknameField.text.toString())
                        view.findNavController().navigate(action)
                    }
                }
            }
        }
    }
}
