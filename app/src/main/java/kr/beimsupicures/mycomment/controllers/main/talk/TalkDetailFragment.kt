package kr.beimsupicures.mycomment.controllers.main.talk

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.PickLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.common.isPushEnabledAtOSLevel
import kr.beimsupicures.mycomment.common.keyboard.showKeyboard
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.WaterDropDialog
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.controllers.main.feed.DramaFeedFragment
import kr.beimsupicures.mycomment.extensions.*


class TalkDetailFragment : BaseFragment() {

    var talk: TalkModel? = null
    lateinit var ivContentImage: ImageView
    lateinit var titleLabel: TextView
    lateinit var contentLabel: TextView
    lateinit var bookmarkView: ImageView
    lateinit var ivNetflix: ImageView
    lateinit var ivTving: ImageView
    lateinit var ivWave: ImageView
    lateinit var ivWatcha: ImageView

    lateinit var messageWrapperView: LinearLayout
    lateinit var floatingButton: FloatingActionButton

    lateinit var btnSend: ImageView
    lateinit var messageField: EditText

    lateinit var viewModel: MyViewModel

    var validation: Boolean = false
        get() = when {
            messageField.text.isEmpty() -> false
            else -> true
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MyViewModel::class.java) }!!
        viewModel.getReply.observe(viewLifecycleOwner, EventObserver { t ->
            messageField.setText(t)
            showKeyboard(requireActivity(), messageField)
        })

    }

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

            ivNetflix = view.findViewById(R.id.ivNetflix)
            ivTving = view.findViewById(R.id.ivTving)
            ivWave = view.findViewById(R.id.ivWave)
            ivWatcha = view.findViewById(R.id.ivWatcha)

            titleLabel = view.findViewById(R.id.titleLabel)
            contentLabel = view.findViewById(R.id.contentLabel)
            bookmarkView = view.findViewById(R.id.bookmarkView)

            messageWrapperView = view.findViewById(R.id.messageWrapperView)
            floatingButton = view.findViewById(R.id.floating_button)

            tabLayouts = view.findViewById(R.id.tabLayout)
            viewPager = view.findViewById(R.id.viewPager2)
            talk?.let { values ->

                realTimeTalkFragment = RealTimeTalkFragment(talk!!)
                dramaFeedFragment = DramaFeedFragment(talk!!)
                timeLineTalkFragment = TimeLineTalkFragment()

                RealTimeTalkFragment.newInstance(values)

                viewPager.adapter = CustomFragmentStateAdapter(talk!!, this)
                TabLayoutMediator(tabLayouts, viewPager) { tab, position ->
                    tab.text = tabTextList[position]
                }.attach()
                viewPager.isUserInputEnabled = false

                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (position == 0) {
                            messageWrapperView.visibility = View.VISIBLE
                            floatingButton.visibility = View.GONE
                        } else {
                            messageField.setText("")
                            messageWrapperView.visibility = View.GONE
                            floatingButton.visibility = View.VISIBLE
                        }
                    }

                })

                floatingButton.setOnClickListener {
                    BaseApplication.shared.getSharedPreferences().getUser()?.let {
                        val action = NavigationDirections.actionGlobalDramaFeedWriteFragment()
                        view.findNavController().navigate(action)
                    } ?: run {
                        activity?.let { activity ->
                            activity.popup("로그인하시겠습니까?", "로그인") {
                                Navigation.findNavController(activity, R.id.nav_host_fragment)
                                    .navigate(R.id.action_global_signInFragment)
                            }
                        }
                    }
                }

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

                                viewModel.setMessage(messageField.text.toString())
                                messageField.setText("")
                                hideKeyboard()

                            }
                            false -> {
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

                Glide.with(this)
                    .load(values.content_image_url)
                    .override(Target.SIZE_ORIGINAL)
                    .into(ivContentImage)
                ivContentImage.setOnClickListener { view ->
                    if (!values.banner_url.isNullOrBlank()){
                        val action =
                            NavigationDirections.actionGlobalWebViewFragment(values, null)
                        view.findNavController().navigate(action)
                    }
                }
                titleLabel.text = values.title
                contentLabel.text = values.content

                ivNetflix.isVisible = values.otts.contains("netflix")
                ivTving.isVisible = values.otts.contains("tving")
                ivWave.isVisible = values.otts.contains("wavve")
                ivWatcha.isVisible = values.otts.contains("watcha")

                bookmarkView.setImageDrawable(
                    ContextCompat.getDrawable(
                        view.context,
                        if (values.pick == true) R.drawable.bookmark_full_green else R.drawable.bookmark_empty_black
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
                                        R.drawable.bookmark_empty_black
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
                                        R.drawable.bookmark_full_green
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
            BaseApplication.shared.getSharedPreferences().setPostTalkId(talk.id)
            BaseApplication.shared.getSharedPreferences().setTalk(talk)
        }

    }

    class CustomFragmentStateAdapter(
        var viewModel: TalkModel,
        fragmentActivity: TalkDetailFragment
    ) :
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


}