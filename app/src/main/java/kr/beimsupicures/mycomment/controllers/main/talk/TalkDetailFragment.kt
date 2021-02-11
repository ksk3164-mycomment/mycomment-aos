package kr.beimsupicures.mycomment.controllers.main.talk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_talk_detail.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.PickLoader
import kr.beimsupicures.mycomment.api.models.PickModel
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.api.models.pick
import kr.beimsupicures.mycomment.common.isPushEnabledAtOSLevel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.WaterDropDialog
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*

class TalkDetailFragment : BaseFragment() {

    var talk: TalkModel? = null
    lateinit var ivContentImage: ImageView
    lateinit var titleLabel: TextView
    lateinit var contentLabel: TextView
    lateinit var bookmarkView: ImageView

    private lateinit var viewPager: ViewPager2
    lateinit var tabLayouts: TabLayout
    private val tabTextList = arrayListOf("실시간톡", "드라마피드")


    lateinit var realTimeTalkFragment: RealTimeTalkFragment
    lateinit var dramaFeedFragment: DramaFeedFragment
    lateinit var timeLineTalkFragment: TimeLineTalkFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_talk_detail, container, false)

    }

    override fun onResume() {
        super.onResume()
        fetchModel()
//        setUpViewPager()
        hideKeyboard()

    }


    override fun loadModel() {
        super.loadModel()
        talk = TalkDetailFragmentArgs.fromBundle(requireArguments()).talk
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            ivContentImage = view.findViewById(R.id.ivContentImage)
            titleLabel = view.findViewById(R.id.titleLabel)
            contentLabel = view.findViewById(R.id.contentLabel)
            bookmarkView = view.findViewById(R.id.bookmarkView)

            tabLayouts = view.findViewById(R.id.tabLayout)
            viewPager = view.findViewById(R.id.viewPager2)

//            tabLayouts.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                }
//
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//
//                    Log.e("tjdrnr", "" + tab?.position)
//                    when (tab?.position) {
//                        0 ->
//                            viewPager.currentItem = 0
//                        1 ->
//                            viewPager.currentItem = 1
//                        2 ->
//                            viewPager.currentItem = 2
//                    }
//                }
//            })

            talk?.let { values ->


                realTimeTalkFragment = RealTimeTalkFragment(talk!!)
                dramaFeedFragment = DramaFeedFragment(talk!!)
                timeLineTalkFragment = TimeLineTalkFragment()

                RealTimeTalkFragment.newInstance(values)

                viewPager.adapter = CustomFragmentStateAdapter(talk!!,this )
                TabLayoutMediator(tabLayouts, viewPager) { tab, position ->
                    tab.text = tabTextList[position]
                }.attach()

                viewPager.isUserInputEnabled = false

                Glide.with(this).load(values.content_image_url)
                    .into(ivContentImage)
                ivContentImage.setOnClickListener { view ->
                    val action =
                        NavigationDirections.actionGlobalWebViewFragment(values, null)
                    view.findNavController().navigate(action)
                }
                titleLabel.text = values.title
                contentLabel.text = values.content

                bookmarkView.setImageDrawable(
                    ContextCompat.getDrawable(
                        view.context,
                        if (values.pick == true) R.drawable.bookmark_r else R.drawable.bookmark
                    )
                )
                bookmarkView.setOnClickListener {

                    BaseApplication.shared.getSharedPreferences().getUser()?.let {

                        when (values.pick) {
                            true -> {
                                PickLoader.shared.unpick(
                                    PickModel.Category.talk,
                                    values.id
                                ) { pickModel ->
                                    var newValue = talk
                                    if (newValue != null) {
                                        newValue.pick = pickModel.pick()
                                    }
                                    talk = newValue
                                }
                                bookmarkView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        view.context,
                                        R.drawable.bookmark
                                    )
                                )
                            }

                            false -> {
                                activity?.supportFragmentManager?.let { fragmentManager ->
                                    WaterDropDialog.newInstance(
                                        if (isPushEnabledAtOSLevel(view.context)) {
                                            WaterDropDialog.NotificationSetting.allowed
                                        } else {
                                            WaterDropDialog.NotificationSetting.denied
                                        },
                                        values.title
                                    ).show(fragmentManager, "")
                                }
                                PickLoader.shared.pick(
                                    PickModel.Category.talk,
                                    category_owner_id = null,
                                    category_id = values.id
                                ) { pickModel ->
                                    var newValue = talk
                                    newValue?.pick = pickModel.pick()
                                    talk = newValue
                                }
                                bookmarkView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        view.context,
                                        R.drawable.bookmark_r
                                    )
                                )
                            }

                            else -> {
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
    }

    override fun fetchModel() {
        super.fetchModel()

        talk?.let { talk ->
            BaseApplication.shared.getSharedPreferences().setTalkTime()
            BaseApplication.shared.getSharedPreferences().setCurrentTalkId(talk.id)
        }

    }

    class CustomFragmentStateAdapter(var viewModel: TalkModel, fragmentActivity: TalkDetailFragment) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RealTimeTalkFragment(viewModel)
                else -> DramaFeedFragment(viewModel)
            }
        }
    }

//    private fun setUpViewPager() {
//
//        viewPager = viewPager
//        tabLayouts = tabLayout
//
//        val adapter = DetailPagerAdapter(requireFragmentManager(), talk!!)
//        adapter.addFragment(RealTimeTalkFragment(talk!!), "실시간톡")
//        adapter.addFragment(DramaFeedFragment(), "드라마피드")
//        adapter.addFragment(TimeLineTalkFragment(), "타임라인톡")
//
//        viewPagers.adapter = adapter
//        tabLayouts.setupWithViewPager(viewPager)
//
//    }

}

