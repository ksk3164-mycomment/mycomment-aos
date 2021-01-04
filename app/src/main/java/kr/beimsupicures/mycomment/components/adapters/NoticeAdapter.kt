package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.islamkhsh.CardSliderAdapter
import kotlinx.android.synthetic.main.list_item_notice.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.NoticeModel

class NoticeAdapter(var items : MutableList<NoticeModel>) : CardSliderAdapter<NoticeAdapter.ViewHolder>() {

    override fun bindVH(holder: ViewHolder, position: Int) {
        when (holder) {

            is ViewHolder -> {
                holder.bind(items[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_notice,
                parent,
                false
            )
        )
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentLabel = itemView.contentLabel

        fun bind(viewModel: NoticeModel) {
            contentLabel.text = viewModel.content
        }
    }
}
