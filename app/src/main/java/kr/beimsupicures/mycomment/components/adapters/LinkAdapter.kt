package kr.beimsupicures.mycomment.components.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_agenda_link.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.LinkModel


class LinkAdapter(var items: List<LinkModel>) :
    RecyclerView.Adapter<LinkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_agenda_link,
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
                holder.bind(items[position])
            }

        }
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linkLabel = itemView.linkLabel

        fun bind(viewModel: LinkModel) {
            itemView.setOnClickListener { view ->
                view.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(viewModel.content)
                    )
                )
            }

            linkLabel.text = viewModel.content
        }
    }
}