package kr.beimsupicures.mycomment.controllers.main.watch

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.islamkhsh.CardSliderViewPager
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.AdLoader
import kr.beimsupicures.mycomment.api.loaders.NoticeLoader
import kr.beimsupicures.mycomment.api.loaders.WatchLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.components.adapters.*
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.controllers.main.talk.TalkFragment
import kr.beimsupicures.mycomment.controllers.main.talk.showProviderUI
import kr.beimsupicures.mycomment.controllers.signs.CertificationFragment
import kr.beimsupicures.mycomment.extensions.*

class WatchFragment : BaseFragment() {

    var notice: MutableList<NoticeModel> = mutableListOf()
    var ad: MutableList<AdModel> = mutableListOf()
    var category: MutableList<Pair<Boolean, CategoryModel>> = mutableListOf()
    var provider: MutableList<Pair<Boolean, ProviderModel>> = mutableListOf()
        set(newValue) {
            field = newValue
            showProviderUI()
        }
    var watch: MutableList<WatchModel> = mutableListOf()
        set(newValue) {
            field = newValue
            list = watch
        }
    var tabs: MutableList<Pair<Boolean, ProviderModel>> = mutableListOf()
        get() {
            return provider
        }
    var list: MutableList<WatchModel> = mutableListOf()
        set(newValue) {
            field = newValue
            watchAdapter.items = list
            watchAdapter.notifyDataSetChanged()
        }

    var filter: Int = 1
        set(newValue) {
            val oldValue = filter
            field = newValue
        }

    lateinit var noticeView: CardSliderViewPager
    lateinit var noticeAdapter: NoticeAdapter
    lateinit var bannerView: CardSliderViewPager
    lateinit var bannerAdapter: BannerAdapter
    lateinit var categoryView: RecyclerView
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var providerView: RecyclerView
    lateinit var providerAdapter: ProviderAdapter
    lateinit var watchView: RecyclerView
    lateinit var watchAdapter: WatchAdapter
    lateinit var plusView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_watch, container, false)
    }

    override fun onResume() {
        super.onResume()

        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            noticeView = view.findViewById(R.id.noticeView)
            noticeAdapter = NoticeAdapter(notice)
            noticeView.adapter = noticeAdapter
            bannerView = view.findViewById(R.id.bannerView)
            bannerAdapter = BannerAdapter(activity, ad)
            bannerView.adapter = bannerAdapter
            bannerView.layoutParams.height =
                (((resources.configuration.screenWidthDp - 16 - 16).toFloat() / 343F) * 110F).toInt().dp

            categoryView = view.findViewById(R.id.categoryView)
            categoryView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    when (parent.getChildAdapterPosition(view)) {
                        0 -> {
                        }
                        else -> outRect.left = 8.dp
                    }
                }
            })
            categoryView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

                }

                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> {
                            rv.parent.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    return false
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                }
            })
            categoryAdapter = CategoryAdapter(this.category, didSelectAt = { selected ->
                filter = selected.second.id
            })
            categoryView.layoutManager =
                LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
            categoryView.adapter = categoryAdapter

            providerView = view.findViewById(R.id.providerView)
            providerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    when (parent.getChildAdapterPosition(view)) {
                        0 -> {
                        }
                        else -> outRect.left = 8.dp
                    }
                }
            })
            providerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

                }

                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> {
                            rv.parent.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    return false
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                }
            })
            providerAdapter = ProviderAdapter(this.provider, didSelectAt = { position, selected ->
                val newValue = provider.mapIndexed { index, value ->
                    Pair(
                        index == position,
                        value.second
                    )
                }.toMutableList()
                provider = newValue
            })
            providerView.layoutManager =
                LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
            providerView.adapter = providerAdapter

            watchAdapter = WatchAdapter(activity, this.watch)
            watchView = view.findViewById(R.id.watchView)
            watchView.layoutManager = LinearLayoutManager(context)
            watchView.adapter = watchAdapter

            plusView = view.findViewById(R.id.plusView)
            plusView.setOnClickListener {

                BaseApplication.shared.getSharedPreferences().getUser()?.let {
                    WatchLoader.shared.getWatch { watch ->

                        watch?.let {
                            when (watch.status) {
                                WatchModel.Status.onair -> {
                                    activity?.alert("기존에 방이 있습니다. 이동합니다.", "알림", {

                                        val action = NavigationDirections.actionGlobalWatchDetailFragment(watch)
                                        view.findNavController().navigate(action)
                                    })
                                }
                                else -> {
                                    val action = NavigationDirections.actionGlobalCreateWatchFragment(CreateWatchFragment.ViewType.create)
                                    view.findNavController().navigate(action)
                                }
                            }

                        } ?: run {
                            val action = NavigationDirections.actionGlobalCreateWatchFragment(CreateWatchFragment.ViewType.create)
                            view.findNavController().navigate(action)
                        }
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

        NoticeLoader.shared.getNoticeList(true) { notice ->
            this.notice = notice.toMutableList()
            noticeAdapter.items = this.notice
            noticeAdapter.notifyDataSetChanged()
            noticeView.visibility = if (notice.size > 0) View.VISIBLE else View.GONE
        }

        AdLoader.shared.getAdList(true, AdModel.Location.talk) { ad ->
            this.ad = ad
            bannerAdapter.items = this.ad
            bannerAdapter.notifyDataSetChanged()
        }

        WatchLoader.shared.getCategoryList { values ->
            this.category =
                values.mapIndexed { index, providerModel -> Pair((index == 0), providerModel) }
                    .toMutableList()
            categoryAdapter.items = this.category
            categoryAdapter.notifyDataSetChanged()
        }

        WatchLoader.shared.getProviderList { values ->
            this.provider =
                values.mapIndexed { index, providerModel -> Pair((index == 0), providerModel) }
                    .toMutableList()
            providerAdapter.items = this.tabs
            providerAdapter.notifyDataSetChanged()
        }
    }

    fun sort(watch: MutableList<WatchModel>): MutableList<WatchModel> {
        val onair = watch.filter { it.onAir }
        val standby = watch.filter { !it.onAir }.sortedBy { data -> data.watch_count }.reversed()

        var sorted = onair.toMutableList()
        sorted.addAll(standby)

        return sorted
    }
}

fun WatchFragment.showProviderUI() {
    provider.filter { it.first }.firstOrNull()?.let { selected ->
        WatchLoader.shared.getWatchList(selected.second.id) { talk ->
            this.watch = sort(talk)
        }
    }
}