package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.list_item_drama_feed.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.setFeed
import kr.beimsupicures.mycomment.extensions.setFeedId
import kr.beimsupicures.mycomment.extensions.timeline

class DramaFeedAdapter(var activity: FragmentActivity?, var items: MutableList<FeedModel>) :
    RecyclerView.Adapter<DramaFeedAdapter.ViewHolder>() {

    var lastPosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_drama_feed,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {

                val safePosition = holder.adapterPosition
                holder.bind(items[safePosition], safePosition)
//                setAnimation(holder.itemView, position)
            }
        }
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.title
        val title2 = itemView.title2
        val thumbnail = itemView.thumbnail
        val profile = itemView.profile
        val createAt = itemView.createAt
        val userId = itemView.userId
        val view_cnt = itemView.view_cnt


        fun bind(model: FeedModel, position: Int) {

            if (model.feed_thumbnail.isNullOrBlank()) {
                title.visibility = View.GONE
                title2.visibility = View.VISIBLE
                thumbnail.visibility = View.GONE
            } else {
                thumbnail.visibility = View.VISIBLE
                var feedThumbnail = model.feed_thumbnail.substringAfter("/img/")
                Glide.with(itemView.context)
                    .load("https://myco-img-re.s3.ap-northeast-2.amazonaws.com/$feedThumbnail")
                    .thumbnail(0.1f)
                    .override(Target.SIZE_ORIGINAL)
                    .transform(CenterCrop(),RoundedCorners(30))
                    .into(thumbnail)
                title.visibility = View.VISIBLE
                title2.visibility = View.GONE
            }

            if (model.profile_image_url.isNullOrEmpty()) {
                profile.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.bg_profile_original
                    )
                )
            } else {
                Glide.with(itemView.context)
                    .load(model.profile_image_url)
                    .transform(CircleCrop(), CenterCrop())
                    .into(profile)
            }

            title.text = model.title
            title2.text = model.title
            userId.text = model.nickname
            view_cnt.text = "조회 ${model.view_cnt}"
            createAt.text = model.c_ts.timeline()

            itemView.setOnClickListener {
                BaseApplication.shared.getSharedPreferences().setFeedId(model.feed_seq)
                BaseApplication.shared.getSharedPreferences().setFeed(model)

                activity?.let { it

                    val action = NavigationDirections.actionGlobalDramaFeedDetailFragment()
                    Navigation.findNavController(it, R.id.nav_host_fragment)
                        .navigate(action)

                }


            }
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            var animation: Animation =
                AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

}