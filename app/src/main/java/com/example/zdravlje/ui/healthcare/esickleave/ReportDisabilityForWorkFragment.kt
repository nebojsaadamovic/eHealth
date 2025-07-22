package com.example.zdravlje.ui.healthcare.esickleave

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.response.healthcare.SickLeaveResponse
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.ReportDisabilityForWorkAdapter
import com.example.zdravlje.ui.home.HomeActivity
import com.nites.ezdravljeapp.databinding.FragmentReportDisabilityForWorkBinding

class ReportDisabilityForWorkFragment(
    private var data: SickLeaveResponse,
    private var userPin: String?
) : Fragment() {

    lateinit var binding: FragmentReportDisabilityForWorkBinding
    lateinit var reportDisabilityForWorkAdapter: ReportDisabilityForWorkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportDisabilityForWorkBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val urlValue = data.commissionDecisions?.firstOrNull()?.urlForCommissionDecision
        if (urlValue != null) {
            setAdapter()
        } else {
            binding.wrapperData.visibility = View.VISIBLE
        }
        setupUI()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    private fun setAdapter() {
        reportDisabilityForWorkAdapter =
            ReportDisabilityForWorkAdapter(
                data.commissionDecisions,
                parentFragmentManager,
                viewLifecycleOwner,
                requireContext(),
                userPin,
                data
            )
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.reciclerViewReportDisabilityWork.layoutManager = linearLayoutManager
        binding.reciclerViewReportDisabilityWork.adapter = reportDisabilityForWorkAdapter
    }
}