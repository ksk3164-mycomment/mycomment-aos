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
import androidx.core.content.ContextCompat
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser

class IntroDialog(context: Context, val completionHandler: (UserModel) -> Unit) : Dialog(context) {

    var validation: Boolean = false
        get() = when {
            introField.text.isEmpty() -> false
            else -> true
        }

    lateinit var introField: EditText
    lateinit var btnConfirm: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)
        setContentView(R.layout.dialog_intro)

        introField = findViewById(R.id.titleField)
        introField.setText(BaseApplication.shared.getSharedPreferences().getUser()?.intro)
        introField.addTextChangedListener(object : TextWatcher {
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
                    UserLoader.shared.updateIntro("${introField.text}") { user ->
                        completionHandler(user)
                        dismiss()
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