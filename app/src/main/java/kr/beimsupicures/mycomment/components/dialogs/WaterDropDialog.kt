package kr.beimsupicures.mycomment.components.dialogs

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_waterdrop.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.extensions.dp

class WaterDropDialog(
    val type: NotificationSetting,
    val title: String
) : DialogFragment() {

    enum class NotificationSetting {
        allowed, denied
    }

    lateinit var btnConfirm: TextView
    lateinit var btnAlarmSetting: TextView
    lateinit var tvTitle: TextView
    lateinit var tvAlarmSetting: TextView

    companion object {

        fun newInstance(
            type: NotificationSetting,
            title: String
        ): WaterDropDialog {
            val dialog = WaterDropDialog(type, title)
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            300.dp,
            260.dp
        )
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.dialog_waterdrop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUI()
    }

    fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context?.packageName))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun loadUI() {
        view?.let { view ->
            tvTitle = view.findViewById(R.id.tv_title)
            btnConfirm = view.findViewById(R.id.btnConfirm)
//            btnAlarmSetting = view.findViewById(R.id.btn_alarm_setting)
//            tvAlarmSetting = view.findViewById(R.id.tv_alarm_setting)
            btnConfirm.setOnClickListener {
                dialog?.dismiss()
            }
//            btnAlarmSetting.setOnClickListener {
//                dialog?.dismiss()
//                openNotificationSettings()
//            }
            tvTitle.text = title
//            when(type) {
//                NotificationSetting.allowed -> {
//                    btnConfirm.visibility = View.VISIBLE
//                    btnAlarmSetting.visibility = View.GONE
//                    tvAlarmSetting.visibility = View.GONE
//                }
//                NotificationSetting.denied -> {
//                    btnConfirm.visibility = View.GONE
//                    btnAlarmSetting.visibility = View.VISIBLE
//                    tvAlarmSetting.visibility = View.VISIBLE
//                }
//            }
        }
    }
}
