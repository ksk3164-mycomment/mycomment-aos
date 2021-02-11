package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.list_item_drama_feed.view.*
import kotlinx.android.synthetic.main.list_item_talk_comment.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.extensions.timeline

class DramaFeedAdapter(var items: MutableList<FeedModel>) :
    RecyclerView.Adapter<DramaFeedAdapter.ViewHolder>() {


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
        holder.bind(items[position], position)
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
                Glide.with(itemView.context)
                    .load("http://api.my-comment.co.kr:3000${model.feed_thumbnail}")
                    .override(400,Target.SIZE_ORIGINAL)
                    .fitCenter()
                    .into(thumbnail)
                title.visibility = View.VISIBLE
                title2.visibility = View.GONE
            }

            if (model.profile_image_url.isNullOrEmpty()) {
                profile.setImageResource(R.drawable.bg_profile_thumbnail)
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
        }
    }
}