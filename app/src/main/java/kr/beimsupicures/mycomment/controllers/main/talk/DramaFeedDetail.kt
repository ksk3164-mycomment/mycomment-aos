package kr.beimsupicures.mycomment.controllers.main.talk

import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.Target
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.fragment_drama_feed_detail.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.api.models.EventObserver
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.api.models.MyViewModel
import kr.beimsupicures.mycomment.common.keyboard.showKeyboard
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*
import org.sufficientlysecure.htmltextview.HtmlFormatter
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView


class DramaFeedDetailFragment : BaseFragment() {

    lateinit var title: TextView
//    lateinit var content: HtmlTextView
    lateinit var nicknameLabel: TextView
//    lateinit var ivContent: ImageView
//    lateinit var ivContent2: ImageView
//    lateinit var ivContent3: ImageView
//    lateinit var ivContent4: ImageView
//    lateinit var ivContent5: ImageView
//    lateinit var ivContent6: ImageView
//    lateinit var ivContent7: ImageView
//    lateinit var ivContent8: ImageView
    lateinit var createAt: TextView
    lateinit var view_cnt: TextView
    lateinit var profileView: ImageView
    lateinit var feedCommentFragment: FeedCommentFragment

    lateinit var messageField: EditText
    lateinit var btnSend: ImageView

    lateinit var viewModel: MyViewModel

    lateinit var editor: RichEditor

    var validation: Boolean = false
        get() = when {
            messageField.text.isEmpty() -> false
            else -> true
        }



    var feed: FeedModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MyViewModel::class.java) }!!
        viewModel.getReply2.observe(viewLifecycleOwner, EventObserver { t ->
            messageField.setText(t)
            showKeyboard(requireActivity(),messageField)
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

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            title = view.findViewById(R.id.title)
//            content = view.findViewById(R.id.content)
            nicknameLabel = view.findViewById(R.id.nicknameLabel)
            createAt = view.findViewById(R.id.createAt)
            view_cnt = view.findViewById(R.id.view_cnt)

//            ivContent = view.findViewById(R.id.ivContent)
//            ivContent2 = view.findViewById(R.id.ivContent2)
//            ivContent3 = view.findViewById(R.id.ivContent3)
//            ivContent4 = view.findViewById(R.id.ivContent4)
//            ivContent5 = view.findViewById(R.id.ivContent5)
//            ivContent6 = view.findViewById(R.id.ivContent6)
//            ivContent7 = view.findViewById(R.id.ivContent7)
//            ivContent8 = view.findViewById(R.id.ivContent8)

            profileView = view.findViewById(R.id.profileView)
            editor = view.findViewById(R.id.editor)

            feed?.let { values ->
                nicknameLabel.text = values.nickname
                if (values.profile_image_url.isNullOrEmpty()) {
                    profileView.setImageDrawable(
                        context?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.bg_profile_thumbnail
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

                val feedSeq = BaseApplication.shared.getSharedPreferences().getFeedId()
                FeedLoader.shared.getFeedDetail(feedSeq) { values ->
                    BaseApplication.shared.getSharedPreferences()
                        .setFeedDetailUserId(values.user_id)
                    title.text = values.title
//                    var spanned: Spanned =
//                        HtmlFormatter.formatHtml(HtmlFormatterBuilder().setHtml(values.content))
//                    content.text = spanned

                    val displayMetrics = this.resources?.displayMetrics
                    val dpWidth =
                        displayMetrics!!.widthPixels / displayMetrics.density

                        editor.html = values.content?.replace("alt=\"\">","alt=\"\" width=\"${dpWidth}\">")
                    editor.setInputEnabled(false)
//                    var num = values.feed_seq / 100
//                        content.setHtml("<html><body>하이루 Hello<img src='http://api.my-comment.co.kr:3000/img/feed/0/7_0.jpg'/ alt=\"\" width=\"320\">하하</body></html>" , HtmlHttpImageGetter(content))
//                        Glide.with(this)
//                            .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_0.jpg")
//                            .override(Target.SIZE_ORIGINAL)
//                            .transform(CenterCrop())
//                            .into(ivContent)
//                    values.imgs.let { imgs ->
//                        if (imgs!!.contains("0")) {
//
//                            ivContent.visibility = View.VISIBLE
//                            Log.e("tjdrnr", "img1")
//                            Glide.with(this)
//                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_0.jpg")
//                                .override(Target.SIZE_ORIGINAL)
//                                .transform(CenterCrop())
//                                .into(ivContent)
//                        }
//                        if (imgs.contains("1")) {
//                            ivContent2.visibility = View.VISIBLE
//                            Log.e("tjdrnr", "img2")
//                            Glide.with(this)
//                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_1.jpg")
//                                .override(Target.SIZE_ORIGINAL)
//                                .transform(CenterCrop())
//                                .into(ivContent2)
//                        }
//                        if (imgs.contains("2")) {
//                            ivContent3.visibility = View.VISIBLE
//                            Log.e("tjdrnr", "img3")
//                            Glide.with(this)
//                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_2.jpg")
//                                .override(Target.SIZE_ORIGINAL)
//                                .transform(CenterCrop())
//                                .into(ivContent3)
//                        }
//                        if (imgs.contains("3")) {
//                            ivContent4.visibility = View.VISIBLE
//                            Log.e("tjdrnr", "img4")
//                            Glide.with(this)
//                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_3.jpg")
//                                .override(Target.SIZE_ORIGINAL)
//                                .transform(CenterCrop())
//                                .into(ivContent4)
//                        }
//                        if (imgs.contains("4")) {
//                            ivContent5.visibility = View.VISIBLE
//                            Log.e("tjdrnr", "img5")
//                            Glide.with(this)
//                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_4.jpg")
//                                .override(Target.SIZE_ORIGINAL)
//                                .transform(CenterCrop())
//                                .into(ivContent5)
//                        }
//                        if (imgs.contains("5")) {
//                            ivContent6.visibility = View.VISIBLE
//                            Log.e("tjdrnr", "img6")
//                            Glide.with(this)
//                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_5.jpg")
//                                .override(Target.SIZE_ORIGINAL)
//                                .transform(CenterCrop())
//                                .into(ivContent6)
//                        }
//                        if (imgs.contains("6")) {
//                            ivContent7.visibility = View.VISIBLE
//                            Log.e("tjdrnr", "img7")
//                            Glide.with(this)
//                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_6.jpg")
//                                .override(Target.SIZE_ORIGINAL)
//                                .transform(CenterCrop())
//                                .into(ivContent7)
//                        }
//                        if (imgs.contains("7")) {
//                            ivContent8.visibility = View.VISIBLE
//                            Log.e("tjdrnr", "img8")
//                            Glide.with(this)
//                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_7.jpg")
//                                .override(Target.SIZE_ORIGINAL)
//                                .transform(CenterCrop())
//                                .into(ivContent8)
//
//                        }
//                    }
                }

                feedCommentFragment = FeedCommentFragment(values)

                activity?.let {
                    it.supportFragmentManager.beginTransaction()
                        .replace(R.id.feedCommentFragment, feedCommentFragment).commit()
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
}