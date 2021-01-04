package kr.beimsupicures.mycomment.controllers.main.detail.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.OpinionModel
import kr.beimsupicures.mycomment.components.adapters.OpinionAdapter
import kr.beimsupicures.mycomment.components.fragments.BaseFragment

class HistoryFragment : BaseFragment() {
    var history: MutableList<OpinionModel> = mutableListOf()

    lateinit var historyView: RecyclerView
    lateinit var historyAdapter: OpinionAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onResume() {
        super.onResume()

        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            historyAdapter = OpinionAdapter(activity, history, true)
            historyView = view.findViewById(R.id.historyView)
            historyView.layoutManager = LinearLayoutManager(context)
            historyView.adapter = historyAdapter
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        val user_id = HistoryFragmentArgs.fromBundle(requireArguments()).userId
        UserLoader.shared.getUserOpinion(user_id) { history ->
            this.history = history.filter { data -> data.opinion_id == null }.toMutableList()
            historyAdapter.items = this.history
            historyAdapter.notifyDataSetChanged()
        }
    }
}