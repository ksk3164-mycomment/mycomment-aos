package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.android.synthetic.main.list_item_talk_comment.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.UserModel

class LikeUserAdapter(var items: MutableList<UserModel>) : RecyclerView.Adapter<LikeUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_like_user,
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
        val nameLabel = itemView.nameLabel
        val profileView = itemView.profileView

        fun bind(model: UserModel, position: Int) {
            Glide.with(itemView.context).load(model.profile_image_url)
                .transform(CenterCrop(), CircleCrop())
                .into(profileView)
            nameLabel.text = model.nickname
        }
    }

}