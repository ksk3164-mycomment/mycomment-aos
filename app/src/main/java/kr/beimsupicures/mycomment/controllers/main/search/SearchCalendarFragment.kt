package kr.beimsupicures.mycomment.controllers.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.api.loaders.AgendaLoader
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.AgendaModel
import kr.beimsupicures.mycomment.components.adapters.AgendaAdapter
import kr.beimsupicures.mycomment.components.fragments.BaseFragment

class SearchCalendarFragment : BaseFragment() {

    var agenda: MutableList<AgendaModel> = mutableListOf()

    lateinit var calendarView: CalendarView
    lateinit var agendaDetailView: RecyclerView
    lateinit var agendaAdapter: AgendaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_calendar, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            calendarView = view.findViewById(R.id.calendarView)
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                val date = "$year-${month+1}-$dayOfMonth"
                AgendaLoader.shared.getAgenda(date) { agenda ->
                    this.agenda = agenda.toMutableList()
                    agendaAdapter.items = this.agenda
                    agendaAdapter.notifyDataSetChanged()
                }
            }

            agendaAdapter = AgendaAdapter(activity, agenda)
            agendaDetailView = view.findViewById(R.id.agendaDetailView)
            agendaDetailView.layoutManager = LinearLayoutManager(context)
            agendaDetailView.adapter = agendaAdapter

            fetchModel()
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        AgendaLoader.shared.getAgeanda { agenda ->
            this.agenda = agenda.toMutableList()
            agendaAdapter.items = this.agenda
            agendaAdapter.notifyDataSetChanged()
        }
    }
}