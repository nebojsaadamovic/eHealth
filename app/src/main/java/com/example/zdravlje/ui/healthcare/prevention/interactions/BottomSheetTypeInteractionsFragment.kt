package com.example.zdravlje.ui.healthcare.prevention.interactions

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetTypeInteractionsBinding

class BottomSheetTypeInteractionsFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetTypeInteractionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetTypeInteractionsBinding.inflate(layoutInflater)
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
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }
}