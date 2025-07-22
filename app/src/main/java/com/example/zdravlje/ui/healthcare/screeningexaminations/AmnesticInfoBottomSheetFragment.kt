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
import com.nites.ezdravljeapp.databinding.FragmentAmnesticInfoBottomSheetBinding

class AmnesticInfoBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentAmnesticInfoBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAmnesticInfoBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mbCancel.setOnClickListener() {
            dismiss()
        }
        binding.mbSave.setOnClickListener() {
            Toast.makeText(
                requireContext(),
                getString(R.string.fragment_amnestic_info_bottom_sheet_data_saved_successfully),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}