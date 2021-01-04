package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_category.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.CategoryModel

class CategoryAdapter(var items: MutableList<Pair<Boolean, CategoryModel>>, val didSelectAt: (Pair<Boolean, CategoryModel>) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_category,
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

        fun bind(viewModel: Pair<Boolean, CategoryModel>, position: Int) {
            itemView.setOnClickListener { view ->
                var newValue = items.map { Pair(false, it.second) }.toMutableList()
                newValue[position] = Pair(true, newValue[position].second)

                items = newValue
                notifyDataSetChanged()

                didSelectAt(newValue[position])
            }

            nameLabel.text = viewModel.second.name
            nameLabel.typeface = ResourcesCompat.getFont(itemView.context, if (viewModel.first) R.font.gyeonggi_batang_b else R.font.gyeonggi_batang_r)
            nameLabel.setTextColor(ContextCompat.getColor(itemView.context, if (viewModel.first) R.color.colorTextTitle else R.color.colorTextSegment))
        }
    }
}