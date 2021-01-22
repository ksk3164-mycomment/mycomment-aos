package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_talk.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.PickLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.common.isPushEnabledAtOSLevel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.WaterDropDialog
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser
import kr.beimsupicures.mycomment.extensions.popup

class TalkAdapter(val activity: FragmentActivity?, var items: MutableList<TalkModel>) :
    RecyclerView.Adapter<TalkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_talk,
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
        val bookmarkView = itemView.bookmarkView
        val onAirView = itemView.onAirView
        val profileView = itemView.profileView
        val titleLabel = itemView.titleLabel
        val descLabel = itemView.descLabel
        val countLabel = itemView.countLabel

        fun bind(viewModel: TalkModel, position: Int) {
            bookmarkView.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    if (viewModel.pick == true) R.drawable.bookmark_full else R.drawable.bookmark
                )
            )
            bookmarkView.setOnClickListener {
                BaseApplication.shared.getSharedPreferences().getUser()?.let {

                    when (viewModel.pick) {
                        true -> {
                            PickLoader.shared.unpick(
                                PickModel.Category.talk,
                                viewModel.id
                            ) { pickModel ->
                                val newValue = items[position]
                                newValue.pick = pickModel.pick()
                                items[position] = newValue
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
                                var newValue = items[position]
                                newValue.pick = pickModel.pick()
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
            onAirView.visibility = if (viewModel.onAir) View.VISIBLE else View.GONE
            Glide.with(itemView.context).load(viewModel.title_image_url).into(profileView)
            titleLabel.text = viewModel.title
            descLabel.text = "${viewModel.dayString} ${viewModel.openTimeString}"
            countLabel.text = viewModel.talk_count.toString()

            itemView.setOnClickListener {
                activity?.let { activity ->

                    val action = NavigationDirections.actionGlobalTalkDetailFragment(viewModel)
                    Navigation.findNavController(activity, R.id.nav_host_fragment)
                        .navigate(action)
                }
            }
        }
    }
}
