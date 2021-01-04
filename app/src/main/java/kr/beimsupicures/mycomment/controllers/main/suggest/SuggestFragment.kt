package kr.beimsupicures.mycomment.controllers.main.suggest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.AgendaLoader
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.afterTextChanged

class SuggestFragment : BaseFragment() {

    var validation: Boolean = false
        get() = when {
            suggestField.text.isEmpty() -> false
            else -> true
        }

    lateinit var titleLabel: TextView
    lateinit var suggestField: EditText
    lateinit var descLabel: TextView
    lateinit var btnConfirm: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_suggest, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            titleLabel = view.findViewById(R.id.titleLabel)
            suggestField = view.findViewById(R.id.suggestField)
            suggestField.afterTextChanged { text ->
                btnConfirm.setBackgroundResource(if (validation) R.drawable.bg_button_enable else R.drawable.bg_button_disable)
            }
            descLabel = view.findViewById(R.id.descLabel)
            btnConfirm = view.findViewById(R.id.btnConfirm)
            btnConfirm.setOnClickListener {
                if (validation) {
                    AgendaLoader.shared.suggestAgenda(suggestField.text.toString()) {
                        activity?.onBackPressed()
                    }
                }
            }
        }
    }
}