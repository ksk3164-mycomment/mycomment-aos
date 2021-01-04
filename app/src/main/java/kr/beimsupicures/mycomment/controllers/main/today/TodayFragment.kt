package kr.beimsupicures.mycomment.controllers.main.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.islamkhsh.CardSliderViewPager
import kr.beimsupicures.mycomment.api.loaders.AgendaLoader
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.AdLoader
import kr.beimsupicures.mycomment.api.models.AdModel
import kr.beimsupicures.mycomment.api.models.AgendaModel
import kr.beimsupicures.mycomment.components.adapters.AgendaAdapter
import kr.beimsupicures.mycomment.components.adapters.BannerAdapter
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.dp

class TodayFragment : BaseFragment() {

    var agenda: MutableList<AgendaModel> = mutableListOf()
    var ad: MutableList<AdModel> = mutableListOf()

    lateinit var agendaDetailView: RecyclerView
    lateinit var agendaAdapter: AgendaAdapter
    lateinit var bannerView: CardSliderViewPager
    lateinit var bannerAdapter: BannerAdapter
    lateinit var suggestView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onResume() {
        super.onResume()

        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            agendaAdapter = AgendaAdapter(activity, agenda)
            agendaDetailView = view.findViewById(R.id.agendaDetailView)
            agendaDetailView.layoutManager = LinearLayoutManager(context)
            agendaDetailView.adapter = agendaAdapter

            bannerView = view.findViewById(R.id.bannerView)
            bannerAdapter = BannerAdapter(activity, ad)
            bannerView.adapter = bannerAdapter
            bannerView.layoutParams.height =
                (((resources.configuration.screenWidthDp - 16 - 16).toFloat() / 343F) * 110F).toInt()
                    .dp

            suggestView = view.findViewById(R.id.suggestView)
            suggestView.setOnClickListener {
                val action = TodayFragmentDirections.actionTodayFragmentToSuggestFragment()
                view.findNavController().navigate(action)
            }
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        AgendaLoader.shared.getAgeanda { agenda ->
            this.agenda = agenda.toMutableList()
            agendaAdapter.items = this.agenda
            agendaAdapter.notifyDataSetChanged()
        }

        AdLoader.shared.getAdList(true, AdModel.Location.today) { ad ->
            this.ad = ad
            bannerAdapter.items = this.ad
            bannerAdapter.notifyDataSetChanged()
        }
    }
}