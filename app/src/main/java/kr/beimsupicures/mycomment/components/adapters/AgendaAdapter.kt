package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_agenda.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.AgendaLoader
import kr.beimsupicures.mycomment.api.loaders.PickLoader
import kr.beimsupicures.mycomment.api.models.AgendaModel
import kr.beimsupicures.mycomment.api.models.PickModel
import kr.beimsupicures.mycomment.api.models.pick
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.currencyValue
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser
import kr.beimsupicures.mycomment.extensions.popup

class AgendaAdapter(val activity: FragmentActivity?, var items: MutableList<AgendaModel>) :
    RecyclerView.Adapter<AgendaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_agenda,
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
        val categoryLabel = itemView.categoryLabel
        val titleLabel = itemView.titleLabel
        val likeCountLabel = itemView.bookmarkCountLabel
        val bookmarkView = itemView.bookmarkView
        val viewCountLabel = itemView.viewCountLabel

        fun bind(viewModel: AgendaModel, position: Int) {
            itemView.setOnClickListener { view ->
                AgendaLoader.shared.increaseViewCount(viewModel.id) {
                    val action =
                        NavigationDirections.actionGlobalDetailFragment().setAgendaId(viewModel.id)
                    view.findNavController().navigate(action)
                }
            }

            categoryLabel.text = viewModel.category
            titleLabel.text = viewModel.title
            likeCountLabel.text = viewModel.pick_count.toString()
            viewCountLabel.text = "${viewModel.view_count.currencyValue} views"

            bookmarkView.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    if (viewModel.pick == true) R.drawable.bookmark_r else R.drawable.bookmark
                )
            )
            bookmarkView.setOnClickListener { view ->

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
