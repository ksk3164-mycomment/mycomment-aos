package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_agenda_detail.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.PickLoader
import kr.beimsupicures.mycomment.api.models.AgendaModel
import kr.beimsupicures.mycomment.api.models.PickModel
import kr.beimsupicures.mycomment.api.models.pick
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser
import kr.beimsupicures.mycomment.extensions.popup

class DetailAdapter(val activity: FragmentActivity?, var items: MutableList<AgendaModel>) :
    RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_agenda_detail,
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
        val titleLabel = itemView.titleLabel
        val contentLabel = itemView.contentLabel
        val likeCountLabel = itemView.bookmarkCountLabel
        val bookmarkView = itemView.bookmarkView

        fun bind(viewModel: AgendaModel, position: Int) {
            titleLabel.text = viewModel.title
            contentLabel.text = viewModel.content
            likeCountLabel.text = viewModel.pick_count.toString()

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
                                PickModel.Category.agenda,
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
                                PickModel.Category.agenda,
                                category_owner_id = null,
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
        }
    }
}