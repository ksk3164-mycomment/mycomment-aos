package kr.beimsupicures.mycomment.components.adapters

import android.util.Log
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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_item_talk.view.*
import kotlinx.android.synthetic.main.list_item_talk.view.descLabel
import kotlinx.android.synthetic.main.list_item_talk.view.profileView
import kotlinx.android.synthetic.main.list_item_talk.view.titleLabel
import kotlinx.android.synthetic.main.list_item_talk_comment.view.*
import kotlinx.android.synthetic.main.list_item_talk_content.view.*
import kotlinx.android.synthetic.main.list_item_talk_header.view.contentLabel
import kotlinx.android.synthetic.main.list_item_talk_tab.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.CommentLoader
import kr.beimsupicures.mycomment.api.loaders.PickLoader
import kr.beimsupicures.mycomment.api.loaders.ReportLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.common.isPushEnabledAtOSLevel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.BubbleUserListDialog
import kr.beimsupicures.mycomment.components.dialogs.ReportDialog
import kr.beimsupicures.mycomment.components.dialogs.WaterDropDialog
import kr.beimsupicures.mycomment.extensions.*

class TalkDetailAdapter2(
    val activity: FragmentActivity?,
    var talk: TalkModel,
    var items: MutableList<CommentModel>,
    var count: Int,
    var real : String,
    val onReplied: (String) -> Unit
) :
    RecyclerView.Adapter<TalkDetailAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> {
                ViewHolder2(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_talk_content,
                        parent,
                        false
                    )
                )
            }
            1 -> {
                ViewHolder1(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_talk_header,
                        parent,
                        false
                    )
                )

            }
            2 -> {
                ViewHolder3(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_talk_board,
                        parent,
                        false
                    )
                )
            }
            3 -> {
                ViewHolder5(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_talk_tab,
                        parent,
                        false
                    )
                )
            }
            4 -> {
                ViewHolder4(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_talk_comment,
                        parent,
                        false
                    )
                )
            }
            else -> {
                ViewHolder1(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_talk_header,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return 3 + items.count()
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            else -> 4
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {

            is ViewHolder1 -> {
                holder.bind(talk)
            }

            is ViewHolder2 -> {
                holder.bind(talk)
            }

            is ViewHolder3 -> {
                holder.bind(count)
            }

            is ViewHolder5 -> {
                holder.bind(real)
            }

            is ViewHolder4 -> {
                holder.bind(items[position - 3], position - 3)
            }
        }
    }

    open inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolder1(itemView: View) : ViewHolder(itemView) {
        val bookmarkView = itemView.bookmarkView
        val titleLabel = itemView.titleLabel
        val descLabel = itemView.descLabel
        val contentLabel = itemView.contentLabel

        fun bind(viewModel: TalkModel) {
            bookmarkView.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    if (viewModel.pick == true) R.drawable.bookmark_full_green else R.drawable.bookmark_empty_black
                )
            )
            bookmarkView.setOnClickListener {
                Log.e("tjdrnr","gld")

                BaseApplication.shared.getSharedPreferences().getUser()?.let {

                    when (viewModel.pick) {
                        true -> {
                            PickLoader.shared.unpick(
                                PickModel.Category.talk,
                                viewModel.id
                            ) { pickModel ->
                                var newValue = talk
                                newValue.pick = pickModel.pick()
                                talk = newValue
                                notifyDataSetChanged()
                            }
                        }

                        false -> {
                            activity?.supportFragmentManager?.let { fragmentManager ->
                                WaterDropDialog.newInstance(
                                    if (isPushEnabledAtOSLevel(activity)) {
                                        WaterDropDialog.NotificationSetting.allowed
                                    } else {
                                        WaterDropDialog.NotificationSetting.denied
                                    },
                                    viewModel.title
                                ).show(fragmentManager, "")
                            }
                            PickLoader.shared.pick(
                                PickModel.Category.talk,
                                category_owner_id = null,
                                category_id = viewModel.id
                            ) { pickModel ->
                                var newValue = talk
                                newValue.pick = pickModel.pick()
                                talk = newValue
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
            titleLabel.text = viewModel.title
            descLabel.text = "${viewModel.dayString} ${viewModel.openTimeString}"
            contentLabel.text = viewModel.content
        }
    }

    inner class ViewHolder2(itemView: View) : ViewHolder(itemView) {
        val imageView = itemView.imageView

        fun bind(viewModel: TalkModel) {
            Glide.with(itemView.context).load(viewModel.content_image_url)
                .into(imageView)
            imageView.setOnClickListener { view ->
                val action =
                    NavigationDirections.actionGlobalWebViewFragment(viewModel, null)
                view.findNavController().navigate(action)
            }
        }
    }

    inner class ViewHolder3(itemView: View) : ViewHolder(itemView) {
        val countLabel = itemView.countLabel

        fun bind(viewModel: Int) {
            countLabel.text = "${viewModel.currencyValue}개의 톡"
        }
    }
    inner class ViewHolder5(itemView: View) : ViewHolder(itemView) {
        var realtimetalkaa = itemView.realtimetalk

        fun bind(viewModel: String) {
//            realtimetalkaa = "${viewModel}개의 톡"
        }
    }

    var isLoad = hashMapOf<Int, Boolean>()

    inner class ViewHolder4(itemView: View) : ViewHolder(itemView) {
        val likeCountLabel = itemView.likeCountLabel
        val likeView = itemView.likeView
        val profileView = itemView.profileView
        val nameLabel = itemView.nameLabel
        val aliasLabel = itemView.aliasLabel
        val contentLabel = itemView.contentLabel
        val timelineLabel = itemView.timelineLabel
        val deleteView = itemView.deleteView
        val reportView = itemView.reportView
        val blockView = itemView.blockView
        val replyView = itemView.replyView
        val likeBackgroundView = itemView.likeBackgroundView

        private fun showLikeMeusers(viewModel: CommentModel): Boolean {
            if (viewModel.isMe) {
                CommentLoader.shared.getTalkPickedUsers(viewModel.id) { list ->
                    activity?.let { activity ->
                        BubbleUserListDialog(activity, list).show()
                    }
                }
                return true
            } else
                return false
        }

        fun bind(viewModel: CommentModel, position: Int) {

            viewModel.owner?.let { userModel ->
                Glide.with(itemView.context).load(userModel.profile_image_url)
                    .transform(CenterCrop(), CircleCrop())
                    .into(profileView)

                profileView.setOnClickListener { view ->
                    val action =
                        NavigationDirections.actionGlobalProfileFragment(userModel.id)
                    view.findNavController().navigate(action)
                }

                nameLabel.text = userModel.nickname
                aliasLabel.text = userModel.title
            }

            activity?.let { activity ->
                likeBackgroundView.background =
                    ContextCompat.getDrawable(
                        activity.baseContext,
                        if (viewModel.isMe) R.drawable.bg_like_box else R.drawable.bg_clear_box
                    )
                likeView.background =
                    ContextCompat.getDrawable(
                        itemView.context,
                        if (viewModel.isMe) R.drawable.bubble_white else {
                            if (viewModel.pick == true) R.drawable.bubble_fill else R.drawable.bubble_empty
                        }
                    )
                likeCountLabel.setTextColor(
                    ContextCompat.getColor(
                        activity.baseContext, if (viewModel.isMe) R.color.white else R.color.black
                    )
                )
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        activity.baseContext,
                        if (viewModel.isMe) R.color.paleGrey else android.R.color.white
                    )
                )
            }
            likeCountLabel.text = "${viewModel.pick_count.currencyValue}"

            likeCountLabel.setOnClickListener {
                showLikeMeusers(viewModel);
            }

            likeBackgroundView.setOnClickListener {
                showLikeMeusers(viewModel);
            }

            likeView.setOnClickListener {
                if (showLikeMeusers(viewModel)) {
                    return@setOnClickListener
                }

                isLoad[position]?.let { isLoad ->
                    if (isLoad) return@setOnClickListener
                }

                BaseApplication.shared.getSharedPreferences().getUser()?.let {
                    isLoad[position] = true
                    when (viewModel.pick) {
                        true -> {
                            var newValue = items[position]
                            items[position] = newValue
                            newValue.pick = false
                            items[position] = newValue
                            notifyDataSetChanged()

                            PickLoader.shared.unpick(
                                category = PickModel.Category.comment,
                                category_id = viewModel.id
                            ) { pickModel ->
                                var newValue = items[position]
                                newValue.pick = pickModel.pick()
                                newValue.pick_count = newValue.pick_count - 1
                                items[position] = newValue

                                val comment = this@TalkDetailAdapter2.items
                                CommentLoader.shared.items = comment.toMutableList()

                                isLoad[position] = false
                                notifyDataSetChanged()

                                FirebaseDatabase.getInstance().getReference("talk")
                                    .child("${talk.id}").child("like")
                                    .setValue("${viewModel.id}/${newValue.pick_count}")
                            }
                        }

                        else -> {
                            var newValue = items[position]
                            items[position] = newValue
                            newValue.pick = true
                            items[position] = newValue
                            notifyDataSetChanged()

                            PickLoader.shared.pick(
                                category = PickModel.Category.comment,
                                category_owner_id = viewModel.user_id,
                                category_id = viewModel.id
                            ) { pickModel ->
                                var newValue = items[position]
                                newValue.pick = pickModel.pick()
                                newValue.pick_count = newValue.pick_count + 1
                                items[position] = newValue

                                val comment = this@TalkDetailAdapter2.items
                                CommentLoader.shared.items = comment.toMutableList()

                                isLoad[position] = false
                                notifyDataSetChanged()

                                FirebaseDatabase.getInstance().getReference("talk")
                                    .child("${talk.id}").child("like")
                                    .setValue("${viewModel.id}/${newValue.pick_count}")
                            }
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
            contentLabel.text = viewModel.content
            timelineLabel.text = "${viewModel.created_at.timeline()}"
            deleteView.visibility = if (viewModel.isMe) View.VISIBLE else View.GONE
            reportView.visibility = if (!viewModel.isMe) View.VISIBLE else View.GONE
            reportView.setOnClickListener { view ->
                activity?.supportFragmentManager?.let { fragmentManager ->

                    ReportDialog(view.context, didSelectAt = { reason ->
                        ReportLoader.shared.report(
                            ReportModel.Category.comment,
                            viewModel.id,
                            reason
                        ) {
                            activity.alert("신고되었습니다.", "신고완료") { }
                        }
                    }).show(fragmentManager, "")
                }
            }
            blockView.visibility = if (!viewModel.isMe) View.VISIBLE else View.GONE
            blockView.setOnClickListener { view ->
                activity?.let { activity ->
                    activity.popup("해당 글을 차단하시겠습니까?", "차단하기") {
                        items.removeAt(position)
                        notifyDataSetChanged()
                    }
                }
            }

            deleteView.setOnClickListener { view ->
                activity?.let { activity ->
                    activity.popup("정말 삭제하시겠습니까?", "삭제하기") {
                        CommentLoader.shared.deleteComment(viewModel.id) { comment ->

                            CommentLoader.shared.getCommentCountTotal(viewModel.id) { total ->
                                CommentLoader.shared.getCommentCount(viewModel.id) { count ->

                                    // Write a message to the database
                                    val database = FirebaseDatabase.getInstance()
                                    val myRef = database.getReference("talk").child("${talk.id}")
                                    myRef.setValue(HashMap<String, String>().apply {
                                        put("total", "${total}")
                                        put("count", "${count}")
                                    })

                                    activity.alert("삭제되었습니다.", "알림") {

                                    }
                                    items.removeAt(position)
                                    notifyDataSetChanged()

                                    val newValue = this@TalkDetailAdapter2.items
                                    CommentLoader.shared.items = newValue
                                }
                            }
                        }
                    }
                }
            }
            replyView.setOnClickListener {
                val content = contentLabel.text.toString()
                val tag = "@${nameLabel.text}"
                val original = content.originalString()
                val replyMessage = "${tag} | ${original}\n"
                onReplied(replyMessage)
            }
            val replyInfos = viewModel.content.getReplyInfo()
            if (replyInfos != null) {
                activity?.let { activity ->
                    val tag = replyInfos.first
                    val origin = replyInfos.second
                    contentLabel.text = viewModel.content.makeRepliedMessage(
                        tag,
                        origin,
                        ContextCompat.getColor(activity.baseContext, R.color.dullTeal)
                    )

                    val nickname = tag.parseNickname()
                    if (!viewModel.isMe && nickname == BaseApplication.shared.getSharedPreferences()
                            .getUser()?.nickname
                    )
                        itemView.setBackgroundColor(
                            ContextCompat.getColor(
                                activity.baseContext,
                                R.color.lightBlueGrey
                            )
                        )
                }
            }
        }
    }


}