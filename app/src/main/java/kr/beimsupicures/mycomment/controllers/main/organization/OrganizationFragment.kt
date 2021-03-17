package kr.beimsupicures.mycomment.controllers.main.organization

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.SearchLoader
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.components.adapters.TalkGridAdapter
import kr.beimsupicures.mycomment.components.adapters.TalkTodayAdapter
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.startLoadingUI
import kr.beimsupicures.mycomment.components.fragments.stopLoadingUI
import kr.beimsupicures.mycomment.controllers.main.bookmark.getUserBookmarkTalk
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser

class OrganizationFragment : BaseFragment() {

    var monTalk: MutableList<TalkModel> = mutableListOf()
    var tueTalk: MutableList<TalkModel> = mutableListOf()
    var wedTalk: MutableList<TalkModel> = mutableListOf()
    var thuTalk: MutableList<TalkModel> = mutableListOf()
    var friTalk: MutableList<TalkModel> = mutableListOf()
    var satTalk: MutableList<TalkModel> = mutableListOf()
    var sunTalk: MutableList<TalkModel> = mutableListOf()

    lateinit var rvMon: RecyclerView
    lateinit var rvTue: RecyclerView
    lateinit var rvWed: RecyclerView
    lateinit var rvThu: RecyclerView
    lateinit var rvFri: RecyclerView
    lateinit var rvSat: RecyclerView
    lateinit var rvSun: RecyclerView

    lateinit var monAdapter: TalkTodayAdapter
    lateinit var tueAdapter: TalkTodayAdapter
    lateinit var wedAdapter: TalkTodayAdapter
    lateinit var thuAdapter: TalkTodayAdapter
    lateinit var friAdapter: TalkTodayAdapter
    lateinit var satAdapter: TalkTodayAdapter
    lateinit var sunAdapter: TalkTodayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_organization, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            startLoadingUI()
            monAdapter = TalkTodayAdapter(activity, monTalk)
            rvMon = view.findViewById(R.id.rv_mon)
            rvMon.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvMon.adapter = monAdapter

            rvTue = view.findViewById(R.id.rv_tue)
            tueAdapter = TalkTodayAdapter(activity, tueTalk)
            rvTue.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvTue.adapter = tueAdapter

            rvWed = view.findViewById(R.id.rv_wed)
            wedAdapter = TalkTodayAdapter(activity, wedTalk)
            rvWed.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvWed.adapter = wedAdapter

            rvThu = view.findViewById(R.id.rv_thu)
            thuAdapter = TalkTodayAdapter(activity, thuTalk)
            rvThu.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvThu.adapter = thuAdapter

            rvFri = view.findViewById(R.id.rv_fri)
            friAdapter = TalkTodayAdapter(activity, friTalk)
            rvFri.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvFri.adapter = friAdapter

            rvSat = view.findViewById(R.id.rv_sat)
            satAdapter = TalkTodayAdapter(activity, satTalk)
            rvSat.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvSat.adapter = satAdapter

            rvSun = view.findViewById(R.id.rv_sun)
            sunAdapter = TalkTodayAdapter(activity, sunTalk)
            rvSun.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvSun.adapter = sunAdapter
        }
    }

    override fun fetchModel() {
        super.fetchModel()


        SearchLoader.shared.searchTalk("") { talk ->

            monTalk = talk.filter { it.live == 2 && it.open_day.contains("월") }.toMutableList()
            monAdapter.items = monTalk
            monAdapter.notifyDataSetChanged()

            tueTalk = talk.filter { it.live == 2 && it.open_day.contains("화") }.toMutableList()
            tueAdapter.items = tueTalk
            tueAdapter.notifyDataSetChanged()

            wedTalk = talk.filter { it.live == 2 && it.open_day.contains("수") }.toMutableList()
            wedAdapter.items = wedTalk
            wedAdapter.notifyDataSetChanged()

            thuTalk = talk.filter { it.live == 2 && it.open_day.contains("목") }.toMutableList()
            thuAdapter.items = thuTalk
            thuAdapter.notifyDataSetChanged()

            friTalk = talk.filter { it.live == 2 && it.open_day.contains("금") }.toMutableList()
            friAdapter.items = friTalk
            friAdapter.notifyDataSetChanged()

            satTalk = talk.filter { it.live == 2 && it.open_day.contains("토") }.toMutableList()
            satAdapter.items = satTalk
            satAdapter.notifyDataSetChanged()

            sunTalk = talk.filter { it.live == 2 && it.open_day.contains("일") }.toMutableList()
            sunAdapter.items = sunTalk
            sunAdapter.notifyDataSetChanged()

            stopLoadingUI()
        }
    }
}