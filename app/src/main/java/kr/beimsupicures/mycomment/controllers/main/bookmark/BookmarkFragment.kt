package kr.beimsupicures.mycomment.controllers.main.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_talk.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.AgendaModel
import kr.beimsupicures.mycomment.api.models.OpinionModel
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.api.models.WatchModel
import kr.beimsupicures.mycomment.components.adapters.*
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser

class BookmarkFragment : BaseFragment() {

    var history: MutableList<OpinionModel> = mutableListOf()
    var agenda: MutableList<AgendaModel> = mutableListOf()
    var opinion: MutableList<OpinionModel> = mutableListOf()
    var talk: MutableList<TalkModel> = mutableListOf()
    var watch: MutableList<WatchModel> = mutableListOf()

    lateinit var btnHistory: TextView
    lateinit var btnBookmark: TextView

    lateinit var historyView: RecyclerView
    lateinit var historyAdapter: OpinionAdapter

    lateinit var bookmarkWrapperView: LinearLayout

    lateinit var btnAgenda: TextView
    lateinit var btnOpinion: TextView
    lateinit var btnTalk: TextView
    lateinit var btnWatch: TextView

    lateinit var agendaView: RecyclerView
    lateinit var agendaAdapter: AgendaAdapter

    lateinit var opinionView: RecyclerView
    lateinit var opinionAdapter: OpinionAdapter

    lateinit var talkView: RecyclerView
    lateinit var talkAdapter: TalkAdapter2

    lateinit var watchView: RecyclerView
    lateinit var watchAdapter: WatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
//            btnHistory = view.findViewById(R.id.btnHistory)
//            btnHistory.setOnClickListener {
//                historyView.visibility = View.VISIBLE
//                bookmarkWrapperView.visibility = View.GONE
//
//                context?.let { btnHistory.setTextColor(ContextCompat.getColor(it, R.color.colorTextSegment)) }
//                btnHistory.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_b
//                    )
//                )
//                context?.let { btnBookmark.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnBookmark.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//            }
//            btnBookmark = view.findViewById(R.id.btnBookmark)
//            btnBookmark.setOnClickListener {
//                historyView.visibility = View.GONE
//                bookmarkWrapperView.visibility = View.VISIBLE
//
//                context?.let { btnHistory.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnHistory.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnBookmark.setTextColor(ContextCompat.getColor(it, R.color.colorTextSegment)) }
//                btnBookmark.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_b
//                    )
//                )
//            }
//            //
//            historyAdapter = OpinionAdapter(activity, history, false, true)
//            historyView = view.findViewById(R.id.historyView)
//            historyView.layoutManager = LinearLayoutManager(context)
//            historyView.adapter = historyAdapter
//
//            bookmarkWrapperView = view.findViewById(R.id.bookmarkWrapperView)
//            bookmarkWrapperView.visibility = View.VISIBLE
//
//            btnAgenda = view.findViewById(R.id.btnAgenda)
//            btnAgenda.setOnClickListener {
//                context?.let { btnAgenda.setTextColor(ContextCompat.getColor(it, R.color.colorTextSegment)) }
//                btnAgenda.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_b
//                    )
//                )
//                context?.let { btnOpinion.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnOpinion.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnTalk.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnTalk.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnWatch.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnWatch.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//
//                agendaView.visibility = View.VISIBLE
//                opinionView.visibility = View.GONE
//                talkView.visibility = View.GONE
//                watchView.visibility = View.GONE
//            }
//            btnOpinion = view.findViewById(R.id.btnOpinion)
//            btnOpinion.setOnClickListener {
//                context?.let { btnAgenda.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnAgenda.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnOpinion.setTextColor(ContextCompat.getColor(it, R.color.colorTextSegment)) }
//                btnOpinion.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_b
//                    )
//                )
//                context?.let { btnTalk.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnTalk.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnWatch.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnWatch.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//
//                agendaView.visibility = View.GONE
//                opinionView.visibility = View.VISIBLE
//                talkView.visibility = View.GONE
//                watchView.visibility = View.GONE
//            }
//            btnTalk = view.findViewById(R.id.btnTalk)
//            btnTalk.setOnClickListener {
//                context?.let { btnAgenda.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnAgenda.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnOpinion.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnOpinion.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnTalk.setTextColor(ContextCompat.getColor(it, R.color.colorTextSegment)) }
//                btnTalk.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_b
//                    )
//                )
//                context?.let { btnWatch.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnWatch.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//
//                agendaView.visibility = View.GONE
//                opinionView.visibility = View.GONE
//                talkView.visibility = View.VISIBLE
//                watchView.visibility = View.GONE
//
//                getUserBookmarkTalk()
//            }
//            btnWatch = view.findViewById(R.id.btnWatch)
//            btnWatch.setOnClickListener {
//                context?.let { btnAgenda.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnAgenda.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnOpinion.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnOpinion.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnTalk.setTextColor(ContextCompat.getColor(it, R.color.colorTextNone)) }
//                btnTalk.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_r
//                    )
//                )
//                context?.let { btnWatch.setTextColor(ContextCompat.getColor(it, R.color.colorTextSegment)) }
//                btnWatch.setTypeface(
//                    ResourcesCompat.getFont(
//                        view.context,
//                        R.font.gyeonggi_batang_b
//                    )
//                )
//                agendaView.visibility = View.GONE
//                opinionView.visibility = View.GONE
//                talkView.visibility = View.GONE
//                watchView.visibility = View.VISIBLE
//
//                getUserBookmarkWatch()
//            }

//            agendaAdapter = AgendaAdapter(activity, agenda)
//            agendaView = view.findViewById(R.id.agendaView)
//            agendaView.layoutManager = LinearLayoutManager(context)
//            agendaView.adapter = agendaAdapter
//            agendaView.visibility = View.GONE
//
//            opinionAdapter = OpinionAdapter(activity, opinion, true, true)
//            opinionView = view.findViewById(R.id.opinionView)
//            opinionView.layoutManager = LinearLayoutManager(context)
//            opinionView.adapter = opinionAdapter
//            opinionView.visibility = View.GONE

            talkAdapter = TalkAdapter2(activity, talk)
            talkView = view.findViewById(R.id.talkView)
            talkView.layoutManager = GridLayoutManager(context,3)
            talkView.adapter = talkAdapter
            talkView.visibility = View.VISIBLE

//            watchAdapter = WatchAdapter(activity, watch)
//            watchView = view.findViewById(R.id.watchView)
//            watchView.layoutManager = LinearLayoutManager(context)
//            watchView.adapter = watchAdapter
//            watchView.visibility = View.GONE
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        BaseApplication.shared.getSharedPreferences().getUser()?.let { user ->

            val user_id = user.id
            UserLoader.shared.getUserOpinion(user_id) { history ->
                this.history = history.filter { data -> data.opinion_id == null }.toMutableList()
                historyAdapter.items = this.history
                historyAdapter.notifyDataSetChanged()
            }

            UserLoader.shared.getUserAgendaOpinion { agenda ->
                this.agenda = agenda.toMutableList()
                agendaAdapter.items = this.agenda
                agendaAdapter.notifyDataSetChanged()
            }

            UserLoader.shared.getUserBookmarkOpinion { opinion->
                this.opinion = opinion.toMutableList()
                opinionAdapter.items = this.opinion
                opinionAdapter.notifyDataSetChanged()
            }

            getUserBookmarkTalk()
            getUserBookmarkWatch()
        }
    }
}

fun BookmarkFragment.getUserBookmarkTalk() {
    UserLoader.shared.getUserBookmarkTalk { talk ->
        this.talk = talk.toMutableList()
        talkAdapter.items = this.talk
        talkAdapter.notifyDataSetChanged()
    }
}

fun BookmarkFragment.getUserBookmarkWatch() {
    UserLoader.shared.getUserBookmarkWatch { watch ->

        this.watch = watch.toMutableList()
        watchAdapter.items = this.watch
        watchAdapter.notifyDataSetChanged()
    }
}