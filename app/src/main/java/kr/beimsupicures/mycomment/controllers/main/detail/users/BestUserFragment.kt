package kr.beimsupicures.mycomment.controllers.main.detail.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.OpinionLoader
import kr.beimsupicures.mycomment.api.models.OpinionModel
import kr.beimsupicures.mycomment.components.adapters.OpinionAdapter
import kr.beimsupicures.mycomment.components.fragments.BaseFragment

class BestUserFragment : BaseFragment() {

    var opinion: MutableList<OpinionModel> = mutableListOf()

    lateinit var opinionView: RecyclerView
    lateinit var opinionAdapter: OpinionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_best_user, container, false)
    }

    override fun onResume() {
        super.onResume()

        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            opinionAdapter = OpinionAdapter(activity, opinion)
            opinionView = view.findViewById(R.id.opinionView)
            opinionView.layoutManager = LinearLayoutManager(context)
            opinionView.adapter = opinionAdapter
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        val agendaId = BestUserFragmentArgs.fromBundle(requireArguments()).agendaId
        OpinionLoader.shared.getOpinionRankList(agendaId) { opinion ->
            this.opinion = opinion.toMutableList()
            opinionAdapter.items = this.opinion
            opinionAdapter.notifyDataSetChanged()
        }
    }
}