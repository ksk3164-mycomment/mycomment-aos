package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.list_item_opinion.view.*
import kotlinx.android.synthetic.main.list_item_opinion.view.contentLabel
import kotlinx.android.synthetic.main.list_item_opinion.view.deleteView
import kotlinx.android.synthetic.main.list_item_opinion.view.nameLabel
import kotlinx.android.synthetic.main.list_item_opinion.view.profileView
import kotlinx.android.synthetic.main.list_item_opinion.view.timelineLabel
import kotlinx.android.synthetic.main.list_item_reply.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.OpinionLoader
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.ReplyModel
import kr.beimsupicures.mycomment.api.models.isMe
import kr.beimsupicures.mycomment.api.models.nameOnly
import kr.beimsupicures.mycomment.extensions.popup
import kr.beimsupicures.mycomment.extensions.timeline

class ReplyAdapter(val activity: FragmentActivity?, var items: MutableList<ReplyModel>) :
    RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_reply,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {

            is ViewHolder -> {
                holder.bind(items[position], position)
            }

        }
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileView = itemView.profileView
        val nameLabel = itemView.nameLabel
        val contentLabel = itemView.contentLabel
        val timelineLabel = itemView.timelineLabel
        val deleteView = itemView.deleteView

        fun bind(viewModel: ReplyModel, position: Int) {
            itemView.setOnClickListener { view ->

            }

            UserLoader.shared.getUser(viewModel.user_id) { user ->
                profileView.setOnClickListener { view ->
                    val action = NavigationDirections.actionGlobalProfileFragment().setUserId(user.id)
                    view.findNavController().navigate(action)
                }
                nameLabel.text = user.nameOnly()
                user.profile_image_url?.let { profile_image_url ->
                    Glide.with(itemView.context).load(profile_image_url)
                        .transform(CenterCrop(), CircleCrop())
                        .into(profileView)

                } ?: run {
                    profileView.setImageResource(android.R.color.transparent)
                }
            }

            contentLabel.text = viewModel.content
            timelineLabel.text = viewModel.created_at.timeline()
            deleteView.visibility = if (viewModel.isMe()) View.VISIBLE else View.GONE
            deleteView.setOnClickListener { view ->

                activity?.let { activity ->
                    activity.popup("삭제하시겠습니까?", "삭제하기") {
                        OpinionLoader.shared.deleteOpinion(viewModel.id) { opinion ->
                            items.removeAt(position)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}