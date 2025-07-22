package com.example.zdravlje.ui.notification

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zdravlje.data.model.response.notification.EventPayload
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentInfoBottomSheetBinding

class InfoBottomSheetFragment(
    private var eventPayload: EventPayload?
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentInfoBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mtvTitle.text = eventPayload?.event?.eveTitle
        binding.mtvDescription.text = eventPayload?.message

        binding.mbClose.setOnClickListener() {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}