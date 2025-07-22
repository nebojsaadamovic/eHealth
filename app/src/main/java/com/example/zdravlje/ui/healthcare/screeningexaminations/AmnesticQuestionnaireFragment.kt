package com.example.zdravlje.ui.healthcare.screeningexaminations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.databinding.FragmentAmnesticQuestionnaireBinding

class AmnesticQuestionnaireFragment : Fragment(),
    DoctorsReportBottomSheetFragment.OnDoctorsReportListener,
    EnterValueBottomSheetFragment.OnValueListener,
    MenstrualCycleBottomSheetFragment.OnValueListener,
    SelectValueBottomSheetFragment.OnValueListener,
    BreastBottomSheetFragment.OnValueListener,
    SelectValueFiveMoreBottomSheetFragment.OnValueListener,
    StopMenstruationBottomSheetFragment.OnValueListener {

    lateinit var binding: FragmentAmnesticQuestionnaireBinding
    private var currentFieldTag: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAmnesticQuestionnaireBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mtvFirstMenstruationValue.setOnClickListener() {
            currentFieldTag = "firstMenstruation"
            openEnterValueBottomSheet()
        }

        binding.mtvYearsLastMenstruationValue.setOnClickListener() {
            currentFieldTag = "yearsLastMenstruation"
            openEnterValueBottomSheet()
        }

        binding.mtvFirstPregnancyValue.setOnClickListener() {
            currentFieldTag = "firstPregnancyValue"
            openEnterValueBottomSheet()
        }
        binding.mtvNumPregnanciesValue.setOnClickListener() {
            currentFieldTag = "numPregnanciesValue"
            openEnterValueBottomSheet()
        }
        binding.mtvFistChildValue.setOnClickListener() {
            currentFieldTag = "fistChildValue"
            openEnterValueBottomSheet()
        }

        binding.mtvYearsSmokingValue.setOnClickListener() {
            currentFieldTag = "yearsSmokingValue"
            openEnterValueBottomSheet()
        }

        binding.mtvHormoneTherapyValue.setOnClickListener() {
            currentFieldTag = "hormoneTherapyValue"
            openEnterValueBottomSheet()
        }

        binding.mtvYearsHormoneTherapyValue.setOnClickListener() {
            currentFieldTag = "yearsHormoneTherapyValue"
            openEnterValueBottomSheet()
        }

        binding.mtvMenstrualCycleValue.setOnClickListener {
            currentFieldTag = "menstrualCycleValue"
            openMenstrualCycleBottomSheet()
        }

        binding.mtvReasonCessationMenstruationValue.setOnClickListener {
            currentFieldTag = "reasonCessationMenstruationValue"
            stopMenstruationBottomSheet()
        }

        binding.mtvMatherOvariesValue.setOnClickListener() {
            currentFieldTag = "matherOvariesValue"
            openSelectValueBottomSheet()
        }

        binding.mtvSistersValueOvaries.setOnClickListener() {
            currentFieldTag = "sistersValueOvaries"
            openSelectValueBottomSheet()
        }

        binding.mtvDaughterOvariesValue.setOnClickListener() {
            currentFieldTag = "daughterOvariesValue"
            openSelectValueBottomSheet()
        }

        binding.mtvGrandmaOvariesValue.setOnClickListener() {
            currentFieldTag = "grandmaOvariesValue"
            openSelectValueBottomSheet()
        }

        binding.mtvAuntOvariesValue.setOnClickListener() {
            currentFieldTag = "auntOvariesValue"
            openSelectValueBottomSheet()
        }

        binding.mtvMatherBreastValue.setOnClickListener() {
            currentFieldTag = "matherBreastValue"
            openSelectValueBottomSheet()
        }

        binding.mtvSistersValueBreast.setOnClickListener() {
            currentFieldTag = "sistersValueBreast"
            openSelectValueBottomSheet()
        }

        binding.mtvDaughterBreastValue.setOnClickListener() {
            currentFieldTag = "daughterBreastValue"
            openSelectValueBottomSheet()
        }

        binding.mtvGrandmaBreastValue.setOnClickListener() {
            currentFieldTag = "grandmaBreastValue"
            openSelectValueBottomSheet()
        }

        binding.mtvAuntBreastValue.setOnClickListener() {
            currentFieldTag = "auntBreastValue"
            openSelectValueBottomSheet()
        }

        binding.mtvYearsValue.setOnClickListener() {
            currentFieldTag = "yearsValue"
            openSelectValueBottomSheet()
        }

        binding.mtvTotalMammographyValue.setOnClickListener {
            currentFieldTag = "totalMammographyValue"
            openSelectValueFiveMoreBottomSheet()
        }

        binding.mtvPeinValue.setOnClickListener {
            currentFieldTag = "mtvPeinValue"
            openBreastBottomSheet()

        }

        binding.mtvKnotValue.setOnClickListener {
            currentFieldTag = "knotValue"
            openBreastBottomSheet()
        }

        binding.mtvDischargesValue.setOnClickListener {
            currentFieldTag = "dischargesValue"
            openBreastBottomSheet()
        }

        binding.mtvTheRestValue.setOnClickListener {
            currentFieldTag = "theRestValue"
            openBreastBottomSheet()
        }

        binding.mtvTracerValue.setOnClickListener {
            currentFieldTag = "tracerValue"
            openBreastBottomSheet()
        }

        binding.mtvOperationValues.setOnClickListener {
            currentFieldTag = "operationValues"
            openBreastBottomSheet()
        }

        binding.mtvBiopsyValue.setOnClickListener {
            currentFieldTag = "biopsyValue"
            openBreastBottomSheet()
        }

        binding.mtvImplantsValue.setOnClickListener {
            currentFieldTag = "implantsValue"
            openBreastBottomSheet()
        }

        binding.mtvDoctorsReportValue.setOnClickListener {
            currentFieldTag = "doctorsReportValue"
            doctorsReportBottomSheet()
        }
        binding.mtvPreviousBreastBiopsyValue.setOnClickListener {
            currentFieldTag = "previousBreastBiopsyValue"
            doctorsReportBottomSheet()
        }

        binding.mtvDateLastMammographyValue.setOnClickListener() {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateLastMammographyValue
            ) { selectedDate ->
                binding.mtvDateLastMammographyValue.text = selectedDate
            }
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.mtvSave.setOnClickListener() {
            val amnesticInfoBottomSheetFragment = AmnesticInfoBottomSheetFragment()
            amnesticInfoBottomSheetFragment.show(
                parentFragmentManager,
                amnesticInfoBottomSheetFragment.tag
            )
        }
    }

    override fun onDoctorsReportChange(report: String) {
        when (currentFieldTag) {
            "doctorsReportValue" -> binding.mtvDoctorsReportValue.text = report
            "previousBreastBiopsyValue" -> binding.mtvPreviousBreastBiopsyValue.text = report
        }
    }

    override fun OnValueChange(value: String) {
        when (currentFieldTag) {
            "firstMenstruation" -> binding.mtvFirstMenstruationValue.text = value
            "yearsLastMenstruation" -> binding.mtvYearsLastMenstruationValue.text = value
            "firstPregnancyValue" -> binding.mtvFirstPregnancyValue.text = value
            "numPregnanciesValue" -> binding.mtvNumPregnanciesValue.text = value
            "fistChildValue" -> binding.mtvFistChildValue.text = value
            "yearsSmokingValue" -> binding.mtvYearsSmokingValue.text = value
            "hormoneTherapyValue" -> binding.mtvHormoneTherapyValue.text = value
            "yearsHormoneTherapyValue" -> binding.mtvYearsHormoneTherapyValue.text = value
            "menstrualCycleValue" -> binding.mtvMenstrualCycleValue.text = value
            "matherOvariesValue" -> binding.mtvMatherOvariesValue.text = value
            "sistersValueOvaries" -> binding.mtvSistersValueOvaries.text = value
            "daughterOvariesValue" -> binding.mtvDaughterOvariesValue.text = value
            "grandmaOvariesValue" -> binding.mtvGrandmaOvariesValue.text = value
            "auntOvariesValue" -> binding.mtvAuntOvariesValue.text = value
            "matherBreastValue" -> binding.mtvMatherBreastValue.text = value
            "sistersValueBreast" -> binding.mtvSistersValueBreast.text = value
            "daughterBreastValue" -> binding.mtvDaughterBreastValue.text = value
            "grandmaBreastValue" -> binding.mtvGrandmaBreastValue.text = value
            "auntBreastValue" -> binding.mtvAuntBreastValue.text = value
            "yearsValue" -> binding.mtvYearsValue.text = value
            "peinValue" -> binding.mtvPeinValue.text = value
            "knotValue" -> binding.mtvKnotValue.text = value
            "dischargesValue" -> binding.mtvDischargesValue.text = value
            "theRestValue" -> binding.mtvTheRestValue.text = value
            "tracerValue" -> binding.mtvTracerValue.text = value
            "operationValues" -> binding.mtvOperationValues.text = value
            "biopsyValue" -> binding.mtvBiopsyValue.text = value
            "implantsValue" -> binding.mtvImplantsValue.text = value
            "totalMammographyValue" -> binding.mtvTotalMammographyValue.text = value
            "reasonCessationMenstruationValue" -> binding.mtvReasonCessationMenstruationValue.text = value
            "doctorsReportValue" -> binding.mtvDoctorsReportValue.text = value
            "previousBreastBiopsyValue" -> binding.mtvPreviousBreastBiopsyValue.text = value
        }
    }

    private fun openEnterValueBottomSheet() {
        val enterValueBottomSheetFragment = EnterValueBottomSheetFragment()
        enterValueBottomSheetFragment.setOnValueListener(this, currentFieldTag ?: "")
        enterValueBottomSheetFragment.show(parentFragmentManager, enterValueBottomSheetFragment.tag)
    }

    private fun openMenstrualCycleBottomSheet() {
        val menstrualCycleBottomSheet = MenstrualCycleBottomSheetFragment()
        menstrualCycleBottomSheet.setOnValueListener(this, currentFieldTag ?: "")
        menstrualCycleBottomSheet.show(parentFragmentManager, menstrualCycleBottomSheet.tag)
    }

    private fun openSelectValueBottomSheet() {
        val selectValueBottomSheetFragment = SelectValueBottomSheetFragment()
        selectValueBottomSheetFragment.setOnValueListener(this, currentFieldTag ?: "")
        selectValueBottomSheetFragment.show(
            parentFragmentManager,
            selectValueBottomSheetFragment.tag
        )
    }

    private fun openBreastBottomSheet() {
        val breastBottomSheetFragment = BreastBottomSheetFragment()
        breastBottomSheetFragment.setOnValueListener(this, currentFieldTag ?: "")
        breastBottomSheetFragment.show(parentFragmentManager, breastBottomSheetFragment.tag)
    }


    private fun openSelectValueFiveMoreBottomSheet() {
        val selectValueFiveMoreBottomSheetFragment = SelectValueFiveMoreBottomSheetFragment()
        selectValueFiveMoreBottomSheetFragment.setOnValueListener(this, currentFieldTag ?: "")
        selectValueFiveMoreBottomSheetFragment.show(
            parentFragmentManager, selectValueFiveMoreBottomSheetFragment.tag
        )
    }

    private fun stopMenstruationBottomSheet() {
        val stopMenstruationBottomSheetFragment = StopMenstruationBottomSheetFragment()
        stopMenstruationBottomSheetFragment.setOnValueListener(this, currentFieldTag ?: "")
        stopMenstruationBottomSheetFragment.show(
            parentFragmentManager,
            stopMenstruationBottomSheetFragment.tag
        )
    }

    private fun doctorsReportBottomSheet() {
        val doctorsReportBottomSheetFragment = DoctorsReportBottomSheetFragment()
        doctorsReportBottomSheetFragment.setOnDoctorsReportsListener(this, currentFieldTag ?: "")
        doctorsReportBottomSheetFragment.show(
            parentFragmentManager,
            doctorsReportBottomSheetFragment.tag
        )
    }
}