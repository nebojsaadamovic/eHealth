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
import com.nites.ezdravljeapp.databinding.FragmentEnterEmailAndNameBottomSheetBinding

class EnterEmailAndNameBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentEnterEmailAndNameBottomSheetBinding
    var title: String =""
    var mbClose: String =""
    var mbAdd: String =""

    interface OnValueListener {
        fun OnValueChange(email: String, name:String)
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
        binding = FragmentEnterEmailAndNameBottomSheetBinding.inflate(layoutInflater)
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
            val email = binding.ietEmail.text.toString().trim()
            val name = binding.ietName.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Polje za e-mail ne može biti prazno.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                listener?.OnValueChange(email,name)
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}