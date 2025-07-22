package com.example.zdravlje.ui.login.info

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChooseCategoryQuestBottomSheetBinding

class ChooseCategoryQuestBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentChooseCategoryQuestBottomSheetBinding
    private var categoryListener: OnChooseCategoryListener? = null

    interface OnChooseCategoryListener {
        fun onChoseCategoryChange(categoryText: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentChooseCategoryQuestBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mbGDPR.setOnClickListener {
            categoryListener?.onChoseCategoryChange(getString(R.string.fragment_choose_category_quest_bottom_sheet_gdpr))
            dismiss()
        }
        binding.mbHelpDesk.setOnClickListener {
            categoryListener?.onChoseCategoryChange(getString(R.string.fragment_choose_category_quest_bottom_sheet_helpdesk))
            dismiss()
        }
        binding.mbRegistration.setOnClickListener {
            categoryListener?.onChoseCategoryChange(getString(R.string.fragment_choose_category_quest_bottom_sheet_registration))
            dismiss()
        }
        binding.mbClose.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    fun setOnChooseCategoryListener(listener: OnChooseCategoryListener) {
        categoryListener = listener
    }
}