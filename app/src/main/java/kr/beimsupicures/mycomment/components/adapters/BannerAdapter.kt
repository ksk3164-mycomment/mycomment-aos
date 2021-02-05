package kr.beimsupicures.mycomment.components.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.islamkhsh.CardSliderAdapter
import kotlinx.android.synthetic.main.list_item_banner.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.AdModel

class BannerAdapter(val activity: FragmentActivity?, var items : MutableList<AdModel>) : CardSliderAdapter<BannerAdapter.ViewHolder>() {

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
                R.layout.list_item_banner,
                parent,
                false
            )
        )
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.imageView


        fun bind(viewModel: AdModel) {
            viewModel.banner_image_url?.let { image_url ->
                Glide.with(itemView.context).load(image_url)
                    .transform(CenterCrop())
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                    .into(imageView)
            }

            imageView.setOnClickListener { view ->
                viewModel.url?.let { url ->
                    view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }
        }
    }
}