package kr.beimsupicures.mycomment.controllers.main.detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.AgendaLoader
import kr.beimsupicures.mycomment.api.loaders.LinkLoader
import kr.beimsupicures.mycomment.api.loaders.OpinionLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.components.adapters.DetailAdapter
import kr.beimsupicures.mycomment.components.adapters.LinkAdapter
import kr.beimsupicures.mycomment.components.adapters.OpinionAdapter
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser
import kr.beimsupicures.mycomment.extensions.hideKeyboard
import kr.beimsupicures.mycomment.extensions.popup

class DetailFragment : BaseFragment() {

    var validation: Boolean = false
        get() = when {
            messageField.text.isEmpty() -> false
            else -> true
        }

    var agenda: MutableList<AgendaModel> = mutableListOf()
    var link: List<LinkModel> = arrayListOf()
    var users: List<UserModel> = arrayListOf()
    var opinion: MutableList<OpinionModel> = mutableListOf()

    lateinit var agendaView: RecyclerView
    lateinit var detailAdapter: DetailAdapter

    lateinit var linkView: RecyclerView
    var linkAdapter = LinkAdapter(link)

    lateinit var user1Label: TextView
    lateinit var user2Label: TextView
    lateinit var user3Label: TextView
    lateinit var moreView: ImageView

    lateinit var btnNew: TextView
    lateinit var btnRank: TextView

    lateinit var opinionView: RecyclerView
    lateinit var opinionAdapter: OpinionAdapter

    lateinit var messageField: EditText
    lateinit var btnSend: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onResume() {
        super.onResume()

        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            detailAdapter = DetailAdapter(activity, agenda)
            agendaView = view.findViewById(R.id.agendaDetailView)
            agendaView.layoutManager = LinearLayoutManager(context)
            agendaView.adapter = detailAdapter

            linkView = view.findViewById(R.id.agendaDetailLinkView)
            linkView.layoutManager = LinearLayoutManager(context)
            linkView.adapter = linkAdapter

            user1Label = view.findViewById(R.id.user1Label)
            user2Label = view.findViewById(R.id.user2Label)
            user3Label = view.findViewById(R.id.user3Label)
            moreView = view.findViewById(R.id.moreView)
            moreView.setOnClickListener { view ->

                agenda.firstOrNull()?.id?.let { id ->

                    val action =
                        DetailFragmentDirections.actionDetailFragmentToBestUserFragment()
                            .setAgendaId(id)
                    view.findNavController().navigate(action)
                }

            }

            btnNew = view.findViewById(R.id.btnNew)
            btnNew.setOnClickListener {
                btnNew.setTypeface(ResourcesCompat.getFont(view.context, R.font.gyeonggi_batang_b))
                btnRank.setTypeface(ResourcesCompat.getFont(view.context, R.font.gyeonggi_batang_r))

                opinionAdapter.items = opinion.sortedBy { data -> data.id }.reversed().toMutableList()
                opinionAdapter.notifyDataSetChanged()
            }
            btnRank = view.findViewById(R.id.btnRank)
            btnRank.setOnClickListener {
                btnNew.setTypeface(ResourcesCompat.getFont(view.context, R.font.gyeonggi_batang_r))
                btnRank.setTypeface(ResourcesCompat.getFont(view.context, R.font.gyeonggi_batang_b))

                opinionAdapter.items = opinion.sortedBy { data -> data.pick_count }.reversed().toMutableList()
                opinionAdapter.notifyDataSetChanged()
            }

            opinionAdapter = OpinionAdapter(activity, opinion, showCircieView = false)
            opinionView = view.findViewById(R.id.opinionView)
            opinionView.layoutManager = LinearLayoutManager(context)
            opinionView.adapter = opinionAdapter

            messageField = view.findViewById(R.id.messageField)
            messageField.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    context?.let { context ->
                        btnSend.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                if (validation) R.drawable.send else R.drawable.send_g
                            )
                        )
                    }
                }
            })
            btnSend = view.findViewById(R.id.btnSend)
            btnSend.setOnClickListener {

                BaseApplication.shared.getSharedPreferences().getUser()?.let {

                    when (validation) {
                        true -> {
                            agenda.firstOrNull()?.id?.let { agendaId ->

                                OpinionLoader.shared.addOpinion(agendaId, messageField.text.toString()) { opinion ->
                                    opinion?.let { opinion -> this.opinion.add(0, opinion) }
                                    opinionAdapter.notifyDataSetChanged()
                                    messageField.setText("")
                                    hideKeyboard()
                                }
                                /*
                                OpinionLoader.shared.duplicate(agendaId) { duplicate ->
                                    when (duplicate) {
                                        true -> {
                                            Toast.makeText(context, "코멘트는 1주제당 1번만 작성가능합니다", Toast.LENGTH_SHORT).show()
                                            messageField.setText("")
                                            hideKeyboard()
                                        }

                                        false -> {
                                            OpinionLoader.shared.addOpinion(agendaId, messageField.text.toString()) { opinion ->
                                                opinion?.let { opinion -> this.opinion.add(0, opinion) }
                                                opinionAdapter.notifyDataSetChanged()
                                                messageField.setText("")
                                                hideKeyboard()
                                            }
                                        }
                                    }
                                }
                                */
                            }
                        }

                        false -> { }
                    }

                } ?: run {

                    activity?.let { activity ->
                        activity.popup("로그인하시겠습니까?", "로그인") {
                            Navigation.findNavController(activity, R.id.nav_host_fragment)
                                .navigate(R.id.action_global_signInFragment)
                        }
                    }
                }
            }
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        val agendaId = DetailFragmentArgs.fromBundle(requireArguments()).agendaId
        AgendaLoader.shared.getAgenda(agendaId) { agenda ->
            this.agenda = mutableListOf(agenda)
            detailAdapter.items = this.agenda
            detailAdapter.notifyDataSetChanged()
        }

        LinkLoader.shared.getLink(agendaId) { link ->
            this.link = link
            linkAdapter.items = this.link
            linkAdapter.notifyDataSetChanged()
        }

        OpinionLoader.shared.getOpinionUserRankList(agendaId) { users ->
            this.users = users

            for (i in users.indices) {

                when (i) {
                    0 -> {
                        user1Label.text = users[i].nameOnly()
                        user1Label.visibility = View.VISIBLE
                    }
                    1 -> {
                        user2Label.text = users[i].nameOnly()
                        user2Label.visibility = View.VISIBLE
                    }

                    2 -> {
                        user3Label.text = users[i].nameOnly()
                        user3Label.visibility = View.VISIBLE
                    }
                    else -> {
                        moreView.visibility = View.VISIBLE
                    }
                }
            }
        }

        OpinionLoader.shared.getOpinionList(agendaId) { opinion ->
            this.opinion = opinion.filter { data -> data.opinion_id == null }.toMutableList()
            opinionAdapter.items = this.opinion
            opinionAdapter.notifyDataSetChanged()
        }
    }
}

