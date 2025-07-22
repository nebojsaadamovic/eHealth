package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.response.healthcare.DiagnosisEntry
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.PrescribedListLimitationsAdapter
import com.nites.ezdravljeapp.databinding.FragmentLimitationsBinding
import kotlin.math.roundToInt

class LimitationsFragment(
    private val prescribedObject: PrescribedListMedicamentResponse
) : Fragment() {

    private lateinit var binding: FragmentLimitationsBinding
    private lateinit var prescribedListLimitationsAdapter: PrescribedListLimitationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLimitationsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        val diagnosisList = prescribedObject.toDiagnosisList()
        setAdapter(diagnosisList)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun PrescribedListMedicamentResponse.toDiagnosisList(): List<DiagnosisEntry> {
        val participation = this.cbmedrfzoProcenatucesca
            ?.split(";")
            ?.map {
                val number = it.trim().toDoubleOrNull() ?: 0.0
                val rounded = number.roundToInt().toString()
                if (rounded.length > 2) rounded.take(2) else rounded
            } ?: emptyList()

        val quantities = this.quantity
            ?.split(";")
            ?.map {
                val number = it.trim().toDoubleOrNull()?.roundToInt() ?: 0
                number.toString()
            } ?: emptyList()

        val yearsFrom = this.yearsFrom?.split(";")?.map { it.trim() } ?: emptyList()
        val yearsTo = this.yearsTo?.split(";")?.map { it.trim() } ?: emptyList()
        val diagnoses = this.descriptionDagnosis?.split(";")?.map { it.trim() } ?: emptyList()

        return diagnoses.mapIndexed { index, diagnosis ->
            DiagnosisEntry(
                diagnosis = diagnosis.takeUnless { it.isBlank() } ?: "/",
                participation = participation.getOrNull(index).takeUnless { it.isNullOrBlank() } ?: "0",
                quantity = quantities.getOrNull(index).takeUnless { it.isNullOrBlank() } ?: "0",
                yearsFrom = yearsFrom.getOrNull(index).takeUnless { it.isNullOrBlank() } ?: "0",
                yearsTo = yearsTo.getOrNull(index).takeUnless { it.isNullOrBlank() } ?: "/"
            )
        }
    }


    private fun setAdapter(dataList: List<DiagnosisEntry>) {
        prescribedListLimitationsAdapter =
            PrescribedListLimitationsAdapter(dataList, parentFragmentManager)
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.rvLimitations.layoutManager = linearLayoutManager
        binding.rvLimitations.adapter = prescribedListLimitationsAdapter
    }
}