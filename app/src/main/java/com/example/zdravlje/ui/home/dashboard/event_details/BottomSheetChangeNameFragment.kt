package com.example.zdravlje.ui.home.dashboard.event_details

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
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetChangeNameBinding

class BottomSheetChangeNameFragment : BottomSheetDialogFragment() {

    interface OnNameListener {
        fun onNameChange(name: String, title: String)
    }

    private var listener: OnNameListener? = null
    private var title: String? = null

    fun setOnNameListener(listener: OnNameListener, title: String) {
        this.listener = listener
        this.title = title
    }

    lateinit var binding: FragmentBottomSheetChangeNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetChangeNameBinding.inflate(layoutInflater)
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

        binding.mbConfirm.setOnClickListener(View.OnClickListener {
            val name = binding.ietName.text.toString().trim()
            listener?.onNameChange(name, title ?: "")
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