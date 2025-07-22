package com.example.zdravlje.ui.profile.settings

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
import com.nites.ezdravljeapp.databinding.FragmentAdrressBottomSheetBinding

class AdrressFragmentBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentAdrressBottomSheetBinding
    private var listener: OnAddressListener? = null

    interface OnAddressListener {
        fun onAddressChange(address: String)
    }

    fun setOnAddressListener(listener: OnAddressListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdrressBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.ietAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !s.isNullOrEmpty()
                binding.mbConfirm.isEnabled = hasText
                binding.mbConfirm.alpha = if (hasText) 1.0f else 0.2f
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.mbConfirm.setOnClickListener(View.OnClickListener {
            val address = binding.ietAddress.text.toString().trim()
            listener?.onAddressChange(address)
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