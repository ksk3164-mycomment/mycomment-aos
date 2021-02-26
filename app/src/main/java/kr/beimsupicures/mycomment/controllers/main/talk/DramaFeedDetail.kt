package kr.beimsupicures.mycomment.controllers.main.talk

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.disklrucache.DiskLruCache
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.Target
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.fragment_drama_feed.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.api.models.FeedDetailModel
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.common.keyboard.KeyboardVisibilityUtils
import kr.beimsupicures.mycomment.components.adapters.TalkDetailAdapter
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.controllers.main.detail.webview.WebViewFragmentArgs
import kr.beimsupicures.mycomment.extensions.*
import org.sufficientlysecure.htmltextview.HtmlFormatter
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView


class DramaFeedDetailFragment : BaseFragment() {

    lateinit var title: TextView
    lateinit var content: HtmlTextView
    lateinit var nicknameLabel: TextView
    lateinit var ivContent: ImageView
    lateinit var ivContent2: ImageView
    lateinit var ivContent3: ImageView
    lateinit var ivContent4: ImageView
    lateinit var ivContent5: ImageView
    lateinit var ivContent6: ImageView
    lateinit var ivContent7: ImageView
    lateinit var ivContent8: ImageView
    lateinit var createAt: TextView
    lateinit var view_cnt: TextView
    lateinit var profileView: ImageView
    lateinit var feedCommentFragment: FeedCommentFragment
//    lateinit var editor: RichEditor

    var feed: FeedModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drama_feed_detail, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            title = view.findViewById(R.id.title)
            content = view.findViewById(R.id.content)
            nicknameLabel = view.findViewById(R.id.nicknameLabel)
            createAt = view.findViewById(R.id.createAt)
            view_cnt = view.findViewById(R.id.view_cnt)

            ivContent = view.findViewById(R.id.ivContent)
            ivContent2 = view.findViewById(R.id.ivContent2)
            ivContent3 = view.findViewById(R.id.ivContent3)
            ivContent4 = view.findViewById(R.id.ivContent4)
            ivContent5 = view.findViewById(R.id.ivContent5)
            ivContent6 = view.findViewById(R.id.ivContent6)
            ivContent7 = view.findViewById(R.id.ivContent7)
            ivContent8 = view.findViewById(R.id.ivContent8)

            profileView = view.findViewById(R.id.profileView)
//            editor = view.findViewById(R.id.editor)

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
                    var spanned: Spanned =
                        HtmlFormatter.formatHtml(HtmlFormatterBuilder().setHtml(values.content))
                    content.text = spanned
//                        editor.html = "\"하이루 Hello<img src='http://api.my-comment.co.kr:3000/img/feed/0/7_0.jpg' alt=\"\" width=\"320\">하하\""
                    var num = values.feed_seq / 100
//                        content.setHtml("하이루 Hello<img src='http://api.my-comment.co.kr:3000/img/feed/0/7_0.jpg'/>하하" , HtmlHttpImageGetter(content))
//                        Glide.with(this)
//                            .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_0.jpg")
//                            .override(Target.SIZE_ORIGINAL)
//                            .transform(CenterCrop())
//                            .into(ivContent)
                    Log.e("tjdrnr", "imgs = " + values.imgs)
                    values.imgs.let { imgs ->
                        if (imgs!!.contains("0")) {

                            ivContent.visibility = View.VISIBLE
                            Log.e("tjdrnr", "img1")
                            Glide.with(this)
                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_0.jpg")
                                .override(Target.SIZE_ORIGINAL)
                                .transform(CenterCrop())
                                .into(ivContent)
                        }
                        if (imgs.contains("1")) {
                            ivContent2.visibility = View.VISIBLE
                            Log.e("tjdrnr", "img2")
                            Glide.with(this)
                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_1.jpg")
                                .override(Target.SIZE_ORIGINAL)
                                .transform(CenterCrop())
                                .into(ivContent2)
                        }
                        if (imgs.contains("2")) {
                            ivContent3.visibility = View.VISIBLE
                            Log.e("tjdrnr", "img3")
                            Glide.with(this)
                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_2.jpg")
                                .override(Target.SIZE_ORIGINAL)
                                .transform(CenterCrop())
                                .into(ivContent3)
                        }
                        if (imgs.contains("3")) {
                            ivContent4.visibility = View.VISIBLE
                            Log.e("tjdrnr", "img4")
                            Glide.with(this)
                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_3.jpg")
                                .override(Target.SIZE_ORIGINAL)
                                .transform(CenterCrop())
                                .into(ivContent4)
                        }
                        if (imgs.contains("4")) {
                            ivContent5.visibility = View.VISIBLE
                            Log.e("tjdrnr", "img5")
                            Glide.with(this)
                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_4.jpg")
                                .override(Target.SIZE_ORIGINAL)
                                .transform(CenterCrop())
                                .into(ivContent5)
                        }
                        if (imgs.contains("5")) {
                            ivContent6.visibility = View.VISIBLE
                            Log.e("tjdrnr", "img6")
                            Glide.with(this)
                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_5.jpg")
                                .override(Target.SIZE_ORIGINAL)
                                .transform(CenterCrop())
                                .into(ivContent6)
                        }
                        if (imgs.contains("6")) {
                            ivContent7.visibility = View.VISIBLE
                            Log.e("tjdrnr", "img7")
                            Glide.with(this)
                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_6.jpg")
                                .override(Target.SIZE_ORIGINAL)
                                .transform(CenterCrop())
                                .into(ivContent7)
                        }
                        if (imgs.contains("7")) {
                            ivContent8.visibility = View.VISIBLE
                            Log.e("tjdrnr", "img8")
                            Glide.with(this)
                                .load("http://api.my-comment.co.kr:3000/img/feed-1020x/$num/${values.feed_seq}_7.jpg")
                                .override(Target.SIZE_ORIGINAL)
                                .transform(CenterCrop())
                                .into(ivContent8)

                        }
                    }
                }

                feedCommentFragment = FeedCommentFragment(values)

                activity?.let {
                    it.supportFragmentManager.beginTransaction()
                        .replace(R.id.feedCommentFragment, feedCommentFragment).commit()
                }
            }
        }
    }

    override fun loadModel() {
        super.loadModel()
        feed = BaseApplication.shared.getSharedPreferences().getFeed()
    }
}