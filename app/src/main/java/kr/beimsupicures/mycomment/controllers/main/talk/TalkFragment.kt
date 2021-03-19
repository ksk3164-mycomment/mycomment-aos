package kr.beimsupicures.mycomment.controllers.main.talk

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
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
import kr.beimsupicures.mycomment.controllers.signs.SignInFragmentDirections
import kr.beimsupicures.mycomment.extensions.*
import kr.beimsupicures.mycomment.services.MCFirebaseMessagingService
import kr.beimsupicures.mycomment.viewmodels.signs.SignStep1ViewModel
import java.text.SimpleDateFormat
import java.util.*

class TalkFragment : BaseFragment() {

    var ad: MutableList<AdModel> = mutableListOf()

    var ymd_date: String = ""
    var talk: MutableList<TalkModel> = mutableListOf()
    var bookmark: MutableList<TalkModel> = mutableListOf()
    var pickTop: MutableList<PickTopModel> = mutableListOf()

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
    lateinit var tvOrganization: TextView

    lateinit var how: TextView

    lateinit var firstProfileWrapper: ConstraintLayout
    lateinit var secondProfileWrapper: ConstraintLayout
    lateinit var layoutSecondSympathy: ConstraintLayout

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
        fetchModel()
    }

    override fun loadModel() {
        super.loadModel()
        val day = Calendar.getInstance()
        day.add(Calendar.DATE, -1)
        var beforeDate: String = SimpleDateFormat("yyMMdd").format(day.time)

        ymd_date = beforeDate

        PickTopLoader.shared.getPickTop(ymd_date) { values ->

            pickTop = values.toMutableList()
            if (pickTop.size > 0) {

                Glide.with(this).load(pickTop[0].profile_image_url)
                    .transform(CenterCrop(), CircleCrop())
                    .override(Target.SIZE_ORIGINAL)
                    .fallback(R.drawable.bg_profile_original)
                    .into(ivFirstProfile)
                tvFirstSympathyName.text = pickTop[0].nickname

                if (pickTop.size >= 2) {
                    layoutSecondSympathy.visibility = View.VISIBLE
                    Glide.with(this).load(pickTop[1].profile_image_url)
                        .transform(CenterCrop(), CircleCrop())
                        .override(Target.SIZE_ORIGINAL)
                        .fallback(R.drawable.bg_profile_original)
                        .into(ivSecondProfile)
                    tvSecondSympathyName.text = pickTop[1].nickname
                }

            } else {
                day.add(Calendar.DATE, -1)
                beforeDate = SimpleDateFormat("yyMMdd").format(day.time)

                ymd_date = beforeDate
                PickTopLoader.shared.getPickTop(ymd_date) { values ->
                    pickTop = values.toMutableList()
                    Glide.with(this).load(pickTop[0].profile_image_url)
                        .transform(CenterCrop(), CircleCrop())
                        .override(Target.SIZE_ORIGINAL)
                        .fallback(R.drawable.bg_profile_original)
                        .into(ivFirstProfile)
                    tvFirstSympathyName.text = pickTop[0].nickname

                    if (pickTop.size >= 2) {
                        layoutSecondSympathy.visibility = View.VISIBLE
                        Glide.with(this).load(pickTop[1].profile_image_url)
                            .transform(CenterCrop(), CircleCrop())
                            .override(Target.SIZE_ORIGINAL)
                            .fallback(R.drawable.bg_profile_original)
                            .into(ivSecondProfile)
                        tvSecondSympathyName.text = pickTop[1].nickname
                    }
                }
            }
        }
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            layoutSecondSympathy = view.findViewById(R.id.layout_second_sympathy)

            bannerView = view.findViewById(R.id.bannerView)
            bannerIndicator = view.findViewById(R.id.bannerIndicator)
            var selectedIndicator: Drawable? =
                ContextCompat.getDrawable(requireContext(), R.drawable.select_dot)
            bannerIndicator.selectedIndicator = selectedIndicator

            bannerAdapter = BannerAdapter(activity, ad)
            bannerView.adapter = bannerAdapter
            bannerView.layoutParams.height =
                (((resources.configuration.screenWidthDp - 16 - 16).toFloat() / 343F) * 110F).toInt().dp

            tvFirstSympathyName = view.findViewById(R.id.tv_first_sympathy_name)
            tvSecondSympathyName = view.findViewById(R.id.tv_second_sympathy_name)
            ivFirstProfile = view.findViewById(R.id.iv_first_profile)
            ivSecondProfile = view.findViewById(R.id.iv_second_profile)
            firstProfileWrapper = view.findViewById(R.id.first_profile_wrapper)
            secondProfileWrapper = view.findViewById(R.id.second_profile_wrapper)

            firstProfileWrapper.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalProfileFragment(pickTop[0].category_owner_id)
                it.findNavController().navigate(action)
            }

            secondProfileWrapper.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalProfileFragment(pickTop[1].category_owner_id)
                it.findNavController().navigate(action)
            }

            BaseApplication.shared.getSharedPreferences().getUser()?.let {

                var accessToken = BaseApplication.shared.getSharedPreferences().getAccessToken()
                Log.e("성국", "Bearer $accessToken")

                bookmarkLayout.visibility = View.VISIBLE

                bookMarkAdapter = TalkBookmarkAdapter(activity, this.bookmark)
                bookMarkView = view.findViewById(R.id.rvBookMark)
                bookMarkView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                bookMarkView.adapter = bookMarkAdapter

            } ?: run {

                bookmarkLayout.visibility = View.GONE
            }

            tvBookMark = view.findViewById(R.id.tvBookMark)
            tvBookMark.setOnClickListener {
                val action = NavigationDirections.actionGlobalBookmarkFragment()
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(action)
            }

            tvOrganization = view.findViewById(R.id.tvOrganization)
            tvOrganization.setOnClickListener {
                val action = NavigationDirections.actionGlobalOrganizationFragment()
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(action)
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

        TalkLoader.shared.getTalkList {
            this.talk = it.toMutableList()
            var talkfilter =
                this.talk.filter { model -> model.live == 2 }.sortedBy { data -> data.talk_count }
                    .reversed()
            this.talk = talkfilter.toMutableList()

            dramaAdapter.items = this.talk
            dramaAdapter.notifyDataSetChanged()

        }

        AdLoader.shared.getAdList(true, AdModel.Location.talk) { ad ->
            this.ad = ad
            bannerAdapter.items = this.ad
            bannerAdapter.notifyDataSetChanged()
        }

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

fun TalkFragment.makeTalkModel(payload: String): TalkModel? {
    val gson = GsonBuilder().create()
    return gson.fromJson(payload, TalkModel::class.java)
}

fun TalkFragment.makeMentionModel(payload: String): MentionModel? {
    val gson = GsonBuilder().create()
    return gson.fromJson(payload, MentionModel::class.java)
}

fun TalkFragment.getUserBookmarkTalk() {
    UserLoader.shared.getUserBookmarkTalk { bookmark ->
        this.bookmark = bookmark.toMutableList()
        bookMarkAdapter.items = this.bookmark
        bookMarkAdapter.notifyDataSetChanged()
    }
}