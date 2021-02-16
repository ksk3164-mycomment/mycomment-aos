package kr.beimsupicures.mycomment.controllers.main.talk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.android.synthetic.main.fragment_drama_feed.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.api.models.FeedDetailModel
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.controllers.main.detail.webview.WebViewFragmentArgs
import kr.beimsupicures.mycomment.extensions.*


class DramaFeedDetailFragment : BaseFragment() {

    lateinit var title: TextView
    lateinit var content: TextView
    lateinit var nicknameLabel: TextView
    lateinit var createAt: TextView
    lateinit var view_cnt: TextView
    lateinit var profileView: ImageView
    lateinit var viewPager2: ViewPager2

    var feedDetail : FeedDetailModel? = null
    var feed : FeedModel? = null
    var talk : TalkModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drama_feed_detail, container, false)
    }

    override fun onResume() {
        super.onResume()
//        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            title = view.findViewById(R.id.title)
            content = view.findViewById(R.id.content)
            nicknameLabel = view.findViewById(R.id.nicknameLabel)
            createAt = view.findViewById(R.id.createAt)
            view_cnt = view.findViewById(R.id.view_cnt)
            profileView = view.findViewById(R.id.profileView)
            viewPager2 = view.findViewById(R.id.viewPager2)

            feed?.let { values ->
                Log.e("tjdrnr1",values.nickname)
                nicknameLabel.text = values.nickname
                Glide.with(this)
                    .load(values.profile_image_url)
                    .transform(CircleCrop(), CenterCrop())
                    .into(profileView)
                createAt.text = values.c_ts.timeline()
                view_cnt.text = "조회 ${values.view_cnt}"

                val feedSeq = BaseApplication.shared.getSharedPreferences().getFeedId()
                    FeedLoader.shared.getFeedDetail(feedSeq){ values ->
                        BaseApplication.shared.getSharedPreferences().setFeedDetailUserId(values.user_id)
                        title.text =  values.title
                        content.text = values.content
                }

                viewPager2.adapter = CustomFragmentStateAdapter(talk!!,this)



            }
        }

    }

    override fun loadModel() {
        super.loadModel()
        feed = BaseApplication.shared.getSharedPreferences().getFeed()
        Log.e("tjdrnr", feed.toString())
    }

    class CustomFragmentStateAdapter(var viewModel: TalkModel, fragmentActivity: DramaFeedDetailFragment) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 1
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                else -> RealTimeTalkFragment(viewModel,false)
            }
        }
    }

}

