package com.example.zdravlje.ui.profile.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPhoneNumberBottomSheetBinding

class PhoneNumberFragmentBottomSheet : BottomSheetDialogFragment() {

    interface OnPhoneNumListener {
        fun onPhoneNumChange(phoneNumber: String)
    }

    private var listener: OnPhoneNumListener? = null

    fun setOnPhoneNumListener(listener: OnPhoneNumListener) {
        this.listener = listener
    }

    lateinit var binding: FragmentPhoneNumberBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhoneNumberBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mbConfirm.setOnClickListener(View.OnClickListener {
            val phoneNumber = binding.ietPhone.text.toString().trim()
            listener?.onPhoneNumChange(phoneNumber)
            dismiss()
        })

        binding.mbCancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}