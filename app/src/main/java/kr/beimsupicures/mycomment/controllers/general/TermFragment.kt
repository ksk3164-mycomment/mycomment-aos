package kr.beimsupicures.mycomment.controllers.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.TermLoader
import kr.beimsupicures.mycomment.api.models.TermModel
import kr.beimsupicures.mycomment.components.fragments.BaseFragment

class TermFragment : BaseFragment() {

    lateinit var term: TermModel

    lateinit var contentLabel: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_term, container, false)
    }

    override fun onResume() {
        super.onResume()

        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            contentLabel = view.findViewById(R.id.contentLabel)
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        val category = TermFragmentArgs.fromBundle(requireArguments()).category

        TermLoader.shared.getTerm(category) { term ->
            this.term = term
            contentLabel.text = this.term.content
        }
    }
}