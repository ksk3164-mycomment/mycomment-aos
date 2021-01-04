package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.android.synthetic.main.list_item_opinion.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.OpinionLoader
import kr.beimsupicures.mycomment.api.loaders.PickLoader
import kr.beimsupicures.mycomment.api.loaders.ReportLoader
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.ReportDialog
import kr.beimsupicures.mycomment.extensions.*

class OpinionAdapter(val activity: FragmentActivity?, var items: MutableList<OpinionModel>, val showReplyView: Boolean = false, val touchItemView: Boolean = false, val showCircieView: Boolean = true) :
    RecyclerView.Adapter<OpinionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_opinion,
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
        val bookmarkCountLabel = itemView.bookmarkCountLabel
        val bookmarkView = itemView.bookmarkView
        val timelineLabel = itemView.timelineLabel
        val deleteView = itemView.deleteView
        val replyView = itemView.replyView
        val reportView = itemView.reportView
        val blockView = itemView.blockView
        val circleView = itemView.circleView

        fun bind(viewModel: OpinionModel, position: Int) {
            itemView.setOnClickListener {
                if (touchItemView) {
                    activity?.let { activity ->
                        val action = NavigationDirections.actionGlobalDetailFragment().setAgendaId(viewModel.agenda_id)
                        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(action)
                    }
                }
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
            bookmarkCountLabel.text = viewModel.pick_count.toString()
            bookmarkCountLabel.visibility = if (viewModel.pick_count == 0) View.GONE else View.VISIBLE

            bookmarkView.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    if (viewModel.pick == true) R.drawable.bookmark_r else R.drawable.bookmark
                )
            )
            bookmarkView.setOnClickListener {

                BaseApplication.shared.getSharedPreferences().getUser()?.let {

                    when (viewModel.pick) {
                        true -> {
                            PickLoader.shared.unpick(
                                PickModel.Category.opinion,
                                viewModel.id
                            ) { pickModel ->
                                var newValue = items[position]
                                newValue.pick = pickModel.pick()
                                newValue.pick_count = newValue.pick_count - 1
                                items[position] = newValue
                                notifyDataSetChanged()
                            }
                        }

                        false -> {
                            PickLoader.shared.pick(
                                category = PickModel.Category.opinion,
                                category_owner_id = viewModel.user_id,
                                category_id = viewModel.id
                            ) { pickModel ->
                                var newValue = items[position]
                                newValue.pick = pickModel.pick()
                                newValue.pick_count = newValue.pick_count + 1
                                items[position] = newValue
                                notifyDataSetChanged()
                            }
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
            reportView.visibility = if (!viewModel.isMe()) View.VISIBLE else View.GONE
            reportView.setOnClickListener { view ->
                activity?.supportFragmentManager?.let { fragmentManager ->

                    ReportDialog(view.context, didSelectAt = { reason ->
                        ReportLoader.shared.report(ReportModel.Category.opinion, viewModel.id, reason) {
                            activity.alert("신고되었습니다.", "신고완료") { }
                        }
                    }).show(fragmentManager, "")
                }
            }
            blockView.visibility = if (!viewModel.isMe()) View.VISIBLE else View.GONE
            blockView.setOnClickListener { view ->
                activity?.let { activity ->
                    activity.popup("해당 글을 차단하시겠습니까?", "차단하기") {
                        items.removeAt(position)
                        notifyDataSetChanged()
                    }
                }
            }

            OpinionLoader.shared.getCommentCount(viewModel.id) { count ->
                replyView.text = "Reply (${count.currencyValue})"
            }

            replyView.setOnClickListener { view ->

                val action =
                    NavigationDirections.actionGlobalReplyFragment().setOpinionId(viewModel.id)
                view.findNavController().navigate(action)
            }

            when (showReplyView) {
                true -> {
                    replyView.visibility = View.GONE
                    circleView.visibility = View.GONE
                }
                false -> {

                    when (showCircieView) {
                        true -> {
                            OpinionLoader.shared.getReplyList(viewModel.id) { opinion ->
                                val filter = opinion.filter { it.read_at == null }
                                circleView.visibility = if (filter.size == 0) View.GONE else View.VISIBLE
                            }
                        }
                        false -> {
                            circleView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
