package kr.beimsupicures.mycomment.components.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.alert
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser
import kr.beimsupicures.mycomment.extensions.setUser

class NicknameDialog(context: Context, val completionHandler: (UserModel) -> Unit) : Dialog(context) {

    var validation: Boolean = false
        get() = when {
            nicknameField.text.isEmpty() -> false
            nicknameField.text.length > 15 -> false
            !nicknameField.text.matches("""^[a-zA-Z0-9가-힣]+$""".toRegex()) -> false
            else -> true
        }

    lateinit var nicknameField: EditText
    lateinit var btnConfirm: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)
        setContentView(R.layout.dialog_nickname)

        nicknameField = findViewById(R.id.titleField)
        nicknameField.setText(BaseApplication.shared.getSharedPreferences().getUser()?.nickname)
        nicknameField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                validateUI()
            }

        })

        btnConfirm = findViewById(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            when (validation) {

                true -> {
                    UserLoader.shared.uniqueNickname("${nicknameField.text}") { unique ->
                        when (unique) {
                            true -> {
                                UserLoader.shared.updateNickname("${nicknameField.text}") { user ->
                                    BaseApplication.shared.getSharedPreferences().setUser(user)
                                    completionHandler(user)
                                    dismiss()
                                }
                            }
                            false -> {
                                BaseApplication.shared.getSharedPreferences().getUser()?.nickname.let { oldValue ->
                                    when (oldValue == "${nicknameField.text}") {
                                        true -> {
                                            dismiss()
                                        }
                                        false -> {
                                            Toast.makeText(context, "중복된 닉네임이 존재합니다.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun validateUI() {
        when (validation) {
            true -> {
                btnConfirm.background = ContextCompat.getDrawable(context, R.drawable.bg_dialog_button_enable)
            }

            false -> {
                btnConfirm.background = ContextCompat.getDrawable(context, R.drawable.bg_dialog_button_disable)
            }
        }
    }
}