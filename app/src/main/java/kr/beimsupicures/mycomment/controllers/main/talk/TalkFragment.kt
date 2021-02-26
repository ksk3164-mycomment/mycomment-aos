package kr.beimsupicures.mycomment.controllers.main.talk

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.Target
import com.github.islamkhsh.CardSliderIndicator
import com.github.islamkhsh.CardSliderViewPager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_talk.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.*
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.components.adapters.*
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*
import kr.beimsupicures.mycomment.services.MCFirebaseMessagingService
import java.lang.reflect.Array.set
import java.text.SimpleDateFormat
import java.util.*

class TalkFragment : BaseFragment() {

//    enum class Sort {
//        weekday, provider
//    }

//    var filter: Pair<Int, Sort> = Pair(1, Sort.weekday)
//        set(newValue) {
//            val oldValue = filter
//            field = newValue

//            Log.e("TAG", "${filter}")

//            context?.let { context ->
//                when (filter.second) {
//                    Sort.weekday -> {
//                        btnWeekday.setTextColor(
//                            ContextCompat.getColor(
//                                context,
//                                R.color.colorTextTitle
//                            )
//                        )
//                        btnWeekday.typeface =
//                            ResourcesCompat.getFont(context, R.font.gyeonggi_batang_b)
//                        btnProvider.setTextColor(
//                            ContextCompat.getColor(
//                                context,
//                                R.color.colorTextSegment
//                            )
//                        )
//                        btnProvider.typeface =
//                            ResourcesCompat.getFont(context, R.font.gyeonggi_batang_r)
//                    }
//                    Sort.provider -> {
//                        btnWeekday.setTextColor(
//                            ContextCompat.getColor(
//                                context,
//                                R.color.colorTextSegment
//                            )
//                        )
//                        btnWeekday.typeface =
//                            ResourcesCompat.getFont(context, R.font.gyeonggi_batang_r)
//                        btnProvider.setTextColor(
//                            ContextCompat.getColor(
//                                context,
//                                R.color.colorTextTitle
//                            )
//                        )
//                        btnProvider.typeface =
//                            ResourcesCompat.getFont(context, R.font.gyeonggi_batang_b)
//                    }
//                }
//            }

//            if (filter.second != oldValue.second) {
//                providerAdapter.items = tabs
//                providerAdapter.notifyDataSetChanged()
//            }
//
//            if (filter.first != oldValue.first) {
//                list = talk.filter { it.category_id == filter.first }.toMutableList()
//            }
//        }

    //    var notice: MutableList<NoticeModel> = mutableListOf()
    var ad: MutableList<AdModel> = mutableListOf()

    var ymd_date: String = ""

//    var category: MutableList<Pair<Boolean, CategoryModel>> = mutableListOf()
//    var provider: MutableList<Pair<Boolean, ProviderModel>> = mutableListOf()
//        set(newValue) {
//            field = newValue
//
//            when (filter.second) {
//                Sort.provider -> {
//                    showProviderUI()
//                }
//            }
//        }
//    var weekday: MutableList<Pair<Boolean, ProviderModel>> = mutableListOf()
//        set(newValue) {
//            field = newValue
//            when (filter.second) {
//                Sort.weekday -> {
//                    showWeekdayUI()
//                }
//            }
//        }
    var talk: MutableList<TalkModel> = mutableListOf()
    var pickTop: MutableList<PickTopModel> = mutableListOf()
//        set(newValue) {
//            field = newValue
//            list = talk.filter { it.category_id == filter.first }.toMutableList()
//        }
//    var tabs: MutableList<Pair<Boolean, ProviderModel>> = mutableListOf()
//        get() {
//            when (filter.second) {
//                Sort.provider -> {
//                    return provider
//                }
//                Sort.weekday -> {
//                    return weekday
//                }
//            }
//        }
//    var list: MutableList<TalkModel> = mutableListOf()
//        set(newValue) {
//            field = newValue
//            talkAdapter.items = list
//            talkAdapter.notifyDataSetChanged()
//        }


    //    lateinit var noticeView: CardSliderViewPager
//    lateinit var noticeAdapter: NoticeAdapter
    lateinit var bannerView: CardSliderViewPager
    lateinit var bannerIndicator: CardSliderIndicator
    lateinit var bannerAdapter: BannerAdapter
    lateinit var tvBookMark: TextView
    lateinit var bookMarkView: RecyclerView
    lateinit var bookMarkAdapter: TalkBookmarkAdapter
    lateinit var rvDrama: RecyclerView
    lateinit var dramaAdapter: TalkTodayAdapter

    lateinit var tvFirstSympathyName: TextView
    lateinit var tvSecondSympathyName: TextView
    lateinit var ivFirstProfile: ImageView
    lateinit var ivSecondProfile: ImageView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        when (activity?.intent?.getStringExtra("Redirection") ?: null) {
            MCFirebaseMessagingService.Redirection.TalkDetail.value -> {
                activity?.intent?.getStringExtra("Payload")?.let { payload ->
                    val talkModel = makeTalkModel(payload)
                    if (talkModel != null) {
                        val action = NavigationDirections.actionGlobalTalkDetailFragment(talkModel)
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(action)
                    }
                }
            }
            MCFirebaseMessagingService.Redirection.TalkComment.value -> {
                activity?.intent?.getStringExtra("Payload")?.let { payload ->
                    val mentionModel = makeMentionModel(payload)
                    if (mentionModel != null) {
                        AnalyticsLoader.shared.reportMentionConfirm(mentionModel.mention.id)
                        val action =
                            NavigationDirections.actionGlobalTalkDetailFragment(mentionModel.talk)
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(action)
                    }
                }
            }
            else -> {
            }
        }
        BaseApplication.shared.getSharedPreferences().setCurrentTalkId(-1)
        activity?.intent?.removeExtra("Redirection")
        activity?.intent?.removeExtra("Payload")
        return inflater.inflate(R.layout.fragment_talk, container, false)
    }

    override fun onResume() {
        super.onResume()

        val day = Calendar.getInstance()
        day.add(Calendar.DATE, -1)
        val beforeDate: String = SimpleDateFormat("yyMMdd").format(day.time)

        ymd_date = beforeDate

        fetchModel()
//        filter = Pair(1, Sort.weekday)
    }

    override fun loadModel() {
        super.loadModel()

        var array = mutableListOf(
            ProviderModel(name = "월요일"),
            ProviderModel(name = "화요일"),
            ProviderModel(name = "수요일"),
            ProviderModel(name = "목요일"),
            ProviderModel(name = "금요일"),
            ProviderModel(name = "토요일"),
            ProviderModel(name = "일요일")
        )

        val day = Calendar.getInstance()[Calendar.DAY_OF_WEEK]

//        weekday = array.mapIndexed { index, value -> Pair((index == (day + 5) % 7), value) }
//            .toMutableList()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

//            noticeView = view.findViewById(R.id.noticeView)
//            noticeAdapter = NoticeAdapter(notice)
//            noticeView.adapter = noticeAdapter
            bannerView = view.findViewById(R.id.bannerView)
            bannerIndicator = view.findViewById(R.id.bannerIndicator)
            var selectedIndicator: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.select_dot)
            bannerIndicator.selectedIndicator = selectedIndicator
//            weekdayView = view.findViewById(R.id.weekdayView)

            bannerAdapter = BannerAdapter(activity, ad)
            bannerView.adapter = bannerAdapter
            bannerView.layoutParams.height =
                (((resources.configuration.screenWidthDp - 16 - 16).toFloat() / 343F) * 110F).toInt().dp


//            categoryView = view.findViewById(R.id.categoryView)
//            categoryView.addItemDecoration(object : RecyclerView.ItemDecoration() {
//                override fun getItemOffsets(
//                    outRect: Rect,
//                    view: View,
//                    parent: RecyclerView,
//                    state: RecyclerView.State
//                ) {
//                    super.getItemOffsets(outRect, view, parent, state)
//                    when (parent.getChildAdapterPosition(view)) {
//                        0 -> {
//                        }
//                        else -> outRect.left = 8.dp
//                    }
//                }
//            })
//            categoryView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
//                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//
//                }
//
//                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                    when (e.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            rv.parent.requestDisallowInterceptTouchEvent(true)
//                        }
//                    }
//                    return false
//                }
//
//                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//
//                }
//            })
//            categoryAdapter = CategoryAdapter(this.category, didSelectAt = { selected ->
//                filter = Pair(selected.second.id, filter.second)
//            })
//            categoryView.layoutManager =
//                LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
//            categoryView.adapter = categoryAdapter
//
//
//
//            btnWeekday = view.findViewById(R.id.btnWeekday)
//            btnWeekday.setOnClickListener {
//                filter = Pair(
//                    filter.first,
//                    Sort.weekday
//                )
//                showWeekdayUI()
//            }
//            btnProvider = view.findViewById(R.id.btnProvider)
//            btnProvider.setOnClickListener {
//                filter = Pair(
//                    filter.first,
//                    Sort.provider
//                )
//                showProviderUI()
//            }


//            providerView = view.findViewById(R.id.providerView)
//            providerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
//                override fun getItemOffsets(
//                    outRect: Rect,
//                    view: View,
//                    parent: RecyclerView,
//                    state: RecyclerView.State
//                ) {
//                    super.getItemOffsets(outRect, view, parent, state)
//                    when (parent.getChildAdapterPosition(view)) {
//                        0 -> {
//                        }
//                        else -> outRect.left = 8.dp
//                    }
//                }
//            })
//            providerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
//                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//
//                }
//
//                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                    when (e.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            rv.parent.requestDisallowInterceptTouchEvent(true)
//                        }
//                    }
//                    return false
//                }
//
//                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//
//                }
//            })
//            providerAdapter = ProviderAdapter(this.provider, didSelectAt = { position, selected ->
//
//                when (filter.second) {
//                    Sort.weekday -> {
//                        val newValue = weekday.mapIndexed { index, value ->
//                            Pair(
//                                index == position,
//                                value.second
//                            )
//                        }.toMutableList()
//                        weekday = newValue
//                    }
//                    Sort.provider -> {
//                        val newValue = provider.mapIndexed { index, value ->
//                            Pair(
//                                index == position,
//                                value.second
//                            )
//                        }.toMutableList()
//                        provider = newValue
//                    }
//                }
//            })
//            providerView.layoutManager =
//                LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
//            providerView.adapter = providerAdapter



//            talkAdapter = TalkAdapter(activity, this.talk)
//            talkView = view.findViewById(R.id.talkView)
//            talkView.layoutManager = LinearLayoutManager(context)
//            talkView.adapter = talkAdapter

//            weekdayView.visibility = View.GONE
//            providerView.visibility = View.GONE
//            categoryView.visibility = View.GONE

            tvFirstSympathyName = view.findViewById(R.id.tv_first_sympathy_name)
            tvSecondSympathyName = view.findViewById(R.id.tv_second_sympathy_name)
            ivFirstProfile = view.findViewById(R.id.iv_first_prifile)
            ivSecondProfile = view.findViewById(R.id.iv_second_profile)

            BaseApplication.shared.getSharedPreferences().getUser()?.let {

                var accessToken = BaseApplication.shared.getSharedPreferences().getAccessToken()
                Log.e("성국", "Bearer $accessToken")

                bookmarkLayout.visibility = View.VISIBLE

                bookMarkAdapter = TalkBookmarkAdapter(activity, this.talk)
                bookMarkView = view.findViewById(R.id.rvBookMark)
                bookMarkView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                bookMarkView.adapter = bookMarkAdapter


            } ?: run {

                bookmarkLayout.visibility = View.GONE
            }

            tvBookMark = view.findViewById(R.id.tvBookMark)
            tvBookMark.setOnClickListener {
                val action = R.id.action_talkFragment_to_bookMarkFragment
                findNavController().navigate(action)
            }

            rvDrama = view.findViewById(R.id.rvDrama)
            dramaAdapter = TalkTodayAdapter(activity, this.talk)

            rvDrama.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvDrama.adapter = dramaAdapter

        }
    }

    override fun fetchModel() {
        super.fetchModel()

//        NoticeLoader.shared.getNoticeList(true) { notice ->
//            this.notice = notice.toMutableList()
//            noticeAdapter.items = this.notice
//            noticeAdapter.notifyDataSetChanged()
//            noticeView.visibility = if (notice.size > 0) View.VISIBLE else View.GONE
//        }

        PickTopLoader.shared.getPickTop(ymd_date) { values ->
            pickTop = values.toMutableList()

            if (pickTop.size > 0) {
                Glide.with(this).load(pickTop[0].profile_image_url)
                    .transform(CenterCrop(), CircleCrop())
                    .override(Target.SIZE_ORIGINAL)
                    .fallback(R.drawable.bg_profile_thumbnail)
                    .into(ivFirstProfile)

                Glide.with(this).load(pickTop[1].profile_image_url)
                    .transform(CenterCrop(), CircleCrop())
                    .override(Target.SIZE_ORIGINAL)
                    .fallback(R.drawable.bg_profile_thumbnail)
                    .into(ivSecondProfile)

                tvFirstSympathyName.text = pickTop[0].nickname
                tvSecondSympathyName.text = pickTop[1].nickname
            } else {
                PickTopLoader.shared.getPickTop("210201") { values ->
                    this.pickTop = values
                    pickTop[0].profile_image_url?.let { profile_image_url ->
                        Glide.with(this).load(profile_image_url)
                            .transform(CenterCrop(), CircleCrop())
                            .override(Target.SIZE_ORIGINAL)
                            .into(ivFirstProfile)

                        Glide.with(this).load(profile_image_url)
                            .transform(CenterCrop(), CircleCrop())
                            .override(Target.SIZE_ORIGINAL)
                            .into(ivSecondProfile)

                        tvFirstSympathyName.text = pickTop[0].nickname
                        tvSecondSympathyName.text = pickTop[1].nickname
                    }
                }

            }
        }

        val instance = Calendar.getInstance()
        val date = instance.get(Calendar.DAY_OF_WEEK).toString()
        var weekDay = ""
        when(date){
            "1"-> weekDay = "일"
            "2"-> weekDay = "월"
            "3"-> weekDay = "화"
            "4"-> weekDay = "수"
            "5"-> weekDay = "목"
            "6"-> weekDay = "금"
            "7"-> weekDay = "토"
        }
        TalkLoader.shared.getTalkList(weekDay) {

            this.talk = it.toMutableList()

            var talkfilter = talk.filter { model-> model.live == 2  }.sortedBy { data -> data.talk_count }.reversed()
            Log.e("성국",""+talkfilter)

            dramaAdapter.items = talkfilter.toMutableList()
            dramaAdapter.notifyDataSetChanged()

        }

        AdLoader.shared.getAdList(true, AdModel.Location.talk) { ad ->
            this.ad = ad
            bannerAdapter.items = this.ad
            bannerAdapter.notifyDataSetChanged()
        }

//        TalkLoader.shared.getCategoryList { values ->
//            this.category =
//                values.mapIndexed { index, providerModel -> Pair((index == 0), providerModel) }
//                    .toMutableList()
//            categoryAdapter.items = this.category
//            categoryAdapter.notifyDataSetChanged()
//        }

//        ProviderLoader.shared.getProviderList(true) { values ->
//            this.provider =
//                values.mapIndexed { index, providerModel -> Pair((index == 0), providerModel) }
//                    .toMutableList()
//            providerAdapter.items = this.tabs
//            providerAdapter.notifyDataSetChanged()
//        }

        getUserBookmarkTalk()
    }

    fun sort(talk: MutableList<TalkModel>): MutableList<TalkModel> {
        val onair = talk.filter { it.onAir }.sortedBy { data -> data.talk_count }.reversed()
        val standby = talk.filter { !it.onAir }.sortedBy { data -> data.talk_count }.reversed()

        var sorted = onair.toMutableList()
        sorted.addAll(standby)

        return sorted
    }
}

//fun TalkFragment.showWeekdayUI() {
//    weekday.filter { it.first }.firstOrNull()?.let { selected ->
//        TalkModel.Weekday.values().filter { it.value == selected.second.name.replace("요일", "") }
//            .firstOrNull()?.let { weekday ->
//                TalkLoader.shared.getTalkList(weekday) { talk ->
//                    this.talk = sort(talk)
//                }
//            }
//    }
//}

//fun TalkFragment.showProviderUI() {
//    provider.filter { it.first }.firstOrNull()?.let { selected ->
//        TalkLoader.shared.getTalkList(selected.second.id) { talk ->
//            this.talk = sort(talk)
//        }
//    }
//}

fun TalkFragment.makeTalkModel(payload: String): TalkModel? {
    val gson = GsonBuilder().create()
    return gson.fromJson(payload, TalkModel::class.java)
}

fun TalkFragment.makeMentionModel(payload: String): MentionModel? {
    val gson = GsonBuilder().create()
    return gson.fromJson(payload, MentionModel::class.java)
}

fun TalkFragment.getUserBookmarkTalk() {
    UserLoader.shared.getUserBookmarkTalk { talk ->
        this.talk = talk.toMutableList()
        bookMarkAdapter.items = this.talk
        bookMarkAdapter.notifyDataSetChanged()
    }
}