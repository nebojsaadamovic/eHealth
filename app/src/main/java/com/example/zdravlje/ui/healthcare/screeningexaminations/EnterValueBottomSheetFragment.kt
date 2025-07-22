package com.example.zdravlje.ui.healthcare.screeningexaminations

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentEnterValueBottomSheetBinding

class EnterValueBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentEnterValueBottomSheetBinding
    var title: String =""
    var mbClose: String =""
    var mbAdd: String =""

    interface OnValueListener {
        fun OnValueChange(value: String)
    }

    private var listener: OnValueListener? = null
    private var fieldTag: String? = null

    fun setOnValueListener(listener: OnValueListener, fieldTag: String) {
        this.listener = listener
        this.fieldTag = fieldTag
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterValueBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = getString(R.string.fragment_enter_value_bottomsheet_title)
        mbClose = getString(R.string.fragment_enter_value_bottomsheet_mbclose)
        mbAdd = getString(R.string.fragment_enter_value_bottomsheet_mbadd)
        setupUI()
    }

    private fun setupUI() {
        if (fieldTag == "ScheduleCallParticipiantFragment") {
            binding.mtvTitleText.text = title
            binding.mbClose.text = mbClose
            binding.mbConfirm.text = mbAdd
        }

        binding.mbClose.setOnClickListener() {
            dismiss()
        }
        binding.mbConfirm.setOnClickListener() {
            val value = binding.etiValue.text.toString().trim()
            if (value.isEmpty()) {
                listener?.OnValueChange(getString(R.string.fragment_enter_value_bottom_sheet_select))
                dismiss()
            } else {
                listener?.OnValueChange(value)
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}