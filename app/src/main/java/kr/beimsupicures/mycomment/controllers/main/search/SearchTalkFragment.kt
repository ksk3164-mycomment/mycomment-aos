package kr.beimsupicures.mycomment.controllers.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.components.adapters.TalkGridAdapter
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.hideKeyboard

class SearchTalkFragment : BaseFragment() {

    var talk: MutableList<TalkModel> = mutableListOf()

    lateinit var resultView: RecyclerView
    lateinit var resultAdapter: TalkGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_talk, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            resultAdapter = TalkGridAdapter(activity, talk)
            resultView = view.findViewById(R.id.resultView)
            resultView.layoutManager = GridLayoutManager(context,3)
            resultView.adapter = resultAdapter
        }
    }

    override fun fetchModel() {
        super.fetchModel()
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

}