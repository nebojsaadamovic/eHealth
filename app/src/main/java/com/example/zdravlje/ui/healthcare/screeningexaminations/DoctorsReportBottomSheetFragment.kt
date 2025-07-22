package com.example.zdravlje.ui.healthcare.screeningexaminations

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentDoctorsReportBottomSheetBinding

class DoctorsReportBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentDoctorsReportBottomSheetBinding

    interface OnDoctorsReportListener {
        fun onDoctorsReportChange(report: String)
    }

    private var listener: OnDoctorsReportListener? = null
    private var fieldTag: String? = null

    fun setOnDoctorsReportsListener(listener: OnDoctorsReportListener, fieldTag: String) {
        this.listener = listener
        this.fieldTag = fieldTag
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoctorsReportBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.ietName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !s.isNullOrEmpty()
                binding.mbConfirm.isEnabled = hasText
                binding.mbConfirm.alpha = if (hasText) 1.0f else 0.2f
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.mbConfirm.setOnClickListener() {
            val value = binding.ietName.text.toString().trim()
            if (value.isEmpty()) {
                listener?.onDoctorsReportChange(getString(R.string.fragment_doctors_report_bottom_sheet_enter_data))
                dismiss()
            } else {
                listener?.onDoctorsReportChange(value)
                dismiss()
            }
        }

        binding.mbCancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}