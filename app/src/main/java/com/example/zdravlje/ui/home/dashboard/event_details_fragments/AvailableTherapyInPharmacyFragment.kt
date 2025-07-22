package com.example.zdravlje.ui.home.dashboard.event_details_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.dashboard.event_payload.TherapyEventPayloadParcelable
import com.nites.ezdravljeapp.databinding.FragmentActivityDetailsBinding

class AvailableTherapyInPharmacyFragment : Fragment() {

    lateinit var binding: FragmentActivityDetailsBinding
    private var therapyEventPayload: TherapyEventPayloadParcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivityDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            therapyEventPayload = it.getParcelable("therapyEventPayload")
        }
        setupUI()
    }

    private fun setupUI() {
        binding.activityTitle.text = therapyEventPayload?.date
        binding.mtvTerValue.text = therapyEventPayload?.prescriptionTypeName
        binding.mtvTimeValue.text = therapyEventPayload?.date
        binding.mtvInfoValue.text = therapyEventPayload?.details
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}