package com.example.zdravlje.ui.home.dashboard.event_details_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.nites.ezdravljeapp.databinding.FragmentPersonalyActivityBinding


class PersonallyActivityFragment(var event: Event) : Fragment() {


    lateinit var binding: FragmentPersonalyActivityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPersonalyActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    fun setupUI() {
        if (event != null) {
            binding.activityDate.text = event.eventStartTime
            binding.mtvTerValue.text = event.eventTitle
            binding.mtvInfoValue.text = event.eventDescription
            binding.mtvTimeValue.text = event.eventStartTime
            binding.ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }
}