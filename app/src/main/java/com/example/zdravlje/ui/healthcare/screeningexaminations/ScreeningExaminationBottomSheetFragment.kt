package com.example.zdravlje.ui.healthcare.screeningexaminations

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentScreeningExaminationBottomSheetBinding

class ScreeningExaminationBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentScreeningExaminationBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreeningExaminationBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mbClose.setOnClickListener() {
            dismiss()
        }
        binding.mbConfirm.setOnClickListener() {
            Toast.makeText(
                requireContext(),
                getString(R.string.fragment_screening_examination_bottom_sheet_scheduled_screening_examination),
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}