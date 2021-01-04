package kr.beimsupicures.mycomment.components.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import kr.beimsupicures.mycomment.R

class ReportDialog(context: Context, val didSelectAt: (String) -> Unit) : DialogFragment() {

    lateinit var option1View: TextView
    lateinit var option2View: TextView
    lateinit var option3View: TextView
    lateinit var option4View: TextView
    lateinit var option5View: TextView
    lateinit var btnCancel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.setCancelable(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.dialog_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUI()
    }

    fun loadUI() {
        view?.let { view ->
            option1View = view.findViewById(R.id.check1View)
            option1View.setOnClickListener {
                didSelectAt("${option1View.text}")
                dialog?.dismiss()
            }
            option2View = view.findViewById(R.id.check2View)
            option2View.setOnClickListener {
                didSelectAt("${option2View.text}")
                dialog?.dismiss()
            }
            option3View = view.findViewById(R.id.check3View)
            option3View.setOnClickListener {
                didSelectAt("${option3View.text}")
                dialog?.dismiss()
            }
            option4View = view.findViewById(R.id.check4View)
            option4View.setOnClickListener {
                didSelectAt("${option4View.text}")
                dialog?.dismiss()
            }
            option5View = view.findViewById(R.id.option5View)
            option5View.setOnClickListener {
                didSelectAt("${option5View.text}")
                dialog?.dismiss()
            }
            btnCancel = view.findViewById(R.id.btnCancel)
            btnCancel.setOnClickListener {
                dialog?.dismiss()
            }
        }
    }
}
