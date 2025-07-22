package com.example.zdravlje.ui.home.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.dashobard.EventsAndActivitiesRequest
import com.example.zdravlje.data.repository.DashboardRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.home.ActivitiesAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.home.DashboardViewModel
import com.example.zdravlje.ui.home.DashboardViewModelFactory
import com.example.zdravlje.ui.interfaces.ActivityClickListener
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.databinding.FragmentEventsBinding

class EventsFragment(val clickListener: ActivityClickListener) : BaseFragment() {

    lateinit var binding: FragmentEventsBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
        subscribeToObservers()
    }

    private fun setViewModels() {
        val dashboardRepository = DashboardRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val dashboardViewModelFactory =
            DashboardViewModelFactory(dashboardRepository, dataStoreRepository)

        dashboardViewModel = ViewModelProvider(
            requireActivity(),
            dashboardViewModelFactory
        ).get(DashboardViewModel::class.java)
    }

    private fun subscribeToObservers() {
        dashboardViewModel.dashboardPastResponseLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                binding.shimmer.visibility = View.GONE

                val filtredlistDailyEvents = response.body()?.map { dailyEvent ->
                    dailyEvent.copy(
                        events = dailyEvent.events.filter { event ->
                            event.eventTypeId == 6 || event.eventTypeId == 3
                        }
                    )
                }?.filter { dailyEvent ->
                    dailyEvent.events.isNotEmpty()
                } ?: emptyList()

                for (event in filtredlistDailyEvents) {
                    for (ev in event.events) {
                        Log.i("sdasadasd", ev.eventTypeId.toString())
                    }
                }

                if (filtredlistDailyEvents.size>0){
                    val eventsAdapter = ActivitiesAdapter(filtredlistDailyEvents, clickListener, requireActivity(), dashboardViewModel)
                    binding.eventActivityRecyclerView.layoutManager =
                        LinearLayoutManager(context)
                    binding.eventActivityRecyclerView.adapter = eventsAdapter
                    binding.eventActivityRecyclerView.visibility = View.VISIBLE
                }else{
                    binding.eventEmpty.visibility=View.VISIBLE
                }

            } else {
               binding.eventEmpty.visibility=View.VISIBLE
            }
        }
    }

    private fun setupUI() {
        val eventsAndActivitiesRequest = EventsAndActivitiesRequest(
            DateUtils.getSixMonthsBeforeDateTime(),
            DateUtils.getCurrentDateTimeFromMidnight(),
            sort_in = "DESC",
            limit_in = 100000,
            mobile_group = 1
        )

        eventsAndActivitiesRequest.pin=getStoredPin()
        dashboardViewModel.getPastDashboardEvents(
            eventsAndActivitiesRequest
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.eventActivityRecyclerView.visibility = View.GONE
            binding.shimmer.visibility = View.VISIBLE
            dashboardViewModel.getPastDashboardEvents(eventsAndActivitiesRequest)
        }
    }
}