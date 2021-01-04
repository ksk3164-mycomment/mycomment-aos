package kr.beimsupicures.mycomment.components.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kr.beimsupicures.mycomment.R

class AgreeDialog(
    val didUpdatedAt: (Boolean, Boolean, Boolean, Boolean) -> Unit,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit,
    val onSelected: (Int) -> Unit
) : DialogFragment() {

    val validation: Boolean
        get() {
            return check1View.isChecked && check2View.isChecked && check3View.isChecked
        }

    var check1: Boolean = false
    var check2: Boolean = false
    var check3: Boolean = false
    var check4: Boolean = false

    lateinit var dismissView: View
    lateinit var checkAllView: CheckBox
    lateinit var check1View: CheckBox
    lateinit var check2View: CheckBox
    lateinit var check3View: CheckBox
    lateinit var check4View: CheckBox
    lateinit var check1Label: TextView
    lateinit var check2Label: TextView
    lateinit var check3Label: TextView
    lateinit var check4Label: TextView
    lateinit var btnConfirm: TextView

    companion object {

        fun newInstance(
            check1: Boolean,
            check2: Boolean,
            check3: Boolean,
            check4: Boolean,
            didUpdateAt: (Boolean, Boolean, Boolean, Boolean) -> Unit,
            onConfirm: () -> Unit,
            onDismiss: () -> Unit,
            onSelected: (Int) -> Unit
        ): AgreeDialog {
            val dialog = AgreeDialog(didUpdateAt, onConfirm, onDismiss, onSelected)

            val args = Bundle()
            args.putBoolean("check1", check1)
            args.putBoolean("check2", check2)
            args.putBoolean("check3", check3)
            args.putBoolean("check4", check4)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.CustomDialog)

        arguments?.let { arguments ->
            check1 = arguments.getBoolean("check1")
            check2 = arguments.getBoolean("check2")
            check3 = arguments.getBoolean("check3")
            check4 = arguments.getBoolean("check4")
        }
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

        return inflater.inflate(R.layout.dialog_agree, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUI()
    }

    fun loadUI() {
        view?.let { view ->
            dismissView = view.findViewById(R.id.dismissView)
            dismissView.setOnClickListener {
                onDismiss()
                dialog?.dismiss()
            }

            checkAllView = view.findViewById(R.id.checkAllView)
            checkAllView.setOnClickListener {
                check1View.isChecked = checkAllView.isChecked
                check2View.isChecked = checkAllView.isChecked
                check3View.isChecked = checkAllView.isChecked
                check4View.isChecked = checkAllView.isChecked

                validateUI()
                didUpdatedAt(check1View.isChecked, check2View.isChecked, check3View.isChecked, check4View.isChecked)
            }
            check1View = view.findViewById(R.id.check1View)
            check1View.isChecked = check1
            check1View.setOnClickListener {
                validateUI()
                checkAllView.isChecked = validation
                didUpdatedAt(check1View.isChecked, check2View.isChecked, check3View.isChecked, check4View.isChecked)
            }
            check2View = view.findViewById(R.id.check2View)
            check2View.isChecked = check2
            check2View.setOnClickListener {
                validateUI()
                checkAllView.isChecked = validation
                didUpdatedAt(check1View.isChecked, check2View.isChecked, check3View.isChecked, check4View.isChecked)
            }
            check3View = view.findViewById(R.id.check3View)
            check3View.isChecked = check3
            check3View.setOnClickListener {
                validateUI()
                checkAllView.isChecked = validation
                didUpdatedAt(check1View.isChecked, check2View.isChecked, check3View.isChecked, check4View.isChecked)
            }
            check4View = view.findViewById(R.id.check4View)
            check4View.isChecked = check4

            check1Label = view.findViewById(R.id.check1Label)
            check1Label.setOnClickListener { view ->
                dialog?.dismiss()
                onSelected(1)
            }
            check2Label = view.findViewById(R.id.check2Label)
            check2Label.setOnClickListener { view ->
                dialog?.dismiss()
                onSelected(2)
            }
            check3Label = view.findViewById(R.id.check3Label)
            check3Label.setOnClickListener { view ->
                dialog?.dismiss()
                onSelected(3)
            }
            check4Label = view.findViewById(R.id.check4Label)
            btnConfirm = view.findViewById(R.id.btnConfirm)
            btnConfirm.setOnClickListener {
                if (validation) {
                    onConfirm()
                    dialog?.dismiss()
                }
            }

            validateUI()
        }
    }

    fun validateUI() {
        when (validation) {
            true -> {
                btnConfirm.background =
                    ContextCompat.getDrawable(context!!, R.drawable.bg_dialog_button_enable)
            }

            false -> {
                btnConfirm.background =
                    ContextCompat.getDrawable(context!!, R.drawable.bg_dialog_button_disable)
            }
        }
    }
}
