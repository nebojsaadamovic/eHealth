package com.example.zdravlje.ui.home.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.ui.adaptersAndViewHolders.home.ActivityAdapter
import com.example.zdravlje.ui.interfaces.ActivityClickListener
import com.example.zdravlje.utilities.DateUtils.getDateWithWeekName
import com.nites.ezdravljeapp.databinding.FragmentNextOnScheduleBinding

class NextOnSchedule(
    val title: String,
    val nextActivities: DailyEvents,
    val clickListener: ActivityClickListener
) : Fragment() {

    lateinit var binding: FragmentNextOnScheduleBinding
    var listNextActivities: List<Event>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNextOnScheduleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        binding.title.text = title
        listNextActivities = nextActivities.events.filter { it.isFuture() }

        val eventsAdapter = ActivityAdapter(listNextActivities!!, clickListener)
        binding.eventsRecyclerView.layoutManager =
            LinearLayoutManager(context)
        binding.eventsRecyclerView.adapter = eventsAdapter
        binding.dateTextView.text = getDateWithWeekName(nextActivities.eventDate)
        binding.clBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}