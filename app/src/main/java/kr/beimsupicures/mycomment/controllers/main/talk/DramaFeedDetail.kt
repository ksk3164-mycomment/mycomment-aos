package kr.beimsupicures.mycomment.controllers.main.talk

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import jp.wasabeef.richeditor.RichEditor
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.api.models.EventObserver
import kr.beimsupicures.mycomment.api.models.FeedDetailModel
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.api.models.MyViewModel
import kr.beimsupicures.mycomment.common.keyboard.showKeyboard
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*


class DramaFeedDetailFragment : BaseFragment() {

    lateinit var title: TextView

    lateinit var nicknameLabel: TextView

    lateinit var createAt: TextView
    lateinit var view_cnt: TextView
    lateinit var profileView: ImageView
    lateinit var constraintLayout: ConstraintLayout
    lateinit var feedCommentFragment: FeedCommentFragment

    lateinit var messageField: EditText
    lateinit var btnSend: ImageView

    lateinit var viewModel: MyViewModel

    lateinit var editor: RichEditor

    var userId = 0

    var validation: Boolean = false
        get() = when {
            messageField.text.isEmpty() -> false
            else -> true
        }

    var feed: FeedModel? = null
    var feedDetail: FeedDetailModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MyViewModel::class.java) }!!
        viewModel.getReply2.observe(viewLifecycleOwner, EventObserver { t ->
            messageField.setText(t)
            showKeyboard(requireActivity(), messageField)
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drama_feed_detail, container, false)
    }

    override fun onPause() {
        super.onPause()
        messageField.setText("")
    }

    override fun onResume() {
        super.onResume()
        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            title = view.findViewById(R.id.title)
            nicknameLabel = view.findViewById(R.id.nicknameLabel)
            createAt = view.findViewById(R.id.createAt)
            view_cnt = view.findViewById(R.id.view_cnt)

            profileView = view.findViewById(R.id.profileView)
            editor = view.findViewById(R.id.editor)
            constraintLayout = view.findViewById(R.id.constraintLayout)
            messageField = view.findViewById(R.id.messageField)
            btnSend = view.findViewById(R.id.btnSend)

            feed?.let { values ->

                nicknameLabel.text = values.nickname

                if (values.profile_image_url.isNullOrEmpty()) {
                    profileView.setImageDrawable(
                        context?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.bg_profile_original
                            )
                        })
                } else {
                    Glide.with(this)
                        .load(values.profile_image_url)
                        .transform(CircleCrop(), CenterCrop())
                        .into(profileView)
                }

                createAt.text = values.c_ts.timeline()

                view_cnt.text = "조회 ${values.view_cnt}"

                editor.setInputEnabled(false)
                constraintLayout.setOnClickListener {
                    val action =
                        NavigationDirections.actionGlobalProfileFragment(userId)
                    it.findNavController().navigate(action)
                }

                feedCommentFragment = FeedCommentFragment(values)

                activity?.let {
                    it.supportFragmentManager.beginTransaction()
                        .replace(R.id.feedCommentFragment, feedCommentFragment).commit()
                }


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


                btnSend.setOnClickListener {

                    BaseApplication.shared.getSharedPreferences().getUser()?.let {

                        when (validation) {
                            true -> {

                                viewModel.setMessage2(messageField.text.toString())
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
            }
        }
    }

    override fun loadModel() {
        super.loadModel()
        feed = BaseApplication.shared.getSharedPreferences().getFeed()
    }

    override fun fetchModel() {
        super.fetchModel()
        val feedSeq = BaseApplication.shared.getSharedPreferences().getFeedId()
        FeedLoader.shared.getFeedDetail(feedSeq) { values ->
            feedDetail = values

            feedDetail?.let {
                userId = it.user_id

                BaseApplication.shared.getSharedPreferences()
                    .setFeedDetailUserId(it.user_id)
                title.text = it.title

                val displayMetrics = this.resources.displayMetrics
                val dpWidth =
                    displayMetrics!!.widthPixels / displayMetrics.density

                editor.html = it.content?.replace(
                    """" alt=""""",
                    """" alt="" width="${dpWidth.toInt() - 32}""""
                )

            }
        }
    }
}