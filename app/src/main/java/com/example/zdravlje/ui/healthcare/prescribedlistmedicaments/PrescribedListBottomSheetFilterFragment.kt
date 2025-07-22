package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPrescribedListBottomSheetFilterBinding

class PrescribedListBottomSheetFilterFragment : BaseBottomSheetDialogFragment() {

    lateinit var binding: FragmentPrescribedListBottomSheetFilterBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var listener: OnFilterListener? = null

    interface OnFilterListener {
        fun onFilterChange()
    }

    fun setOnFilterListener(listener: OnFilterListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrescribedListBottomSheetFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
        restoreSelectedFilters()
    }

    private fun setupUI() {
        binding.mbClose.setOnClickListener() {
            dismiss()
        }

        binding.mbConfirm.setOnClickListener {
            val selectedStatusId = binding.statusRadioGroup.checkedRadioButtonId
            val selectedTypeRecipeId = binding.typeRecipeRadioGroup.checkedRadioButtonId

            val selectedStatusRadioButton: RadioButton? =
                if (selectedStatusId != -1) binding.statusRadioGroup.findViewById(selectedStatusId) else null

            val selectedTypeRecipeRadioButton: RadioButton? =
                if (selectedTypeRecipeId != -1) binding.typeRecipeRadioGroup.findViewById(selectedTypeRecipeId) else null

            val statusValue: Int? = selectedStatusRadioButton?.tag?.toString()?.toIntOrNull()
            val typeValue: Int? = selectedTypeRecipeRadioButton?.tag?.toString()?.toIntOrNull()

            healthcareViewModel.statusOll.value = statusValue
            healthcareViewModel.typeOll.value = typeValue

            listener?.onFilterChange()
            dismiss()
        }
    }

    private fun restoreSelectedFilters() {
        val selectedStatus = healthcareViewModel.statusOll.value
        val selectedType = healthcareViewModel.typeOll.value

        selectedStatus?.let { status ->
            for (i in 0 until binding.statusRadioGroup.childCount) {
                val radioButton = binding.statusRadioGroup.getChildAt(i) as? RadioButton
                if (radioButton?.tag?.toString()?.toIntOrNull() == status) {
                    radioButton.isChecked = true
                    break
                }
            }
        }

        selectedType?.let { type ->
            for (i in 0 until binding.typeRecipeRadioGroup.childCount) {
                val radioButton = binding.typeRecipeRadioGroup.getChildAt(i) as? RadioButton
                if (radioButton?.tag?.toString()?.toIntOrNull() == type) {
                    radioButton.isChecked = true
                    break
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.let {
            val bottomSheet = it.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isFitToContents = false
            behavior.expandedOffset = 0
        }
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(
            requireActivity(), healthcareViewModelFactory
        ).get(HealthcareViewModel::class.java)
    }
}