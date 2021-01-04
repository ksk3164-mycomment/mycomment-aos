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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_item_watch_board.view.*
import kotlinx.android.synthetic.main.list_item_watch_comment.view.*
import kotlinx.android.synthetic.main.list_item_watch_content.view.*
import kotlinx.android.synthetic.main.list_item_watch_header.view.*
import kotlinx.android.synthetic.main.list_item_watch_header.view.contentLabel
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.CommentLoader
import kr.beimsupicures.mycomment.api.loaders.PickLoader
import kr.beimsupicures.mycomment.api.loaders.ReportLoader
import kr.beimsupicures.mycomment.api.loaders.WatchLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.BubbleUserListDialog
import kr.beimsupicures.mycomment.components.dialogs.ReportDialog
import kr.beimsupicures.mycomment.extensions.*

class WatchDetailAdapter(
    val activity: FragmentActivity?,
    var watch: WatchModel,
    var items: MutableList<KommentModel>,
    var playtime: Int,
    var count: Int,
    val onStart: () -> Unit,
    val onPause: () -> Unit,
    val onStop: () -> Unit,
    val onUpdated: (WatchModel) -> Unit,
    val onKicked: (Int) -> Unit,
    val onReplied: (String) -> Unit
) :
    RecyclerView.Adapter<WatchDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> {
                ViewHolder1(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_watch_header,
                        parent,
                        false
                    )
                )
            }
            1 -> {
                ViewHolder2(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_watch_content,
                        parent,
                        false
                    )
                )
            }
            2 -> {
                ViewHolder3(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_watch_board,
                        parent,
                        false
                    )
                )
            }
            3 -> {
                ViewHolder4(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_watch_comment,
                        parent,
                        false
                    )
                )
            }
            else -> {
                ViewHolder1(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_watch_header,
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
            else -> 3
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {

            is ViewHolder1 -> {
                holder.bind(watch)
            }

            is ViewHolder2 -> {
                holder.bind(watch)
            }

            is ViewHolder3 -> {
                holder.bind(count)
            }

            is ViewHolder4 -> {
                holder.bind(items[position-3], position-3)
            }
        }
    }

    open inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolder1(itemView: View) : ViewHolder(itemView) {
        val bookmarkView = itemView.bookmarkView
        val nicknameLabel = itemView.nicknameLabel
        val titleLabel = itemView.titleLabel
        val contentLabel = itemView.contentLabel
        val ownerView = itemView.ownerView
        val playLabel = itemView.playLabel
        val btnPlay = itemView.btnPlay
        val btnPause = itemView.btnPause
        val btnStop = itemView.btnStop
        val onairView = itemView.onairView
        val onairLabel = itemView.onairLabel

        fun bind(viewModel: WatchModel) {
            bookmarkView.visibility = View.INVISIBLE
            nicknameLabel.text = "${viewModel.owner?.nickname}"
            titleLabel.text = "${viewModel.title}"
            contentLabel.text = viewModel.content

            ownerView.visibility = View.VISIBLE

            onairView.isChecked = (watch.status == WatchModel.Status.onair)
            onairView.setOnCheckedChangeListener { buttonView, isChecked ->

                var newValue = this@WatchDetailAdapter.watch
                newValue.status = if (isChecked) WatchModel.Status.onair else WatchModel.Status.standby
                this@WatchDetailAdapter.watch = newValue

                onUpdated(newValue)
            }

            playLabel.text = playtime.showTime
            btnPlay.setOnClickListener {
                onStart()
            }
            btnPause.setOnClickListener {
                onPause()
            }
            btnStop.setOnClickListener {
                onStop()
            }
        }
    }

    inner class ViewHolder2(itemView: View) : ViewHolder(itemView) {
        val imageView = itemView.imageView

        fun bind(viewModel: WatchModel) {
            Glide.with(itemView.context).load(viewModel.title_image_url)
                .into(imageView)
            imageView.setOnClickListener { view ->
                val action = NavigationDirections.actionGlobalWebViewFragment(null, viewModel)
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

    var isLoad = hashMapOf<Int, Boolean>()
    inner class ViewHolder4(itemView: View) : ViewHolder(itemView) {
        val likeCountLabel = itemView.likeCountLabel
        val likeView = itemView.likeView
        val profileView = itemView.profileView
        val nameLabel = itemView.nameLabel
        val contentLabel = itemView.contentLabel
        val timelineLabel = itemView.timelineLabel
        val deleteView = itemView.deleteView
        val reportView = itemView.reportView
        val blockView = itemView.blockView
        val kickerView = itemView.kickerView
        val aliasLabel = itemView.aliasLabel
        val repliedView = itemView.replyView

        fun bind(viewModel: KommentModel, position: Int) {

            viewModel.owner.let { userModel ->
                Glide.with(itemView.context).load(userModel.profile_image_url)
                    .transform(CenterCrop(), CircleCrop())
                    .into(profileView)

                profileView.setOnClickListener { view ->
                    val action =
                        NavigationDirections.actionGlobalProfileFragment().setUserId(userModel.id)
                    view.findNavController().navigate(action)
                }

                nameLabel.text = userModel.nickname
                aliasLabel.text = if (viewModel.user_id == watch.owner?.id) "PD" else userModel.title
            }
            activity?.let { activity ->
                itemView.setBackgroundColor(if (viewModel.isMe) ContextCompat.getColor(activity.baseContext, R.color.paleGrey) else ContextCompat.getColor(activity.baseContext, android.R.color.white))
            }
            likeCountLabel.text = "${viewModel.pick_count.currencyValue}"
            likeView.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    if (viewModel.pick == true) R.drawable.bubble_fill else R.drawable.bubble_empty
                )
            )
            isLoad[position]?.let{ isLoad ->
                if (isLoad) {
                    if (viewModel.pick == true) likeView.setAnimation(R.raw.bubble_unlike) else likeView.setAnimation(R.raw.bubble_like)
                    likeView.playAnimation()
                }
            }

            likeCountLabel.setOnClickListener {
                CommentLoader.shared.getWatchPickedUsers(viewModel.id) { list ->
                    activity?.let { activity ->
                        BubbleUserListDialog(activity, list).show()
                    }
                }
            }

//            likeView.setOnClickListener {
//                isLoad[position]?.let { isLoad ->
//                    if (isLoad) return@setOnClickListener
//                }
//
//                BaseApplication.shared.getSharedPreferences().getUser()?.let {
//                    isLoad[position] = true
//                    when (viewModel.pick) {
//                        true -> {
//                            var newValue = items[position]
//                            items[position] = newValue
//                            newValue.pick = false
//                            items[position] = newValue
//                            notifyDataSetChanged()
//
//                            PickLoader.shared.unpick(
//                                category = PickModel.Category.komment,
//                                category_id = viewModel.id
//                            ) { pickModel ->
//                                var newValue = items[position]
//                                newValue.pick = pickModel.pick()
//                                newValue.pick_count = newValue.pick_count - 1
//                                items[position] = newValue
//
//                                val comment = items
//                                WatchLoader.shared.items = comment.toMutableList()
//
//                                isLoad[position] = false
//                                notifyDataSetChanged()
//
//                                FirebaseDatabase.getInstance().getReference("watch").child("${watch.id}").child("like").setValue("${viewModel.id}/${newValue.pick_count}")
//                            }
//                        }
//
//                        else -> {
//                            var newValue = items[position]
//                            items[position] = newValue
//                            newValue.pick = true
//                            items[position] = newValue
//                            notifyDataSetChanged()
//
//                            PickLoader.shared.pick(
//                                category = PickModel.Category.komment,
//                                category_owner_id = viewModel.user_id,
//                                category_id = viewModel.id
//                            ) { pickModel ->
//                                var newValue = items[position]
//                                newValue.pick = pickModel.pick()
//                                newValue.pick_count = newValue.pick_count + 1
//                                items[position] = newValue
//
//                                val comment = items
//                                WatchLoader.shared.items = comment.toMutableList()
//
//                                isLoad[position] = false
//                                notifyDataSetChanged()
//
//                                FirebaseDatabase.getInstance().getReference("watch").child("${watch.id}").child("like").setValue("${viewModel.id}/${newValue.pick_count}")
//                            }
//                        }
//                    }
//
//                } ?: run {
//                    activity?.let { activity ->
//                        activity.popup("로그인하시겠습니까?", "로그인") {
//                            Navigation.findNavController(activity, R.id.nav_host_fragment)
//                                .navigate(R.id.action_global_signInFragment)
//                        }
//                    }
//                }
//            }
            contentLabel.text = viewModel.content
            timelineLabel.text = "${viewModel.created_at.timeline()}"
            deleteView.visibility = if (viewModel.isMe) View.VISIBLE else View.GONE
            reportView.visibility = if (!viewModel.isMe) View.VISIBLE else View.GONE
            reportView.setOnClickListener { view ->
                activity?.supportFragmentManager?.let { fragmentManager ->

                    ReportDialog(view.context, didSelectAt = { reason ->
                        ReportLoader.shared.report(ReportModel.Category.komment, viewModel.id, reason) {
                            activity.alert("신고되었습니다.", "신고완료") { }
                        }
                    }).show(fragmentManager, "")
                }
            }
            blockView.setOnClickListener { view ->
                activity?.let { activity ->
                    activity.popup("해당 글을 차단하시겠습니까?", "차단하기") {
                        items.removeAt(position)
                        notifyDataSetChanged()
                    }
                }
            }

            deleteView.visibility = if (viewModel.isMe) View.VISIBLE else View.GONE
            deleteView.setOnClickListener { view ->
                activity?.let { activity ->
                    activity.popup("삭제하시겠습니까?", "삭제하기") {
                        WatchLoader.shared.deleteComment(viewModel.watch_id, viewModel.id) { comment ->

                            WatchLoader.shared.getCommentCountTotal(viewModel.watch_id) { total ->
                                WatchLoader.shared.getCommentCount(viewModel.watch_id) { count ->

                                    // Write a message to the database
                                    val database = FirebaseDatabase.getInstance()
                                    val myRef = database.getReference("watch").child("${watch.id}")
                                    myRef.setValue(HashMap<String, String>().apply {
                                        put("total", "${total}")
                                        put("count", "${count}")
                                    })

                                    activity.alert("삭제되었습니다.", "알림", {

                                    })

                                    items.removeAt(position)
                                    this@WatchDetailAdapter.count = count
                                    notifyDataSetChanged()

                                    val newValue = this@WatchDetailAdapter.items
                                    WatchLoader.shared.items = newValue
                                }
                            }
                        }
                    }
                }
            }

            kickerView.setOnClickListener {
                activity?.let { activity ->
                    activity.popup("강퇴하시겠습니까?", "강퇴하기") {
                        onKicked(viewModel.user_id)
                    }
                }
            }

            repliedView.setOnClickListener {
                val content = contentLabel.text.toString()
                val tag = "@${nameLabel.text}"
                val original = content.originalString()
                val replyMessage = "${tag} | ${original}\n"
                onReplied(replyMessage)
            }

            when (watch.isMe) {
                true -> {
                    kickerView.visibility = if (watch.owner?.id == viewModel.user_id) View.GONE else View.VISIBLE
                    blockView.visibility = View.GONE
                }
                false -> {
                    blockView.visibility = if (!viewModel.isMe) View.VISIBLE else View.GONE
                    kickerView.visibility = View.GONE
                }
            }

            val replyInfos = viewModel.content.getReplyInfo()
            if (replyInfos != null) {
                activity?.let { activity ->
                    val tag = replyInfos.first
                    val origin = replyInfos.second
                    contentLabel.text = viewModel.content.makeRepliedMessage(tag, origin, ContextCompat.getColor(activity.baseContext, R.color.dullTeal))

                    val nickname = tag.parseNickname()
                    if (!viewModel.isMe && nickname == BaseApplication.shared.getSharedPreferences().getUser()?.nickname)
                        itemView.setBackgroundColor(ContextCompat.getColor(activity.baseContext, R.color.lightBlueGrey))
                }
            }
        }
    }
}
