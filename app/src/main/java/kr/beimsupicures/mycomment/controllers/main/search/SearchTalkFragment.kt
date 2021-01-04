package kr.beimsupicures.mycomment.controllers.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.SearchLoader
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.components.adapters.TalkAdapter
import kr.beimsupicures.mycomment.components.fragments.BaseFragment

class SearchTalkFragment : BaseFragment() {

    var talk: MutableList<TalkModel> = mutableListOf()

    lateinit var resultView: RecyclerView
    lateinit var resultAdapter: TalkAdapter

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
            resultAdapter = TalkAdapter(activity, talk)
            resultView = view.findViewById(R.id.resultView)
            resultView.layoutManager = LinearLayoutManager(context)
            resultView.adapter = resultAdapter
        }
    }

    override fun fetchModel() {
        super.fetchModel()

    }
}