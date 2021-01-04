package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_provider.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.ProviderModel

class ProviderAdapter(var items: MutableList<Pair<Boolean, ProviderModel>>, val didSelectAt: (Int, Pair<Boolean, ProviderModel>) -> Unit) :
    RecyclerView.Adapter<ProviderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_provider,
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
        val nameLabel = itemView.nameLabel

        fun bind(viewModel: Pair<Boolean, ProviderModel>, position: Int) {
            itemView.setOnClickListener { view ->
                var newValue = items.map { Pair(false, it.second) }.toMutableList()
                newValue[position] = Pair(true, newValue[position].second)

                items = newValue
                notifyDataSetChanged()

                didSelectAt(position, newValue[position])
            }

            nameLabel.background = ContextCompat.getDrawable(itemView.context, if (viewModel.first) R.drawable.bg_provider_selected else R.drawable.bg_provider_normal)
            nameLabel.text = viewModel.second.name
        }
    }
}