package com.example.zdravlje.ui.calendar.apointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nites.ezdravljeapp.databinding.FragmentReviewEnteredDataBinding

class ReviewEnteredDataFragment(
    private val from: String,
    private val to: String,
    private val date: String,
    private val familyDoctor: String,
    private val institution: String,
    private val typeExamination: String,
    private val timeslotId: String,
    private val  className: String?=null
) : Fragment() {

    lateinit var binding: FragmentReviewEnteredDataBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewEnteredDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mtvDateValue.text = date
        binding.mtvTermsValue.text = "${from} - ${to}"
        binding.mtvDoctorValue.text = familyDoctor
        binding.mtvInstitutionValue.text = institution
        binding.mtvTypeExaminationValue.text = typeExamination

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.mbScheduleExamination.setOnClickListener() {
            val bottomSheetDialogConfirmFragment = BottomSheetDialogConfirmFragment(timeslotId, className)
            bottomSheetDialogConfirmFragment.show(
                parentFragmentManager,
                bottomSheetDialogConfirmFragment.tag
            )
        }
    }
}